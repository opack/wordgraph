package com.slamdunk.wordgraph.puzzle;

import com.slamdunk.wordgraph.puzzle.graph.Graph;

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
	 * Méthode appelée lorsqu'un mot est validé avec succès
	 * @param word
	 */
	void wordValidated(String word);
}
