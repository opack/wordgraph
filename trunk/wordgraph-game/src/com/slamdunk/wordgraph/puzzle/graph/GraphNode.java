package com.slamdunk.wordgraph.puzzle.graph;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Un noeud du graphe, donc une lettre
 */
public class GraphNode extends TextButton {
	private List<GraphEdge> edges;

	public GraphNode(String text, TextButtonStyle style) {
		super(text, style);
		edges = new ArrayList<GraphEdge>();
	}

	/**
	 * Ajoute un lien connecté à ce noeud.
	 * @param edge
	 */
	public void addEdge(GraphEdge edge) {
		edges.add(edge);
	}

	/**
	 * Retourne true si aucun lien n'est visible
	 * @return
	 */
	public boolean isIsolated() {
		for (GraphEdge edge : edges) {
			if (edge.isVisible()) {
				return false;
			}
		}
		return true;
	}
}
