package com.slamdunk.wordgraph.puzzle.grid;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Button;

/**
 * Représente une grille de jeu.
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
	 * Définit la cellule qui se trouve à l'emplacement indiqué
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
	 * Retourne les cellules permettant de former le mot indiqué.
	 * La solution retournée est UNE des possibilités.
	 * @param word
	 * @return Liste de cellules, éventuellement vide si le mot
	 * complet n'a pas été trouvé
	 */
	public List<GridCell> findWord(String word) {
		// Préparation des données pour la recherche du mot
		List<GridCell> selectedCells = new ArrayList<GridCell>();
		List<String> letters = new ArrayList<String>();
		final int length = word.length();
		for (int index = 0; index < length; index++) {
			letters.add(word.substring(index, index + 1));
		}

		// Recherche d'une cellule dans la grille qui contient
		// la première lettre du mot
		final String firstLetter = letters.get(0);
		for (GridCell[] line : cells) {
			for (GridCell cell : line) {
				// Si on l'a trouvée, on tente de trouver le reste du mot
				if (cell.getLetter().equals(firstLetter)) {
					selectedCells.add(cell);
					if (findWord(letters, 1, selectedCells, 0)) {
						// La totalité du mot a été trouvé ! On peut
						// retourner le résultat
						return selectedCells;
					}
					// Le mot n'a pas été trouvé, on recommence
					selectedCells.clear();
				}
			}
		}
		return selectedCells;
	}

	private boolean findWord(List<String> letters, int nextLetterIndex, List<GridCell> selectedCells, int previousCellIndex) {
		// Si on a finit le mot, on a trouvé notre solution !
		if (nextLetterIndex == letters.size()) {
			return true;
		}
		
		// Récupère la dernière cellule trouvée, qui devient donc
		// notre point de départ pour la recherche
		GridCell cell = selectedCells.get(previousCellIndex);
		// Récupère la prochaine lettre à trouver
		String wanted = letters.get(nextLetterIndex);
		
		// Recherche la lettre souhaitée dans les voisins de la dernière cellule
		GridCell neighbor;
		for (int[] offset : NEIGHBORS) {
			// Récupère la cellule voisine
			neighbor = getCell(cell.getLine() + offset[0], cell.getColumn() + offset[1]);
			// Teste si la lettre correspond à celle qu'on recherche
			// et si elle n'a pas déjà été sélectionnée
			if (neighbor != null
			&& neighbor.getLetter().equals(wanted)
			&& !selectedCells.contains(neighbor)) {
				// La lettre correspond : on la met de côté
				selectedCells.add(neighbor);
				// et on poursuit la recherche avec la lettre suivante dans le mot
				if (findWord(letters, nextLetterIndex + 1, selectedCells, previousCellIndex + 1)) {
					return true;
				}
				// Finalement on n'a pas pu finir le mot : on retire la lettre de la sélection
				selectedCells.remove(neighbor);
					
			}
		}
		// Si on arrive ici, c'est qu'on n'a pas trouvé la lettre recherchée
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
