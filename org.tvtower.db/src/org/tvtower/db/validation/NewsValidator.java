package org.tvtower.db.validation;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.NewsConstants;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.Effect;
import org.tvtower.db.database.NewsData;
import org.tvtower.db.database.NewsItem;

import com.google.common.base.Strings;

//TODO validate time format
//TODO validate price, quality, flags
//TODO validate genre values
public class NewsValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkNewsType(NewsItem item) {
		if (!NewsConstants.isValidNewsType(item.getType())) {
			error("invalid news type ", $.getNewsItem_Type());
		}
	}

	@Check
	public void newsData(NewsData data) {
		if (data.getGenre() != null) {
			if (!NewsConstants.isNewsGenre(data.getGenre())) {
				error("invalid genre", $.getNewsData_Genre());
			}
		}
		if (data.getFictional() != null) {
			String lower = data.getFictional().toLowerCase();
			if (!"true".equals(lower) && !"false".equals(lower)) {
				error("invalid fictional value", $.getNewsData_Fictional());
			}
		}
	}

	@Check
	public void threadIdPresent(NewsItem item) {
		if (hasNewsTrigger(item)) {
			if (Strings.isNullOrEmpty(item.getThreadId())) {
				warning("news with trigger should have a thread id", $.getNewsItem_Name());
			}
		}
	}

	private boolean hasNewsTrigger(NewsItem item) {
		if (item.getEffects() != null && !item.getEffects().getEffects().isEmpty()) {
			return item.getEffects().getEffects().stream()
					.anyMatch(e -> e.getType() != null && e.getType().startsWith("triggernews"));
		}
		return false;
	}

	@Check
	public void triggeredNewsSameThread(NewsItem item) {
		if (item.getEffects() != null) {
			String thread = item.getThreadId();
			if (thread != null) {
				item.getEffects().getEffects().forEach(e -> {
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
		String triggeredThread = triggered.getThreadId();
		if (!parentNews.getThreadId().equals(triggeredThread)) {
			warning("triggered news must belong to the same thread", e, feature);
		}
		if (parentNews.getData() != null && parentNews.getData().getGenre() != null && triggered.getData() != null) {
			if (!parentNews.getData().getGenre().equals(triggered.getData().getGenre())) {
				warning("genre mismatch with triggering news", triggered.getData(), $.getNewsData_Genre());
			}
		}
		if (!NewsConstants.TYPE_FOLLOWUP_NEWS.equals(triggered.getType())) {
			if (hasNewsTrigger(triggered) && triggered == parentNews) {
				// self-triggered can have 0
				return;
			}
			error("triggered news must have type 2", triggered, $.getNewsItem_Name());
		}
	}

	@Check
	public void checkEffect(Effect e) {
		if (e.getTrigger() == null) {
			error("effect must have a (happen)-trigger", $.getEffect_Trigger());
		} else {
			if (!"happen".equals(e.getTrigger())) {
				error("unsupported effect trigger - only 'happen' allowed", $.getEffect_Trigger());
			}
		}
		if (e.getType() != null) {
			boolean checkMinMax = false;
			boolean genreExpected = false;
			boolean refExpected = false;
			boolean choiceExpected = false;
			switch (e.getType()) {
			case "triggernews":
				break;
			case "triggernewschoice":
				choiceExpected = true;
				break;
			case "modifyPersonPopularity":
				checkMinMax = true;
				refExpected = true;
				break;
			case "modifyMovieGenrePopularity":
				checkMinMax = true;
				genreExpected = true;
				break;
			default:
				error("unsupported trigger type", $.getEffect_Type());
			}
			effectTypeField("valueMin", e.getValueMin(), checkMinMax);
			effectTypeField("valueMax", e.getValueMin(), checkMinMax);
			effectTypeField("genre", e.getGenre(), genreExpected);
			effectTypeField("choice", e.getChoose(), choiceExpected);
			effectTypeField("reference", e.getRefs(), refExpected);

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
		} else {
			error("effect must have type", $.getEffect_Type());
		}
	}

	private void assertChoiceValueNotSet(Object value, EStructuralFeature f) {
		if (value != null) {
			error("without choose this value must not be set", f);
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
