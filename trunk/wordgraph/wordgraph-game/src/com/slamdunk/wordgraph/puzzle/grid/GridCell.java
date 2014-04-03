package com.slamdunk.wordgraph.puzzle.grid;

import static com.slamdunk.wordgraph.puzzle.LetterStates.NORMAL;
import static com.slamdunk.wordgraph.puzzle.LetterStates.SELECTED;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.puzzle.LetterStates;
import com.slamdunk.wordgraph.puzzle.ObstacleTarget;
import com.slamdunk.wordgraph.puzzle.PuzzleButtonDecorator;

/**
 * Représente une celleule de la grille
 * @author didem93n
 *
 */
public class GridCell extends ObstacleTarget {
	/**
	 * Lettre associée à cette cellule
	 */
	private String letter;
	/**
	 * Bouton présentant cette lettre
	 */
	private TextButton button;
	/**
	 * Ligne de la grille dans laquelle se trouve cette lettre
	 */
	private int line;
	/**
	 * Colonne de la grille dans laquelle se trouve cette lettre
	 */
	private int column;
	/**
	 * Etat de la cellule
	 */
	private LetterStates state;
	
	public GridCell(String letter) {
		this.letter = letter;
		state = NORMAL;
	}
	
	public String getLetter() {
		return letter;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public TextButton getButton() {
		return button;
	}
	public void setButton(TextButton button) {
		this.button = button;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}

	public LetterStates getState() {
		return state;
	}
	public void setState(LetterStates state) {
		this.state = state;
	}
	
	public void select() {
		setState(SELECTED);
		PuzzleButtonDecorator.getInstance().setStyle(this, SELECTED);
	}
	public void unselect() {
		setState(NORMAL);
		PuzzleButtonDecorator.getInstance().setStyle(this, NORMAL);
	}
	public boolean isSelected() {
		return state == SELECTED;
	}

	/**
	 * Indique si la cellule fournie est un voisin direct de celle-ci.
	 * @param last
	 * @return
	 */
	public boolean isAround(GridCell other) {
		int deltaX = Math.abs(column - other.column);
        int deltaY = Math.abs(line - other.line);
		return deltaX <= 1 && deltaY <= 1;
	}
}
