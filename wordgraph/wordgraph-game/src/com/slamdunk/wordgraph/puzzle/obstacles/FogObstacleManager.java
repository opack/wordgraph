package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.Map;

/**
 * G�re tous les obstacles Fog
 */
public class FogObstacleManager extends ObstacleManager<FogObstacle> {
	/**
	 * Dissipe le brouillard sur les lettres du mot indiqu�
	 * @param validWord
	 */
	public void clearFog(String validWord) {
		Map<String, FogObstacle> obstacles = getObstacles();
		for (String letter : validWord.split("")) {
			FogObstacle obstacle = obstacles.get(letter);
			if (obstacle != null) {
				obstacle.setActive(false);
				obstacle.applyEffect(getGraph());
			}
		}
	}
	
	@Override
	public void wordValidated(String word) {
		// D�sactivation des obstacles brouillard lorsqu'un mot est valid�
		clearFog(word);
	}
}
