package com.slamdunk.wordgraph.puzzle.obstacles;

import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;

/**
 * Isole une lettre
 */
public class IsleObstacle extends NodeObstacle{
	
	public IsleObstacle(String target) {
		super(ObstaclesTypes.ISLE, target);
	}

	@Override
	public void applyEffect(PuzzleGraph graph) {
		if (!isActive()) {
			return;
		}
		// Place une image d'eau sur la lettre isolée
		if (getImage() == null) {
			createImage("obstacle-isle");
		}
		// Cache tous les liens vers et depuis cette lettre
		for (PuzzleLink link : getNode().getLinks().values()) {
			link.setVisible(false);
		}
	}
}
