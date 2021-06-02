package org.tvtower.db.ui.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hover.DispatchingEObjectTextHover;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.google.inject.Inject;

public class DatabaseEObjectHover extends DispatchingEObjectTextHover {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Override
	protected Pair<EObject, IRegion> getXtextElementAt(XtextResource resource, int offset) {
		Pair<EObject, IRegion> result = super.getXtextElementAt(resource, offset);
		if (result != null) {
			return result;
		} else {
			EObject o = eObjectAtOffsetHelper.resolveElementAt(resource, offset);
			if (o != null) {
				ICompositeNode node = NodeModelUtils.findActualNodeFor(o);
				//create hover region for values
				if (node != null) {
					ILeafNode leaf = NodeModelUtils.findLeafNodeAtOffset(node, offset);
					boolean isStringValue = leaf != null && leaf.getText() != null && leaf.getText().length() > 0
							&& leaf.getText().charAt(0) == '"';
					if (isStringValue) {
						return Tuples.create(o, new Region(leaf.getOffset(), leaf.getLength()));
					}
				}
				// TODO keywords?
			}
		}
		return null;
	}
}
