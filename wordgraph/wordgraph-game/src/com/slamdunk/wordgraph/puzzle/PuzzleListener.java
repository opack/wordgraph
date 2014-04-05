package com.slamdunk.wordgraph.puzzle;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Listener notifié lorsqu'un évènement se produit dans le puzzle
 */
public interface PuzzleListener {
	/**
	 * Méthode appelée une fois que le puzzle a été chargé. Cela implique que le
	 * tous les composants ont été créés et chargés. C'est la toute dernière étape
	 * avant que le joueur ne commence à jouer.
	 * @param grid La grille qui vient d'être chargée
	 * @param puzzlePreferences	Les préférences qui contiennent diverses infos
	 */
	void puzzleLoaded(Grid grid, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences);
	
	/**
	 * Méthode appelée lorsqu'un mot est validé avec succès
	 * @param word
	 */
	void wordValidated(String word, List<GridCell> cells);
	
	/**
	 * Méthode appelée lorsqu'un mot est rejeté
	 * @param word
	 */
	void wordRejected(String word, List<GridCell> cells);
	
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
