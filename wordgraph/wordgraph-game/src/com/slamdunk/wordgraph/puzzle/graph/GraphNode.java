package com.slamdunk.wordgraph.puzzle.graph;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Un noeud du graphe, donc une lettre
 */
public class GraphNode extends TextButton {
	private List<GraphLink> links;

	public GraphNode(String text, TextButtonStyle style) {
		super(text, style);
		links = new ArrayList<GraphLink>();
	}

	/**
	 * Ajoute un lien connect� � ce noeud.
	 * @param link
	 */
	public void addLink(GraphLink link) {
		links.add(link);
	}
	
	public List<GraphLink> getLinks() {
		return links;
	}

	/**
	 * Retourne true s'il existe au moins 1 liens menant �
	 * ce noeud n'ayant pas encore �t� utilis�
	 * @return
	 */
	public boolean isReachable() {
		for (GraphLink link : links) {
			if (!link.isUsed()) {
				return true;
			}
		}
		return false;
	}
}
