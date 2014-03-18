package com.slamdunk.wordgraph.puzzle;

import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

public class PuzzleGestureListener extends GestureAdapter {
	private Graph graph;
	private ScrollPane scrollPane;
	
	public Graph getGraph() {
		return graph;
	}
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	public void setScrollPane(ScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		if (initialDistance == distance) {
			return false;
		}
		if (initialDistance < distance) {
			graph.zoom(1.01f);
		} else {
			graph.zoom(1/1.01f);
		}
		scrollPane.invalidate();
		return true;
	}
}
