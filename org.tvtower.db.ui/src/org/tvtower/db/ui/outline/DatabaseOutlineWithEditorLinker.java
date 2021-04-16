package org.tvtower.db.ui.outline;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.actions.OutlineWithEditorLinker;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.util.ITextRegion;
import org.tvtower.db.database.DatabasePackage;

public class DatabaseOutlineWithEditorLinker extends OutlineWithEditorLinker {

	@Override
	protected IOutlineNode findBestNode(IOutlineNode input, ITextRegion selectedTextRegion) {
		IOutlineNode result = super.findBestNode(input, selectedTextRegion);
		if (result instanceof EObjectNode) {
			EClass eClass = ((EObjectNode) result).getEClass();
			if (eClass == DatabasePackage.eINSTANCE.getNews()) {
				// outline is not nested for news - search for the correct news node
				return getNewsOutlineNode(result, selectedTextRegion);
			}
		}
		return result;
	}

	private IOutlineNode getNewsOutlineNode(IOutlineNode input, ITextRegion selectedTextRegion) {
		for (IOutlineNode node : input.getChildren()) {
			ITextRegion textRegion = node.getFullTextRegion();
			if (textRegion.contains(selectedTextRegion)) {
				return node;
			} else {
				IOutlineNode childCandidate = getNewsOutlineNode(node, selectedTextRegion);
				if (childCandidate != null) {
					return childCandidate;
				}
			}
		}
		return null;
	}
}
