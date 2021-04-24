package org.tvtower.db.validation;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.ScriptData;
import org.tvtower.db.database.ScriptTemplate;

//TODO validiere Programm und Lizenztyp bei Serien/Folgen
//TODO Validierung überhaupt anfangen
public class ScriptValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkScript(ScriptTemplate t) {
		//index-Validierung bei Kind-Elementen
		if(t.getIndex()!=null) {
//			error("index defined", $.getScriptTemplate_Index());
		}
	}

	@Check
	public void checkScriptData(ScriptData d) {
//		if(d.getProductionTime()!=null) {
//			error("time defined", $.getScriptData_ProductionTime());
//		}
	}

}
