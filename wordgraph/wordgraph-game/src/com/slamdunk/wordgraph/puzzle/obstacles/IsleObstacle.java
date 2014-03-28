package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

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

	/**
	 * Indique si le manager contient un obstacle ISLE qui isole la lettre
	 * indiquée. Cela signifie qu'il existe un obstacle actif qui a cette
	 * lettre pour cible.
	 * @param obstacleManager
	 * @param last
	 * @return
	 */
	public static boolean isIsolated(ObstacleManager obstacleManager, String letter) {
		List<Obstacle> isles = obstacleManager.getObstacles(ObstaclesTypes.ISLE);
		if (isles != null && letter != null) {
			for (Obstacle isle : isles) {
				if (isle.isActive() && isle.getTarget().equals(letter)) {
					return true;
				}
			}
		}
		return false;
	}
}
