package com.slamdunk.wordgraph.puzzle.graph;

import java.util.Arrays;
import java.util.List;

/**
 * Représentation logique du graphe du puzzle
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
	 * Crée le graph à partir des solutions fournies
	 */
	public void load(List<String> solutions) {
		
	}
	
	/**
	 * Choisit une position pour chaque lettre, en préférant la disposition
	 * fournie (le cas échéant) au format :
	 * 	A?ERT
	 *  YUIOP
	 *  QSDFG
	 *  HJKLM
	 *  WXCV_
	 *  où :
	 *  	- chaque lettre mentionnée sera placée à sa position indiquée
	 *  	- le caractère "_" marque un emplacement vide
	 *  	- le caractère "?" marque un emplacement qui peut être occupé
	 *  	par n'importe quelle lettre.
	 *  Certains formats par défaut sont utilisés en fonction du nombre de
	 *  lettres différentes. Ils sont fournis par la classe Layouts.
	 */
	public void layout(String[] layout) {
		// Ràz grille
		for (String[] line : grid) {
			Arrays.fill(line, "");
		}
	}
}
