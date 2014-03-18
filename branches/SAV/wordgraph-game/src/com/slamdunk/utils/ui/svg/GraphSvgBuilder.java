package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

public class GraphSvgBuilder extends UISvgBuilder {
	
	@Override
	protected Graph createEmpty(Skin skin, String style) {
		return new Graph();
	}
	
	@Override
	public Graph build(Skin skin) {
		// G�re les propri�t�s basiques du widget
		Graph graph = (Graph)super.build(skin);
		
		// G�re les propri�t�s sp�cifiques
		parseCenter(graph);
		
		return graph;
	}
	
	protected void parseCenter(Graph graph) {
		if (hasAttribute("ui.center")) {
			graph.setCenterGraph(actorDescription.getBooleanAttribute("ui.center"));
		}
	}
}
