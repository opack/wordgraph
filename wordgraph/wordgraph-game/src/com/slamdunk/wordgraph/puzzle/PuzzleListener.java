package com.slamdunk.wordgraph.puzzle;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Listener notifi� lorsqu'un �v�nement se produit dans le puzzle
 */
public interface PuzzleListener {
	/**
	 * M�thode appel�e une fois que le puzzle a �t� charg�. Cela implique que le
	 * tous les composants ont �t� cr��s et charg�s, que le graphe a �t� charg�
	 * et que le layout du graphe a �t� fait. C'est la toute derni�re �tape
	 * avant que le joueur ne commence � jouer.
	 * @param graph Le graphe qui vient d'�tre charg�
	 * @param puzzlePreferences	Les pr�f�rences qui contiennent diverses infos
	 */
	void puzzleLoaded(PuzzleGraph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences);
	
	/**
	 * M�thode appel�e une fois que le graphe a �t� charg�. Cela signifie que le
	 * fichier properties du graphe a �t� lu et que le graphe a �t� charg� ({@link PuzzleGraph#load(java.util.List)}.
	 * @param graph Le graphe qui vient d'�tre charg�
	 * @param puzzlePreferences	Les pr�f�rences qui contiennent diverses infos
	 */
	void graphLoaded(PuzzleGraph graph);
	
	/**
	 * M�thode appel�e une fois qu'un lien est marqu� comme utilis�
	 * @param word
	 */
	void linkUsed(PuzzleLink link);
	
	/**
	 * M�thode appel�e une fois qu'un noeud est pass� � l'�tat invisible
	 * @param word
	 */
	void nodeHidden(PuzzleNode button);
	
	/**
	 * M�thode appel�e lorsqu'un mot est valid� avec succ�s
	 * @param word
	 */
	void wordValidated(String word);
	
	/**
	 * M�thode appel�e lorsqu'un mot est rejet�
	 * @param word
	 */
	void wordRejected(String word);
	
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
