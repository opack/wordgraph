package com.slamdunk.wordgraph.puzzle.graph;

/**
 * Représente le layout du puzzle. Seules les lettres sont indiquées
 * dans cette disposition.
 */
public class PuzzleLayout {
	private final int width;
	private final int height;
	private String[][] layout;
	
	public PuzzleLayout(int width, int height) {
		layout = new String[height][width];
		this.height = height;
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void setLetter(int line, int column, String letter) {
		layout[line][column] = letter;
	}
	
	public String getLetter(int line, int column) {
		return layout[line][column];
	}
}
