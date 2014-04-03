package com.slamdunk.wordgraph.puzzle.grid;

/**
 * Repr�sente une grille de jeu.
 */
public class Grid {
	private GridCell[][] cells;
	
	public Grid(int nbLines, int nbColumns) {
		cells = new GridCell[nbLines][nbColumns];
	}

	/**
	 * D�finit la cellule qui se trouve � l'emplacement indiqu�.
	 * Les coordonn�es stock�es dans la cellule sont mises � jour.
	 * @param line
	 * @param column
	 * @param cell
	 */
	public void setCell(int line, int column, GridCell cell) {
		// Stocke la cellule dans la grille
		cells[line][column] = cell;
		
		// Met � jour les coordonn�es de la cellule
		cell.setLine(line);
		cell.setColumn(column);
	}
	
	public GridCell getCell(int line, int column) {
		return cells[line][column];
	}

}
