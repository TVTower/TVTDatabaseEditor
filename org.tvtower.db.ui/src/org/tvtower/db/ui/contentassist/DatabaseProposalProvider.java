/*
 * generated by Xtext 2.23.0
 */
package org.tvtower.db.ui.contentassist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.ui.editor.contentassist.PrefixMatcher;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.constants.TVTEnum;
import org.tvtower.db.constants.TVTFlag;
import org.tvtower.db.database.GroupAttractivity;

/**
 * See
 * https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#content-assist
 * on how to customize the content assistant.
 */
public class DatabaseProposalProvider extends AbstractDatabaseProposalProvider {

	private static final PrefixMatcher tagPrefixMatcher=new PrefixMatcher() {
		public boolean isCandidateMatchingPrefix(String name, String prefix) {
			return name.contains(prefix);
		};
	};

	private void mapProposal(TVTEnum tvtEnum, ICompletionProposalAcceptor acceptor, ContentAssistContext context) {
		AtomicInteger index = new AtomicInteger(500);
		tvtEnum.forContentAssist().forEach((k, v) -> {
			acceptor.accept(createCompletionProposal("\"" + k + "\"", new StyledString(k + " - " + v), null,
					index.decrementAndGet(), context.getPrefix(), context));
		});
	}

	private void flagProposal(TVTFlag tvtFlag, ICompletionProposalAcceptor acceptor, ContentAssistContext context) {
		tvtFlag.forContentAssist().forEach((k, v) -> {
			acceptor.accept(createCompletionProposal(context.getPrefix(), new StyledString(k + " - " + v), null,
					-k.intValue(), context.getPrefix(), context));
		});
	}

	//simple tag proposal: self-closing tag set cursor position one space after the tag name
	private void oneLineTagProposal(String tag, ICompletionProposalAcceptor acceptor, ContentAssistContext context) {
		ContentAssistContext newContext = context.copy().setMatcher(tagPrefixMatcher).toContext();
		ICompletionProposal proposal = createCompletionProposal("<"+tag +"  />", newContext);
		if(proposal!=null) {
			((ConfigurableCompletionProposal)proposal).shiftOffset(-3);
			acceptor.accept(proposal);
		}
	}

