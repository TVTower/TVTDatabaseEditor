/*
 * generated by Xtext 2.25.0 and then adapted
 */
package org.tvtower.db.ui;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xtext.ui.validation.AbstractValidatorConfigurationBlock;
import org.tvtower.db.validation.DatabaseConfigurableIssueCodesProvider;

@SuppressWarnings("restriction")
public class DatabaseValidatorConfigurationBlock extends AbstractValidatorConfigurationBlock {

	protected static final String SETTINGS_SECTION_NAME = "Database";

	@Override
	protected void fillSettingsPage(Composite composite, int nColumns, int defaultIndent) {
		addComboBox(DatabaseConfigurableIssueCodesProvider.UNSUPPORTED_ATTRIBUTE, "unsupported attribute", composite, defaultIndent);
		addCheckBox(composite, "check availability effects (slow and buggy - activate on demand)", DatabaseConfigurableIssueCodesProvider.VALIDATTE_EFFECT_ACTIVATION, new String[] { IPreferenceStore.TRUE, IPreferenceStore.FALSE }, defaultIndent);

		Composite section = createSection("News", composite, nColumns);
		addComboBox(DatabaseConfigurableIssueCodesProvider.TRIGGERED_NEWS_THREAD, "triggered news: different thread id", section, defaultIndent);
		addComboBox(DatabaseConfigurableIssueCodesProvider.TRIGGERED_NEWS_GENRE, "triggered news: different genre", section, defaultIndent);
		addComboBox(DatabaseConfigurableIssueCodesProvider.TRIGGERED_NEWS_UNIQUENESS, "triggered news: unique triggers repeatable" , section, defaultIndent);

		section = createSection("Persons", composite, nColumns);
		addComboBox(DatabaseConfigurableIssueCodesProvider.PERSON_SPOOF_NAME, "spoof name similarity", section, defaultIndent);
		addComboBox(DatabaseConfigurableIssueCodesProvider.PERSON_NAME_ENDING, "(orig)name ending with s", section, defaultIndent);
		addComboBox(DatabaseConfigurableIssueCodesProvider.ROLE_UNDEFINED_GENDER, "role with undefined gender", section, defaultIndent);

		//		addComboBox(DatabaseConfigurableIssueCodesProvider.DEPRECATED_MODEL_PART, "Deprecated Model Part", composite, defaultIndent);
	}

	@Override
	public void dispose() {
		storeSectionExpansionStates(getDialogSettings());
		super.dispose();
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings dialogSettings = super.getDialogSettings();
		IDialogSettings section = dialogSettings.getSection(SETTINGS_SECTION_NAME);
		if (section == null) {
			return dialogSettings.addNewSection(SETTINGS_SECTION_NAME);
		}
		return section;
	}
}
