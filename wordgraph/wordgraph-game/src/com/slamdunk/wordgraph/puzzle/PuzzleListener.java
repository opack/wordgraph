package com.slamdunk.wordgraph.puzzle;

import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Listener notifié lorsqu'un évènement se produit dans le puzzle
 */
public interface PuzzleListener {
	/**
	 * Méthode appelée une fois que le graphe a été chargé
	 * @param graph Le graphe qui vient d'être chargé
	 * @param puzzlePreferences	Les préférences qui contiennent diverses infos
	 */
	void graphLoaded(Graph graph, PuzzlePreferencesHelper puzzlePreferences);
	
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
	
	/**
	 * Méthode appelée lorsqu'un mot est rejeté
	 * @param word
	 */
	void wordRejected(String word);
	
	/**
	 * Méthode appelée lorsqu'une lettre est sélectionnée, c'est-à-dire que
	 * le joueur a touché cette lettre et qu'elle est allée dans la suggestion,
	 * même si le mot ne sera ensuite pas validé
	 * @param letter
	 */
	void letterSelected(String letter);
	
	/**
	 * Méthode appelée lorsqu'une lettre est désélectionnée, c'est-à-dire que
	 * le joueur a utilisé backspace pour la retirer de la suggestion
	 * @param letter
	 */
	void letterUnselected(String letter);

	/**
	 * Méthode appelée lorsque du temps s'est écoulé
	 * @param delta
	 */
	void timeElapsed(float delta);
}