	// Achievement-------------
	@Override
	public void completeAchievementData_Flags(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.achievementFlag, acceptor, context);
	}

	@Override
	public void completeAchievementData_Category(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.achievementCategory, acceptor, context);
	}

	@Override
	public void completeTaskData_Type(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.taskType, acceptor, context);
	}

	@Override
	public void completeTaskData_Genre1(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeTaskData_Genre2(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeTaskData_Genre3(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeRewardData_Type(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.rewardType, acceptor, context);
	}

	@Override
	public void complete_AchievementData(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("data", acceptor, context);
	}

	@Override
	public void complete_TaskData(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("data", acceptor, context);
	}

	@Override
	public void complete_RewardData(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("data", acceptor, context);
	}
	// End Achievement-------------

	// Ads
	@Override
	public void completeAdConditions_AllowedGenre(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeAdConditions_ProhibitedGenre(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeAdConditions_AllowedProgrammeFlag(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.programmeFlag, acceptor, context);
	}

	@Override
	public void completeAdConditions_ProhibitedProgrammeFlag(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.programmeFlag, acceptor, context);
	}

	@Override
	public void completeAdConditions_AllowedProgrammeType(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmeType, acceptor, context);
	}

	@Override
	public void completeAdConditions_ProhibitedProgrammeType(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmeType, acceptor, context);
	}

	@Override
	public void completeAdConditions_ProPressureGroup(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.pressuregroup, acceptor, context);
	}

	@Override
	public void completeAdConditions_ContraPressureGroup(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.pressuregroup, acceptor, context);
	}

	@Override
	public void completeAdConditions_TargetGroup(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.targetgroup, acceptor, context);
	}

	@Override
	public void completeAdvertisementData_Type(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.adType, acceptor, context);
	}

	@Override
	public void completeAdvertisementData_ProPressureGroup(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.pressuregroup, acceptor, context);
	}

	@Override
	public void completeAdvertisementData_ContraPressureGroup(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.pressuregroup, acceptor, context);
	}

	@Override
	public void completeAdvertisementData_Available(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void completeAdvertisementData_FixPrice(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void completeAdvertisementData_Infomercial(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void completeAdvertisementData_FixInfomercialProfit(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void complete_AdConditions(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("conditions", acceptor, context);
	}

	@Override
	public void complete_AdvertisementData(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("data", acceptor, context);
	}
	// End Ads-------------

	// News
	@Override
	public void completeNewsItem_Type(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.newsType, acceptor, context);
	}

	@Override
	public void completeNewsData_Genre(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.newsGenre, acceptor, context);
	}

	@Override
	public void completeNewsData_Flags(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.newsFlag, acceptor, context);
	}

	@Override
	public void completeNewsData_Fictional(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);	}

	@Override
	public void completeNewsData_Available(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);	}

	@Override
	public void completeEffect_Trigger(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.triggerType, acceptor, context);
	}

	@Override
	public void completeEffect_Type(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.effectType, acceptor, context);
	}

	@Override
	public void completeEffect_Genre(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeEffect_Enable(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void complete_NewsData(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("data", acceptor, context);
	}

	@Override
	public void complete_Effect(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("effect", acceptor, context);
	}
// End News-------------

	// Person
	@Override
	public void completePerson_Gender(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.gender, acceptor, context);
	}

	@Override
	public void completePerson_Fictional(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void completePerson_Bookable(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void completePerson_Job(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.job, acceptor, context);
	}

	@Override
	public void completePerson_Country(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.country, acceptor, context);
	}

	@Override
	public void completePersonData_TopGenre(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completePersonDetails_Gender(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.gender, acceptor, context);
	}

	@Override
	public void completePersonDetails_Fictional(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void completePersonDetails_Job(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.job, acceptor, context);
	}

	@Override
	public void completePersonDetails_Country(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.country, acceptor, context);
	}

	@Override
	public void completeProgrammeRole_Country(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.country, acceptor, context);
	}

	@Override
	public void completeProgrammeRole_Gender(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.gender, acceptor, context);
	}

	@Override
	public void complete_PersonData(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("data", acceptor, context);
	}

	@Override
	public void complete_PersonDetails(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("details", acceptor, context);
	}
	// End Person-------------

	// Programme
	@Override
	public void completeProgramme_Product(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmeType, acceptor, context);
	}

	public void completeProgramme_Fictional(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void completeProgramme_LicenceType(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.licenceType, acceptor, context);
	}

	@Override
	public void completeStaffMember_Function(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.job, acceptor, context);
	}

	@Override
	public void completeProgrammeGroups_TargetGroup(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.targetgroup, acceptor, context);
	}

	@Override
	public void completeProgrammeGroups_OptionalTargetGroup(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.targetgroup, acceptor, context);
	}

	@Override
	public void completeProgrammeGroups_ProPressureGroup(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.pressuregroup, acceptor, context);
	}

	@Override
	public void completeProgrammeGroups_ContraPressureGroup(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.pressuregroup, acceptor, context);
	}

	@Override
	public void completeProgrammeData_Distribution(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.distribution, acceptor, context);
	}

	@Override
	public void completeProgrammeData_Country(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.country, acceptor, context);
	}

	@Override
	public void completeProgrammeData_Flags(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.programmeFlag, acceptor, context);
	}

	@Override
	public void completeProgrammeData_LicenceFlags(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.licenceFlag, acceptor, context);
	}

	@Override
	public void completeProgrammeData_Maingenre(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeProgrammeData_Subgenre(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void complete_ProgrammeReleaseTime(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("releaseTime", acceptor, context);
	}

	@Override
	public void complete_ProgrammeRatings(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("ratings", acceptor, context);
	}

	@Override
	public void complete_ProgrammeData(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("data", acceptor, context);
	}

	@Override
	public void complete_StaffMember(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("member", acceptor, context);
	}
	// End Programme-------------

	// Script
	@Override
	public void completeScriptTemplate_Product(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmeType, acceptor, context);
	}

	@Override
	public void completeScriptTemplate_LicenceType(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.licenceType, acceptor, context);
	}

	@Override
	public void completeJob_Function(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.job, acceptor, context);
	}

	@Override
	public void completeJob_Gender(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.gender, acceptor, context);
	}

	@Override
	public void completeJob_Required(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants._boolean, acceptor, context);
	}

	@Override
	public void completeScriptGenres_MainGenre(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeScriptGenres_Subgenres(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		mapProposal(Constants.programmGenre, acceptor, context);
	}

	@Override
	public void completeScriptData_ProgrammeFlags(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.programmeFlag, acceptor, context);
	}

	@Override
	public void completeScriptData_OptionalProgrammeFlags(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.programmeFlag, acceptor, context);
	}

	@Override
	public void completeScriptData_LicenceFlags(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.licenceFlag, acceptor, context);
	}

	@Override
	public void completeScriptData_ScriptFlags(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		flagProposal(Constants.scriptFlag, acceptor, context);
	}

	@Override
	public void complete_ScriptGenres(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("genres", acceptor, context);
	}

	@Override
	public void complete_Job(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("job", acceptor, context);
	}

	@Override
	public void complete_ScriptData(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("data", acceptor, context);
	}

	@Override
	public void complete_Episodes(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("episodes", acceptor, context);
	}

	@Override
	public void complete_StudioSize(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("studio_size", acceptor, context);
	}

	@Override
	public void complete_Blocks(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("blocks", acceptor, context);
	}

	@Override
	public void complete_Price(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("price", acceptor, context);
	}

	@Override
	public void complete_Potential(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("potential", acceptor, context);
	}

	@Override
	public void complete_Outcome(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("outcome", acceptor, context);
	}

	@Override
	public void complete_Review(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("review", acceptor, context);
	}

	@Override
	public void complete_Speed(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("speed", acceptor, context);
	}

	@Override
	public void complete_ProductionTime(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("production_time", acceptor, context);
	}
	// End Script-------------

	@Override
	public void completeUnnamedProperty_Key(EObject model, Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		if(model instanceof GroupAttractivity) {
			List<String> defined = ((GroupAttractivity) model).getData().stream().map(d->d.getKey()).collect(Collectors.toList());
			List<String>allowed=new ArrayList<String>(Constants.targetgroup.maleFemale());
			allowed.removeAll(defined);
			allowed.forEach(v->acceptor.accept(createCompletionProposal(v, context)));
		}
	}

	// Allgemein
	@Override
	public void completeKeyword(Keyword keyword, ContentAssistContext contentAssistContext,
			ICompletionProposalAcceptor acceptor) {
		if("<".equals(keyword.getValue())){
			return;
		}
		super.completeKeyword(keyword, contentAssistContext, acceptor);
	}

	@Override
	public void complete_GroupAttractivity(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("targetgroupattractivity", acceptor, context);
	}

	@Override
	public void complete_ProgrammeGroups(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("groups", acceptor, context);
	}

	@Override
	public void complete_Availability(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("availability", acceptor, context);
	}

	@Override
	public void complete_Modifier(EObject model, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		oneLineTagProposal("modifier", acceptor, context);
	}
}
