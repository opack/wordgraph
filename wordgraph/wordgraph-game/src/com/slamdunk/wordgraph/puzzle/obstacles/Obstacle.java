package com.slamdunk.wordgraph.puzzle.obstacles;

import com.slamdunk.wordgraph.puzzle.graph.Graph;

/**
 * Repr�sente un obstacle qui peut g�ner le joueur.
 */
public interface Obstacle {
	/**
	 * 
	 * @param graph
	 */
	void init(Graph graph);
	
	/**
	 * Applique l'effet de l'obstacle sur le graphe.
	 * Cette m�thode doit �tre appel�e � chaque rafra�chissement
	 * du graphe.
	 * L'effet ne devrait �tre appliqu� que si l'obstacle est
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
