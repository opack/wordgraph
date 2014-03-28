package com.slamdunk.wordgraph.puzzle;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Listener notifié lorsqu'un évènement se produit dans le puzzle
 */
public interface PuzzleListener {
	/**
	 * Méthode appelée une fois que le puzzle a été chargé. Cela implique que le
	 * tous les composants ont été créés et chargés, que le graphe a été chargé
	 * et que le layout du graphe a été fait. C'est la toute dernière étape
	 * avant que le joueur ne commence à jouer.
	 * @param graph Le graphe qui vient d'être chargé
	 * @param puzzlePreferences	Les préférences qui contiennent diverses infos
	 */
	void puzzleLoaded(PuzzleGraph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences);
	
	/**
	 * Méthode appelée une fois que le graphe a été chargé. Cela signifie que le
	 * fichier properties du graphe a été lu et que le graphe a été chargé ({@link PuzzleGraph#load(java.util.List)}.
	 * @param graph Le graphe qui vient d'être chargé
	 * @param puzzlePreferences	Les préférences qui contiennent diverses infos
	 */
	void graphLoaded(PuzzleGraph graph);
	
	/**
	 * Méthode appelée une fois qu'un lien est marqué comme utilisé
	 * @param word
	 */
	void linkUsed(PuzzleLink link);
	
	/**
	 * Méthode appelée une fois qu'un noeud est passé à l'état invisible
	 * @param word
	 */
	void nodeHidden(PuzzleNode button);
	
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
