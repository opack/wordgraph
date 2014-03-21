package com.slamdunk.wordgraph.puzzle;

import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Listener notifié lorsqu'un évènement se produit dans le puzzle
 */
public interface PuzzleListener {
	/**
	 * Méthode appelée une fois que le graphe a été chargé
	 * @param word
	 */
	void graphLoaded(Graph graph);
	
	/**
	 * Méthode appelée une fois qu'un lien est marqué comme utilisé
	 * @param word
	 */
	void linkUsed(GraphLink link);
	
	/**
	 * Méthode appelée une fois qu'un noeud est passé à l'état invisible
	 * @param word
	 */
	void nodeHidden(GraphNode node);
	
	/**
	 * Méthode appelée lorsqu'un mot est validé avec succès
	 * @param word
	 */
	void wordValidated(String word);
}
