package com.slamdunk.wordgraph.puzzle;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Listener notifi� lorsqu'un �v�nement se produit dans le puzzle
 */
public interface PuzzleListener {
	/**
	 * M�thode appel�e une fois que le puzzle a �t� charg�. Cela implique que le
	 * tous les composants ont �t� cr��s et charg�s. C'est la toute derni�re �tape
	 * avant que le joueur ne commence � jouer.
	 * @param grid La grille qui vient d'�tre charg�e
	 * @param puzzlePreferences	Les pr�f�rences qui contiennent diverses infos
	 */
	void puzzleLoaded(Grid grid, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences);
	
	/**
	 * M�thode appel�e lorsqu'un mot est valid� avec succ�s
	 * @param word
	 */
	void wordValidated(String word, List<GridCell> cells);
	
	/**
	 * M�thode appel�e lorsqu'un mot est rejet�
	 * @param word
	 */
	void wordRejected(String word, List<GridCell> cells);
	
	/**
	 * M�thode appel�e lorsqu'une lettre est s�lectionn�e, c'est-�-dire que
	 * le joueur a touch� cette lettre et qu'elle est all�e dans la suggestion,
	 * m�me si le mot ne sera ensuite pas valid�
	 * @param letter
	 */
	void letterSelected(String letter);
	
	/**
	 * M�thode appel�e lorsqu'une lettre est d�s�lectionn�e, c'est-�-dire que
	 * le joueur a utilis� backspace pour la retirer de la suggestion
	 * @param letter
	 */
	void letterUnselected(String letter);

	/**
	 * M�thode appel�e lorsque du temps s'est �coul�
	 * @param delta
	 */
	void timeElapsed(float delta);
}
