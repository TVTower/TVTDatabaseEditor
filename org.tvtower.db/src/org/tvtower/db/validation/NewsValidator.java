package org.tvtower.db.validation;

import static org.tvtower.db.validation.CommonValidation.isUserDB;

import java.math.BigDecimal;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.constants.EffectType;
import org.tvtower.db.constants.NewsFlag;
import org.tvtower.db.constants.NewsType;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.Effect;
import org.tvtower.db.database.NewsData;
import org.tvtower.db.database.NewsItem;

import com.google.common.base.Strings;

public class NewsValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;
	private static final BigDecimal VALUE_MIN = new BigDecimal("-3");
	private static final BigDecimal VALUE_MAX = new BigDecimal("3");

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkNewsType(NewsItem item) {
		Constants.newsType.isValidValue(item.getType(), "type", true).ifPresent(e -> error(e, $.getNewsItem_Type()));
		if (isUserDB(item) && Strings.isNullOrEmpty(item.getCreatedBy())) {
			error("created_by must be defined", $.getMayContainVariables_Name());
		}
		if (item.getModifiers() != null) {
			error("modifiers used", $.getNewsItem_Modifiers());
		}
	}

	@Check
	public void newsData(NewsData data) {
		Constants.newsGenre.isValidValue(data.getGenre(), "genre", true)
				.ifPresent(e -> error(e, $.getNewsData_Genre()));
		Constants._boolean.isValidValue(data.getFictional(), "fictional", false)
				.ifPresent(e -> error(e, $.getNewsData_Fictional()));
		Constants._boolean.isValidValue(data.getAvailable(), "available", false)
				.ifPresent(e -> error(e, $.getNewsData_Available()));
		CommonValidation.getDecimalRangeError(data.getPrice(), "price", BigDecimal.ZERO, BigDecimal.TEN, true)
				.ifPresent(e -> error(e, $.getNewsData_Price()));

		CommonValidation.getIntRangeError(data.getQuality(), "quality", 0, 100, false)
				.ifPresent(e -> error(e, $.getNewsData_Quality()));
		CommonValidation.getIntRangeError(data.getQualityMin(), "quality_min", 0, 100, false)
				.ifPresent(e -> error(e, $.getNewsData_QualityMin()));
		CommonValidation.getIntRangeError(data.getQualityMax(), "quality_max", 0, 100, false)
				.ifPresent(e -> error(e, $.getNewsData_QualityMax()));
		CommonValidation.getMinMaxError(data.getQualityMin(), data.getQualityMax())
				.ifPresent(e -> error(e, $.getNewsData_QualityMin()));
		CommonValidation.getIntRangeError(data.getQualitySlope(), "quality_slope", 0, 100, false)
				.ifPresent(e -> error(e, $.getNewsData_QualitySlope()));
		CommonValidation.getValueMissingError("quality", data.getQuality(), data.getQualityMin(), data.getQualityMax())
				.ifPresent(e -> error(e, $.getNewsData_Quality()));

		CommonValidation.getIntRangeError(data.getSubscriptionLevel(), "min_subscription_level", 1, 3, false)
				.ifPresent(e -> error(e, $.getNewsData_SubscriptionLevel()));
		CommonValidation.getTimeError(data.getHappenTime(), "happentime")
				.ifPresent(e -> error(e, $.getNewsData_HappenTime()));
		Constants.newsFlag.isValidFlag(data.getFlags(), "flags", false).ifPresent(e -> error(e, $.getNewsData_Flags()));

		if (!Strings.isNullOrEmpty(data.getQuality())) {
			assertNotSet(data.getQualityMin(), $.getNewsData_QualityMin());
			assertNotSet(data.getQualityMax(), $.getNewsData_QualityMax());
			assertNotSet(data.getQualitySlope(), $.getNewsData_QualitySlope());
		} else if (!Strings.isNullOrEmpty(data.getQualityMin()) || !Strings.isNullOrEmpty(data.getQualityMax())) {
			assertNotSet(data.getQuality(), $.getNewsData_Quality());
		}
	}

	private void assertNotSet(String value, EStructuralFeature f) {
		if (!Strings.isNullOrEmpty(value)) {
			error("value must not be set", f);
		}
	}

	@Check
	public void threadIdPresent(NewsItem item) {
		if (hasNewsTrigger(item)) {
			if (Strings.isNullOrEmpty(item.getThreadId())) {
				warning("news with trigger should have a thread id", $.getMayContainVariables_Name());
			}
		}
	}

	private boolean hasNewsTrigger(NewsItem item) {
		if (item.getEffects() != null && !item.getEffects().getEffects().isEmpty()) {
			return item.getEffects().getEffects().stream()
					.anyMatch(e -> Constants.effectType.isNewsTrigger(e.getType()));
		}
		return false;
	}

	@Check
	public void triggeredNewsSameThread(NewsItem item) {
		if (item.getEffects() != null) {
			String thread = item.getThreadId();
			if (thread != null) {
				item.getEffects().getEffects().stream().filter(e->Constants.effectType.isNewsTrigger(e.getType()))
				.forEach(e -> {
					checkTriggeredNews(item, e, e.getNews(), $.getEffect_News());
					checkTriggeredNews(item, e, e.getNews1(), $.getEffect_News1());
					checkTriggeredNews(item, e, e.getNews2(), $.getEffect_News2());
					checkTriggeredNews(item, e, e.getNews3(), $.getEffect_News3());
					checkTriggeredNews(item, e, e.getNews4(), $.getEffect_News4());
				});
			}
		}
	}

	private void checkTriggeredNews(NewsItem parentNews, Effect e, NewsItem triggered, EStructuralFeature feature) {
		if(triggered.eIsProxy()) {
			return;
		}
		String triggeredThread = triggered.getThreadId();
		if (!parentNews.getThreadId().equals(triggeredThread)) {
			warning("triggered news should belong to the same thread", e, feature);
		}
		if (parentNews.getData() != null && parentNews.getData().getGenre() != null && triggered.getData() != null) {
			if (!parentNews.getData().getGenre().equals(triggered.getData().getGenre())) {
				warning("genre mismatch with triggering news", triggered.getData(), $.getNewsData_Genre());
			}
		}
		if (!NewsType.FOLLOW_UP_NEWS.equals(triggered.getType())) {
			if (hasNewsTrigger(triggered) && triggered == parentNews) {
				// self-triggered can have 0
				return;
			}
			error("triggered news must have type " + NewsType.FOLLOW_UP_NEWS, triggered,
					$.getMayContainVariables_Name());
		}
		NewsFlag newsFlags = Constants.newsFlag;
		if(triggered != parentNews && !newsFlags.hasFlag(parentNews.getData().getFlags(), newsFlags.INVISIBLE)) {
			if(triggered.getVariables()!=null) {
				error("variables in triggered news will cause problems", triggered,
						$.getMayContainVariables_Variables());
			}
		}
	}

	@Check
	public void checkEffect(Effect e) {
		Constants.triggerType.isValidValue(e.getTrigger(), "trigger", true)
				.ifPresent(err -> error(err, $.getEffect_Trigger()));
		Constants.effectType.isValidValue(e.getType(), "type", true).ifPresent(err -> error(err, $.getEffect_Type()));
		Constants.programmGenre.isValidValue(e.getGenre(), "genre", false)
				.ifPresent(err -> error(err, $.getEffect_Genre()));
		CommonValidation.getDecimalRangeError(e.getValueMin(), "valueMin", VALUE_MIN, VALUE_MAX, false)
				.ifPresent(err -> error(err, $.getEffect_ValueMin()));
		CommonValidation.getDecimalRangeError(e.getValueMax(), "valueMax", VALUE_MIN, VALUE_MAX, false)
				.ifPresent(err -> error(err, $.getEffect_ValueMax()));
		CommonValidation.getIntRangeError(e.getProbability(), "probability", 0, 100, false)
				.ifPresent(err -> error(err, $.getEffect_Probability()));
		CommonValidation.getIntRangeError(e.getProbability1(), "probability1", 0, 100, false)
				.ifPresent(err -> error(err, $.getEffect_Probability1()));
		CommonValidation.getIntRangeError(e.getProbability2(), "probability2", 0, 100, false)
				.ifPresent(err -> error(err, $.getEffect_Probability2()));
		CommonValidation.getIntRangeError(e.getProbability3(), "probability3", 0, 100, false)
				.ifPresent(err -> error(err, $.getEffect_Probability3()));
		CommonValidation.getIntRangeError(e.getProbability4(), "probability4", 0, 100, false)
				.ifPresent(err -> error(err, $.getEffect_Probability4()));
		Constants._boolean.isValidValue(e.getEnable(), "enable", false)
				.ifPresent(err -> error(err, $.getEffect_Enable()));

		if (e.getType() != null) {
			boolean checkMinMax = false;
			boolean genreExpected = false;
			boolean refExpected = false;
			boolean choiceExpected = false;
			boolean enableExpected = false;
			boolean newsExpected = false;

			switch (e.getType()) {
			case EffectType.NEWS:
				newsExpected = true;
				break;
			case EffectType.NEWS_CHOICE:
				choiceExpected = true;
				break;
			case EffectType.PERSON:
				checkMinMax = true;
				refExpected = true;
				break;
			case EffectType.GENRE:
				checkMinMax = true;
				genreExpected = true;
				break;
			case EffectType.NEWS_AVAILABILITY:
				newsExpected = true;
				enableExpected = true;
				break;
			default:
				break;
			}
			effectTypeField("valueMin", e.getValueMin(), checkMinMax);
			effectTypeField("valueMax", e.getValueMin(), checkMinMax);
			CommonValidation.getMinMaxError(e.getValueMin(), e.getValueMax())
					.ifPresent(err -> error(err, $.getEffect_ValueMin()));
			effectTypeField("genre", e.getGenre(), genreExpected);
			effectTypeField("choice", e.getChoose(), choiceExpected);
			effectTypeField("enable", e.getEnable(), enableExpected);
			referenceField("reference", e.getRefs(), refExpected);
			referenceField("news", e.getNews(), newsExpected);

			// check choices
			if (e.getChoose() != null) {
				if (!"or".equals(e.getChoose())) {
					error("unsupported choose operator", $.getEffect_Choose());
				} else {
					if (e.getNews() != null) {
						error("for choices use news<X> values", $.getEffect_News());
					}
				}
			} else {
				assertChoiceValueNotSet(e.getProbability(), $.getEffect_Probability());
				assertChoiceValueNotSet(e.getNews1(), $.getEffect_News1());
				assertChoiceValueNotSet(e.getNews2(), $.getEffect_News2());
				assertChoiceValueNotSet(e.getNews3(), $.getEffect_News3());
				assertChoiceValueNotSet(e.getNews4(), $.getEffect_News4());
				assertChoiceValueNotSet(e.getProbability1(), $.getEffect_Probability1());
				assertChoiceValueNotSet(e.getProbability2(), $.getEffect_Probability2());
				assertChoiceValueNotSet(e.getProbability3(), $.getEffect_Probability3());
				assertChoiceValueNotSet(e.getProbability4(), $.getEffect_Probability4());
			}
			// TODO time not always needed
			CommonValidation.getTimeError(e.getTime(), "time").ifPresent(err -> error(err, $.getEffect_Time()));
		} else {
			error("effect must have type", $.getEffect_Type());
		}
	}

	private void assertChoiceValueNotSet(Object value, EStructuralFeature f) {
		if (value != null) {
			error("without choose this value must not be set", f);
		}
	}

	private void referenceField(String field, Object value, boolean expected) {
		if(expected && value ==null) {
			error(field+ " expected for this effect type", $.getEffect_Type());
		}else if(!expected&& value!=null) {
			error(field + " not allowed for this effect type", $.getEffect_Type());
		}
	}

	private void effectTypeField(String field, String fieldValue, boolean expected) {
		boolean present = !Strings.isNullOrEmpty(fieldValue);
		if (expected && !present) {
			error(field + " expected for this effect type", $.getEffect_Type());
		} else if (!expected && present) {
			error(field + " not allowed for this effect type", $.getEffect_Type());
		}
	}
}
