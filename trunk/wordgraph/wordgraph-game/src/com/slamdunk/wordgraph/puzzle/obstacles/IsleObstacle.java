package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Isole une lettre
 */
public class IsleObstacle implements Obstacle{
	private String letter;
	private boolean isActive;
	private GraphNode node;
	
	public IsleObstacle(String letter) {
		this.letter = letter;
		isActive = true;
	}

	@Override
	public void applyEffect(Graph graph) {
		// Cache tous les liens vers et depuis cette lettre
		if (node != null) {
			for (GraphLink link : node.getLinks()) {
				link.setVisible(false);
			}
		}
	}

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	@Override
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public ObstaclesTypes getType() {
		return ObstaclesTypes.ISLE;
	}
	
	public String getLetter() {
		return letter;
	}

	@Override
	public void init(Graph graph) {
		// Récupère la lettre isolée
		node = graph.getNode(letter);
		isActive = node != null;
		if (isActive) {
			// Place une image d'eau sur la lettre isolée
			Image water = new Image(Assets.defaultPuzzleSkin.getDrawable("obstacle-isle"));
			node.addActor(water);
			water.setZIndex(0);
			water.setPosition((node.getWidth() - water.getWidth()) / 2, (node.getHeight() - water.getHeight()) / 2);
		}
	}
}
