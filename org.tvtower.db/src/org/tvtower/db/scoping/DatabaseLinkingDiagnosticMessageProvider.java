package org.tvtower.db.scoping;

import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.linking.impl.LinkingDiagnosticMessageProvider;
import org.tvtower.db.database.Effect;

public class DatabaseLinkingDiagnosticMessageProvider extends LinkingDiagnosticMessageProvider {

	@Override
	public DiagnosticMessage getUnresolvedProxyMessage(ILinkingDiagnosticContext context) {
		// do not try to resolve variables within effects
		if (context.getContext() instanceof Effect) {
			if (context.getLinkText() != null && context.getLinkText().matches("\\$\\{\\w+\\}")) {
				return null;
			}
		}
		return super.getUnresolvedProxyMessage(context);
	}
}
