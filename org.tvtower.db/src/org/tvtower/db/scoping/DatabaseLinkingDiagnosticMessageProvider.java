package org.tvtower.db.scoping;

import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.linking.impl.LinkingDiagnosticMessageProvider;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.Effect;

public class DatabaseLinkingDiagnosticMessageProvider extends LinkingDiagnosticMessageProvider {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public DiagnosticMessage getUnresolvedProxyMessage(ILinkingDiagnosticContext context) {
		// do not try to resolve variables within effects or preselected cast
		if (context.getContext() instanceof Effect || context.getReference() == $.getJob_PreselectedCast()) {
			if (context.getLinkText() != null && context.getLinkText().matches("\\$\\{\\w+\\}")) {
				return null;
			}
		}
		return super.getUnresolvedProxyMessage(context);
	}
}
