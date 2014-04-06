package com.slamdunk.wordgraph.effect;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.slamdunk.wordgraph.puzzle.PuzzleScreen;

/**
 * Effet visuel
 */
public interface VisualEffect {

	/**
	 * Initialise l'effet
	 * @param puzzleScreen
	 */
	void init(PuzzleScreen puzzleScreen);

	/**
	 * Met � jour et dessine l'effet
	 * @param delta Temps �coul� depuis le dernier appel
	 * @return true si update() doit continuer � �tre appel�e,
	 * false si l'effet est termin�
	 */
	boolean update(SpriteBatch batch, float delta);
}