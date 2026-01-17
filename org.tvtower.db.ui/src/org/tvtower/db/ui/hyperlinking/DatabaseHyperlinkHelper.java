package org.tvtower.db.ui.hyperlinking;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.HyperlinkHelper;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkAcceptor;
import org.tvtower.db.database.Advertisement;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.NewsItem;
import org.tvtower.db.database.Person;
import org.tvtower.db.database.Programme;
import org.tvtower.db.database.ProgrammeRole;
import org.tvtower.db.database.ScriptTemplate;
import org.tvtower.db.services.DatabaseGrammarAccess;

import com.google.inject.Inject;

public class DatabaseHyperlinkHelper extends HyperlinkHelper {

	@Inject
	private DatabaseGrammarAccess ga;
	DatabasePackage $ = DatabasePackage.eINSTANCE;
	private static final String WIKI = "https://www.wikidata.org/wiki/";
	private static final String TMDB = "https://www.themoviedb.org/";
	private static final String IMDB = "https://www.imdb.com/";

	@Override
	public void createHyperlinksByOffset(XtextResource resource, int offset, IHyperlinkAcceptor acceptor) {
		super.createHyperlinksByOffset(resource, offset, acceptor);
		INode node = NodeModelUtils.findLeafNodeAtOffset(resource.getParseResult().getRootNode(), offset);
		if (node instanceof ILeafNode) {
			EObject sem = NodeModelUtils.findActualSemanticObjectFor(node);
			String wikiId = null;
			Keyword wikiIdKeyword = null;
			EAttribute wikiIdAttribute = null;
			if (sem instanceof Advertisement) {
				Advertisement ad = (Advertisement) sem;
				wikiId = ad.getWikiID();
				wikiIdKeyword = ga.getAdvertisementAccess().getWikidata_idKeyword_2_2_0();
				wikiIdAttribute = $.getAdvertisement_WikiID();
			} else if (sem instanceof Programme) {
				Programme p = (Programme) sem;
				wikiId = p.getWikiID();
				wikiIdKeyword = ga.getProgrammeAccess().getWikidata_idKeyword_2_8_0();
				wikiIdAttribute = $.getProgramme_WikiID();
				createHyperlink(p.getTmdbId(), TMDB + "movie/", node, sem,
						ga.getProgrammeAccess().getTmdb_idKeyword_2_3_0(), $.getProgramme_TmdbId(), acceptor);
				createHyperlink(p.getImdbId(), IMDB + "title/", node, sem,
						ga.getProgrammeAccess().getImdb_idKeyword_2_4_0(), $.getProgramme_ImdbId(), acceptor);
			} else if (sem instanceof Person) {
				Person p = (Person) sem;
				wikiId = p.getWikiID();
				wikiIdKeyword = ga.getPersonAccess().getWikidata_idKeyword_2_22_0();
				wikiIdAttribute = $.getPerson_WikiID();
				createHyperlink(p.getTmdbId(), TMDB + "person/", node, sem,
						ga.getPersonAccess().getTmdb_idKeyword_2_1_0(), $.getPerson_TmdbId(), acceptor);
				createHyperlink(p.getImdbId(), IMDB + "name/", node, sem,
						ga.getPersonAccess().getImdb_idKeyword_2_2_0(), $.getPerson_ImdbId(), acceptor);
			} else if (sem instanceof ProgrammeRole) {
				ProgrammeRole p = (ProgrammeRole) sem;
				wikiId = p.getWikiID();
				wikiIdKeyword = ga.getProgrammeRoleAccess().getWikidata_idKeyword_2_11_0();
				wikiIdAttribute = $.getProgrammeRole_WikiID();
			} else if (sem instanceof ScriptTemplate) {
				ScriptTemplate t = (ScriptTemplate) sem;
				wikiId = t.getWikiID();
				wikiIdKeyword = ga.getScriptTemplateAccess().getWikidata_idKeyword_2_5_0();
				wikiIdAttribute = $.getMayContainVariables_WikiID();
			} else if (sem instanceof NewsItem) {
				NewsItem i = (NewsItem) sem;
				wikiId = i.getWikiID();
				wikiIdKeyword = ga.getNewsItemAccess().getWikidata_idKeyword_2_5_0();
				wikiIdAttribute = $.getMayContainVariables_WikiID();
			}
			if (wikiIdKeyword != null) {
				createHyperlink(wikiId, WIKI, node, sem, wikiIdKeyword, wikiIdAttribute, acceptor);
			}
		}
	}

	private void createHyperlink(String value, String url, INode node, EObject sem, Keyword startKW,
			EStructuralFeature assignment, IHyperlinkAcceptor acceptor) {
		if (value != null && value.length() > 0) {
			int start = -1;
			int end = -1;
			// find end offset of assignment (startKW="assgnment")
			List<INode> nodes = NodeModelUtils.findNodesForFeature(sem, assignment);
			for (INode n : nodes) {
				if (n.getEndOffset() > end) {
					end = n.getEndOffset();
				}
			}
			if (end > 0 && node.getOffset() > end) {
				return;
			}
			// find start keyword by going over all nodes until keyword is found
			ICompositeNode cn = NodeModelUtils.findActualNodeFor(sem);
			Iterator<ILeafNode> leaves = cn.getLeafNodes().iterator();
			while (leaves.hasNext()) {
				ILeafNode l = leaves.next();
				if (l.getGrammarElement() == startKW) {
					start = l.getOffset();
					break;
				}
			}
			//create link only if node lies in assignment range
			if (start >= 0 && end >= 0 && end > start) {
				if (node.getOffset() >= start && node.getEndOffset() <= end) {
					acceptor.accept(new URLHyperlink(new Region(start, end - start), url + value));
				}
			}
		}
	}
}
