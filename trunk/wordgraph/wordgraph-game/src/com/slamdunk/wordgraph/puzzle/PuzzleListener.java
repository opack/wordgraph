package com.slamdunk.wordgraph.puzzle;

import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Listener notifi� lorsqu'un �v�nement se produit dans le puzzle
 */
public interface PuzzleListener {
	/**
	 * M�thode appel�e une fois que le graphe a �t� charg�
	 * @param graph Le graphe qui vient d'�tre charg�
	 * @param puzzlePreferences	Les pr�f�rences qui contiennent diverses infos
	 */
	void graphLoaded(Graph graph, PuzzlePreferencesHelper puzzlePreferences);
	
	/**
	 * M�thode appel�e une fois qu'un lien est marqu� comme utilis�
	 * @param word
	 */
	void linkUsed(GraphLink link);
	
	/**
	 * M�thode appel�e une fois qu'un noeud est pass� � l'�tat invisible
	 * @param word
	 */
	void nodeHidden(GraphNode node);
	
	/**
	 * M�thode appel�e lorsqu'un mot est valid� avec succ�s
	 * @param word
	 */
	void wordValidated(String word);
}
