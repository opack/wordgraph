package com.slamdunk.wordgraph.puzzle.grid;

/**
 * Représente une grille de jeu.
 */
public class Grid {
	private GridCell[][] cells;
	
	public Grid(int nbLines, int nbColumns) {
		cells = new GridCell[nbLines][nbColumns];
	}

	/**
	 * Définit la cellule qui se trouve à l'emplacement indiqué.
	 * Les coordonnées stockées dans la cellule sont mises à jour.
	 * @param line
	 * @param column
	 * @param cell
	 */
	public void setCell(int line, int column, GridCell cell) {
		// Stocke la cellule dans la grille
		cells[line][column] = cell;
		
		// Met à jour les coordonnées de la cellule
		cell.setLine(line);
		cell.setColumn(column);
	}
	
	public GridCell getCell(int line, int column) {
		return cells[line][column];
	}

}
