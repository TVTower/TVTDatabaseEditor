package org.tvtower.db.validation.expressionhelper;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.tvtower.db.database.Job;
import org.tvtower.db.database.Programme;
import org.tvtower.db.database.ScriptTemplate;
import org.tvtower.db.database.StaffMember;

import com.google.common.collect.ImmutableList;

// see game.gamescriptexpression.bmx#SEFN_StationMap
class SelfExpression extends AbstractExpression {
	private static boolean STRICT = true;
	private static final List<String> scriptParams = ImmutableList.of(//
			"role", "parent");
	private static final List<String> programmeParams = ImmutableList.of(//
			"cast", "role", "parent");
	private static final List<String> supportedNameParam = ImmutableList.of(//
			"name", "firstname", "lastname", "fullname", "nickname", "title");

	static String validate(List<String> params, EObject context) {
		int paramCount = params.size();
		if (paramCount < 1) {
			return "at least one parameter expected";
		}
		Programme prog = getProgContainer(context);
		if (prog != null) {
			return getProgrammError(params, prog);
		} else {
			ScriptTemplate scr = getScriptContainer(context);
			if (scr != null) {
				return getScriptError(params, scr);
			}
		}

		return "illegal context for .self";
	}

	//TODO validate parent!!
	private static String getProgrammError(List<String> params, Programme context) {
		String param1 = params.get(0);
		if(!isStringParam(param1)) {
			return "first parameter must be string parameter";
		}
		if (STRICT && !isStringFromCollection(param1, programmeParams)) {
			return "unsupported self paramter " + param1;
		}
		// programme cast or role
		if (param1.contains("cast") || param1.contains("role")) {
			if (params.size() != 3) {
				return "cast needs 2 parameters";
			}
			String index = params.get(1);
			try {
				Integer.parseInt(index);
				StaffMember m = getCast(context, index);
				if (m == null) {
					return "cast with index " + index + " does exists";
				} else if (param1.contains("role") && m.getRolId() == null) {
					return "cast with index " + index + " does not have a role";
				}
			} catch (NumberFormatException e) {
				return "index parameter must be number";
			}
			String nameType = params.get(2);
			if (!isStringFromCollection(nameType, supportedNameParam)) {
				return "unsupported name type";
			}
		}
		return null;
	}

	//TODO validate parent!!
	private static String getScriptError(List<String> params, ScriptTemplate context) {
		String param1 = params.get(0);
		if(!isStringParam(param1)) {
			return "first parameter must be string parameter";
		}
		if("parent".equals(param1)) {
			ScriptTemplate parent = EcoreUtil2.getContainerOfType(context, ScriptTemplate.class);
			if(parent !=null) {
				return getScriptError(params.subList(1, params.size()), parent);
			}
		}
		if (STRICT && !isStringFromCollection(param1, scriptParams)) {
			return "unsupported self paramter " + param1;
		}
		// script role
		if (param1.contains("role")) {
			if (params.size() != 3) {
				return "role needs 2 parameters";
			}
			String index = params.get(1);
			try {
				Integer.parseInt(index);
				if (!hasJobIndex(context, index)) {
					return "role with index " + index + " cannot exists";
				}
			} catch (NumberFormatException e) {
				return "index parameter must be number";
			}
			String nameType = params.get(2);
			if (!isStringFromCollection(nameType, supportedNameParam)) {
				return "unsupported name type";
			}
		}
		return null;
	}

	private static boolean hasJobIndex(ScriptTemplate c, String index) {
		if (c.getJobs() != null) {
			for (Job job : c.getJobs().getJob()) {
				if (index.equals(job.getIndex())) {
					return true;
				}
			}
		}
		if (c.eContainer().eContainer() instanceof ScriptTemplate) {
			return hasJobIndex((ScriptTemplate) c.eContainer().eContainer(), index);
		}
		return false;
	}

	private static StaffMember getCast(Programme p, String index) {
		if (p.getStaff() != null) {
			for (StaffMember staff : p.getStaff().getMember()) {
				if (index.equals(staff.getIndex())) {
					return staff;
				}
			}
		}
		if (p.eContainer().eContainer() instanceof Programme) {
			return getCast((Programme) p.eContainer().eContainer(), index);
		}
		return null;
	}

	private static Programme getProgContainer(EObject context) {
		return EcoreUtil2.getContainerOfType(context, Programme.class);
	}

	private static ScriptTemplate getScriptContainer(EObject context) {
		return EcoreUtil2.getContainerOfType(context, ScriptTemplate.class);
	}
}
