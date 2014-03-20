package com.slamdunk.wordgraph.puzzle;

import com.slamdunk.wordgraph.puzzle.graph.Graph;

/**
 * Listener notifi� lorsqu'un �v�nement se produit dans le puzzle
 */
public interface PuzzleListener {
	/**
	 * M�thode appel�e une fois que le graphe a �t� charg�
	 * @param word
	 */
	void graphLoaded(Graph graph);
	
	/**
	 * M�thode appel�e lorsqu'un mot est valid� avec succ�s
	 * @param word
	 */
	void wordValidated(String word);
}
