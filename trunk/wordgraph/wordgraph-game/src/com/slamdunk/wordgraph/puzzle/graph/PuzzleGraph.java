package com.slamdunk.wordgraph.puzzle.graph;

import java.util.Arrays;
import java.util.List;

/**
 * Repr�sentation logique du graphe du puzzle
 */
public class PuzzleGraph {
	/**
	 * Contient la grille des lettres, pour indiquer leur position
	 */
	private String[][] grid;
	/**
	 * Contient l'ensemble des liens
	 */
	private DoubleEntryMap<String, String, Link> links;
	
	/**
	 * Cr�e le graph � partir des solutions fournies
	 */
	public void load(List<String> solutions) {
		
	}
	
	/**
	 * Choisit une position pour chaque lettre, en pr�f�rant la disposition
	 * fournie (le cas �ch�ant) au format :
	 * 	A?ERT
	 *  YUIOP
	 *  QSDFG
	 *  HJKLM
	 *  WXCV_
	 *  o� :
	 *  	- chaque lettre mentionn�e sera plac�e � sa position indiqu�e
	 *  	- le caract�re "_" marque un emplacement vide
	 *  	- le caract�re "?" marque un emplacement qui peut �tre occup�
	 *  	par n'importe quelle lettre.
	 *  Certains formats par d�faut sont utilis�s en fonction du nombre de
	 *  lettres diff�rentes. Ils sont fournis par la classe Layouts.
	 */
	public void layout(String[] layout) {
		// R�z grille
		for (String[] line : grid) {
			Arrays.fill(line, "");
		}
	}
}
