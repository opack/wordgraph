package com.slamdunk.utils.ui.json;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

public class GraphJsonBuilder extends UIJsonBuilder {
	
	@Override
	protected Graph createEmpty(Skin skin, String style) {
		return new Graph();
	}
	
	@Override
	public Graph build(Skin skin) {
		// Gère les propriétés basiques du widget
		Graph graph = (Graph)super.build(skin);
		
		// Gère les propriétés spécifiques
		parseCenter(graph);
		
		return graph;
	}
	
	protected void parseCenter(Graph graph) {
		if (hasProperty("center")) {
			graph.setCenterGraph(actorDescription.getBoolean("center"));
		}
	}
}
