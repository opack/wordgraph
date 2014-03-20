package com.slamdunk.wordgraph.puzzle.obstacles;


/**
 * Gère tous les obstacles Isle
 */
public class IsleObstacleManager extends ObstacleManager<IsleObstacle> {
	
	/**
	 * Indique si un obstacle rend la lettre isolée
	 * @param selected
	 * @param obstacles
	 * @return
	 */
	public boolean isIsolated(String selected) {
		return getObstacles().containsKey(selected);
	}
}
