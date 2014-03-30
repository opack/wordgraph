package com.slamdunk.wordgraph.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.wordgraph.puzzle.obstacles.Obstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes;

/**
 * Représente un objet qui peut être la cible d'un ou plusieurs obstacles
 */
public class ObstacleTarget {
	private List<Obstacle> obstacles;
	/**
	 * Ajoute un obstacle connecté à ce noeud.
	 * @param link
	 */
	public void addObstacle(Obstacle obstacle) {
		if (obstacles == null) {
			obstacles = new ArrayList<Obstacle>();
		}
		obstacles.add(obstacle);
	}
	
	public void removeObstacle(Obstacle obstacle) {
		if (obstacles != null) {
			obstacles.remove(obstacle);
		}
	}
	
	public List<Obstacle> getObstacles() {
		return obstacles;
	}
	
	/**
	 * Indique s'il existe un obstacle actif du type indiqué qui a ce
	 * noeud pour cible.
	 * @param obstacleManager
	 * @param letter
	 * @return
	 */
	public boolean isTargeted(ObstaclesTypes type) {
		if (obstacles != null) {
			for (Obstacle obstacle : obstacles) {
				if (obstacle.getType() == type
				&& obstacle.isActive()) {
					return true;
				}
			}
		}
		return false;
	}
}
