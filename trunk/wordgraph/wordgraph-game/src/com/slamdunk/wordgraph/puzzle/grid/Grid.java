package com.slamdunk.wordgraph.puzzle.grid;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * Repr�sente une grille de jeu.
 */
public class Grid {
	private static final int[/*LIGNE*/][/*COLONNE*/] NEIGHBORS = new int[][] {
		{-1, -1},
		{-1, 0},
		{-1, +1},
		{0, -1},
		{0, +1},
		{+1, -1},
		{+1, 0},
		{+1, +1},
	};
	private GridCell[][] cells;
	private int nbLines;
	private int nbColumns;
	
	public Grid(int nbLines, int nbColumns) {
		this.nbLines = nbLines;
		this.nbColumns = nbColumns;
		cells = new GridCell[nbLines][nbColumns];
	}

	/**
	 * D�finit la cellule qui se trouve � l'emplacement indiqu�
	 * dans celle-ci.
	 * @param line
	 * @param column
	 * @param cell
	 */
	public void putCell(GridCell cell) {
		// Stocke la cellule dans la grille
		cells[cell.getLine()][cell.getColumn()] = cell;
	}
	
	public GridCell getCell(int line, int column) {
		if (line < 0 || line >= nbLines
		|| column < 0 || column >= nbColumns) {
			return null;
		}
		return cells[line][column];
	}
	
	public int getNbLines() {
		return nbLines;
	}

	public int getNbColumns() {
		return nbColumns;
	}

	/**
	 * Retourne les cellules permettant de former le mot indiqu�.
	 * La solution retourn�e est UNE des possibilit�s.
	 * @param word
	 * @return Liste de cellules, �ventuellement vide si le mot
	 * complet n'a pas �t� trouv�
	 */
	public List<GridCell> findWord(String word) {
		// Pr�paration des donn�es pour la recherche du mot
		List<GridCell> selectedCells = new ArrayList<GridCell>();
		List<String> letters = new ArrayList<String>();
		final int length = word.length();
		for (int index = 0; index < length; index++) {
			letters.add(word.substring(index, index + 1));
		}

		// Recherche d'une cellule dans la grille qui contient
		// la premi�re lettre du mot
		final String firstLetter = letters.get(0);
		for (GridCell[] line : cells) {
			for (GridCell cell : line) {
				// Si on l'a trouv�e, on tente de trouver le reste du mot
				if (cell.getLetter().equals(firstLetter)) {
					selectedCells.add(cell);
					if (findWord(letters, 1, selectedCells, 0)) {
						// La totalit� du mot a �t� trouv� ! On peut
						// retourner le r�sultat
						return selectedCells;
					}
					// Le mot n'a pas �t� trouv�, on recommence
					selectedCells.clear();
				}
			}
		}
		return selectedCells;
	}

	private boolean findWord(List<String> letters, int nextLetterIndex, List<GridCell> selectedCells, int previousCellIndex) {
		// Si on a finit le mot, on a trouv� notre solution !
		if (nextLetterIndex == letters.size()) {
			return true;
		}
		
		// R�cup�re la derni�re cellule trouv�e, qui devient donc
		// notre point de d�part pour la recherche
		GridCell cell = selectedCells.get(previousCellIndex);
		// R�cup�re la prochaine lettre � trouver
		String wanted = letters.get(nextLetterIndex);
		
		// Recherche la lettre souhait�e dans les voisins de la derni�re cellule
		GridCell neighbor;
		for (int[] offset : NEIGHBORS) {
			// R�cup�re la cellule voisine
			neighbor = getCell(cell.getLine() + offset[0], cell.getColumn() + offset[1]);
			// Teste si la lettre correspond � celle qu'on recherche
			// et si elle n'a pas d�j� �t� s�lectionn�e
			if (neighbor != null
			&& neighbor.getLetter().equals(wanted)
			&& !selectedCells.contains(neighbor)) {
				// La lettre correspond : on la met de c�t�
				selectedCells.add(neighbor);
				// et on poursuit la recherche avec la lettre suivante dans le mot
				if (findWord(letters, nextLetterIndex + 1, selectedCells, previousCellIndex + 1)) {
					return true;
				}
				// Finalement on n'a pas pu finir le mot : on retire la lettre de la s�lection
				selectedCells.remove(neighbor);
					
			}
		}
		// Si on arrive ici, c'est qu'on n'a pas trouv� la lettre recherch�e
		return false;
	}

	public GridCell getCell(Button button) {
			if (button != null) {
			for (GridCell[] line : cells) {
				for (GridCell cell : line) {
					if (button.equals(cell.getButton())) {
						return cell;
					}
				}
			}
		}
		return null;
	}
}
