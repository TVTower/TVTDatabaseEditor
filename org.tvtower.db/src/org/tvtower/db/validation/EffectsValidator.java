package org.tvtower.db.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.preferences.IPreferenceValues;
import org.eclipse.xtext.preferences.IPreferenceValuesProvider;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.IResourceDescriptionsProvider;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.ConfigurableIssueCodesProvider;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.EffectType;
import org.tvtower.db.database.Database;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.Effect;
import org.tvtower.db.database.Programme;
import org.tvtower.db.database.ScriptTemplate;

import com.google.inject.Inject;

public class EffectsValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Inject
	private IResourceDescriptionsProvider index;

	@Inject
	private IPreferenceValuesProvider valuesProvider;
	@Inject
	private ConfigurableIssueCodesProvider issuCodeProvider;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkProgrammeEffects(Database db) {
		IPreferenceValues preferenceValues = valuesProvider.getPreferenceValues(db.eResource());
		String value = preferenceValues.getPreference(issuCodeProvider.getConfigurableIssueCodes()
				.get(DatabaseConfigurableIssueCodesProvider.VALIDATTE_EFFECT_ACTIVATION));
		if (!"true".equals(value)) {
			return;
		}
		List<Effect> enablingEffects = getEnablingEffects(db);
		List<Programme> disabledProgrammes = EcoreUtil2.getAllContentsOfType(db, Programme.class).stream()
				.filter(p -> p.getData() != null && "0".equals(p.getData().getAvailable()))
				.collect(Collectors.toList());
		List<ScriptTemplate> disabledScripts = EcoreUtil2.getAllContentsOfType(db, ScriptTemplate.class).stream()
				.filter(p -> p.getData() != null && "0".equals(p.getData().getAvailable()))
				.collect(Collectors.toList());
		List<EObject> enabledObject = enablingEffects.stream().map(e -> e.getGuid()).collect(Collectors.toList());

		// filter to those not enabled within the same database file
		disabledProgrammes.removeAll(enabledObject);
		disabledScripts.removeAll(enabledObject);

		// filter those assumed to be enabled by other files
		removeEnabledByOtherFiles(disabledProgrammes);
		removeEnabledByOtherFiles(disabledScripts);

		disabledProgrammes.forEach(p -> {
			error("unvailable programme is not activated", p.getData(), $.getProgrammeData_Available());
		});
		disabledScripts.forEach(s -> {
			error("unvailable script is not activated", s.getData(), $.getScriptData_Available());
		});
	}

	// if there is an effect reference to an entry, we assume it is an enableing
	// one!
	private void removeEnabledByOtherFiles(List<? extends EObject> objects) {
		if (!objects.isEmpty()) {
			List<EObject> toRemove = new ArrayList<>();
			// all from the same file...
			IResourceDescriptions data = index.getResourceDescriptions(objects.get(0).eResource().getResourceSet());
			EReference refType = $.getEffect_Guid();
			Map<URI, EObject> map = new HashMap<>();
			for (EObject orig : objects) {
				URI origURI = orig.eResource().getURI().appendFragment(orig.eResource().getURIFragment(orig));
				map.put(origURI, orig);
			}
			for (IResourceDescription r : data.getAllResourceDescriptions()) {
				for (IReferenceDescription ref : r.getReferenceDescriptions()) {
					if (refType.equals(ref.getEReference())) {
						EObject enabled = map.get(ref.getTargetEObjectUri());
						if (enabled != null) {
							toRemove.add(enabled);
						}
					}
				}
			}
			objects.removeAll(toRemove);
		}
	}

	private List<Effect> getEnablingEffects(Database db) {
		List<Effect> allEffects = EcoreUtil2.getAllContentsOfType(db, Effect.class);
		List<Effect> result = new ArrayList<>();
		for (Effect effect : allEffects) {
			String type = effect.getType();
			if (EffectType.PROGRAMME_AVAILABILITY.equals(type) || EffectType.SCRIPT_AVAILABILITY.equals(type)) {
				if (!"0".equals(effect.getEnable())) {
					result.add(effect);
				}
			}
		}
		return result;
	}
}
