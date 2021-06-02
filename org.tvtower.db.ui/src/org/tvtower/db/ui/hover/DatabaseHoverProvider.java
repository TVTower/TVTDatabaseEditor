package org.tvtower.db.ui.hover;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.text.IRegion;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControlInput;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.constants.TVTHoverInfoCreator;

public class DatabaseHoverProvider extends DefaultEObjectHoverProvider {

	private IRegion currentRegion;

	@Override
	protected String getHoverInfoAsHtml(EObject o) {
		String result = super.getHoverInfoAsHtml(o);
		if (result != null) {
			return result;
		} else {
			//find hovered object property
			for (EStructuralFeature f : o.eClass().getEAllStructuralFeatures()) {
				List<INode> nodes = NodeModelUtils.findNodesForFeature(o, f);
				for (INode iNode : nodes) {
					if (iNode.getTextRegion().contains(currentRegion.getOffset())) {
						return getFeatureInfo(o, f);
					}
				}
			}
		}
		return null;
	}

	private String getFeatureInfo(EObject o, EStructuralFeature f) {
		TVTHoverInfoCreator infoCreator = Constants.getHoverInfoCreator(f);
		if(infoCreator!=null) {
			return infoCreator.createHoverInfo(o.eGet(f));
		}
		return null;
	}

	@Override
	protected XtextBrowserInformationControlInput getHoverInfo(EObject element, IRegion hoverRegion,
			XtextBrowserInformationControlInput previous) {
		//store region, so that more specifc info can be created
		this.currentRegion = hoverRegion;
		return super.getHoverInfo(element, hoverRegion, previous);
	}
}
