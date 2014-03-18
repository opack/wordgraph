package com.slamdunk.wordgraph.puzzle.obstacles;

import com.slamdunk.wordgraph.puzzle.graph.Graph;

/**
 * Représente un obstacle qui peut gêner le joueur.
 */
public interface Obstacle {
	/**
	 * 
	 * @param graph
	 */
	void init(Graph graph);
	
	/**
	 * Applique l'effet de l'obstacle sur le graphe.
	 * Cette méthode doit être appelée à chaque rafraîchissement
	 * du graphe.
	 * L'effet ne devrait être appliqué que si l'obstacle est
	 * toujours actif.
	 * @param graph
	 * @see #isActive()
	 */
	void applyEffect(Graph graph);
	
	/**
	 * Indique si l'obstacle est toujours actif ou non.
	 * @return
	 */
	boolean isActive();
	
	/**
	 * Retourne le type de l'obstacle
	 * @return
	 */
	ObstaclesTypes getType();
}
