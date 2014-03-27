package com.slamdunk.wordgraph.puzzle.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.slamdunk.utils.DoubleEntryArray;

/**
 * Représentation logique du graphe du puzzle
 */
public class PuzzleGraph {
	/**
	 * Taille d'un côté de la grille de lettres
	 */
	private static final int GRID_SIZE = 5;
	
	/**
	 * Contient la grille des lettres, pour indiquer leur position
	 */
	private String[][] grid;
	
	/**
	 * Contient l'ensemble des liens
	 */
	private DoubleEntryArray<String, String, Link> links;
	
	public PuzzleGraph() {
		grid = new String[GRID_SIZE][GRID_SIZE];
		links = new DoubleEntryArray<String, String, Link>();
	}
	
	/**
	 * Crée le graph à partir des solutions fournies
	 */
	public void load(List<String> solutions) {
		// Vide la table des liens
		links.clear();
		// Lit les solutions pour remplir la table des liens
		for (String solution : solutions) {
			parseSolution(solution);
		}
	}
	
	/**
	 * Extrait de la solution indiquée les lettres et les liens utilisés
	 * @param solution
	 */
	private void parseSolution(String solution) {
		// S'il n'y a pas au moins 2 lettres, alors il ne peut pas y avoir
		// de lien
		final int length = solution.length();
		if (length < 2) {
			return;
		}
		// Parcourt les lettres 2 à 2
		for (int cur = 1; cur < length; cur++) {
			// Récupération des 2 lettres
			String letter1 = solution.substring(cur - 1, cur);
			String letter2 = solution.substring(cur, cur + 1);
			
			// Récupère l'éventuel lien déjà stocké
			Link link = links.get(letter1, letter2);
			
			// Si ce lien existe, on indique qu'il y en a une seconde occurence
			if (link != null) {
				link.setSize(link.getSize() + 1);
			}
			// Sinon, on le crée
			else {
				// Création du lien et initialisation de la taille
				link = new Link();
				link.setEndpoint1(letter1);
				link.setEndpoint2(letter2);
				link.setSize(1);
				
				// Stocke le lien pour les 2 sens de navigation possibles,
				// de façon à ce qu'on puisse le récupérer en demandant
				// lettre1 -> lettre2 ou lettre1 <- lettre2.
				links.put(letter1, letter2, link);
				links.put(letter2, letter1, link);
			}
		}
	}
	
	/**
	 * Arrange les lettres du graphe dans une disposition par défaut
	 * en fonction du nombre de lettres du graphe.
	 */
	public void layout() {
		// Récupération des lettres
		Set<String> letters = links.getKeys1();
		// Récupération d'un layout pour les lettres disponibles
		LayoutFactory layouts = new LayoutFactory();
		layout(layouts.getLayout(letters.size()));
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
		
		// Récupération des lettres
		Set<String> lettersToArrange = new HashSet<String>(links.getKeys1());
		
		// Place les lettres fixées du layout
		for (int curLine = 0; curLine < layout.length; curLine ++) {
			String line = layout[curLine];
			int length = line.length();
			for (int curColumn = 0; curColumn < length; curColumn++) {
				// Récupération de la lettre du layout
				String letter = line.substring(curColumn, curColumn + 1);
				// Si la lettre mentionnée dans le layout reste à placer,
				// on la place et on indique qu'elle n'est plus à placer
				if (lettersToArrange.contains(letter)) {
					grid[curLine][curColumn] = letter;
					lettersToArrange.remove(letter);
				}
			}
		}
		
		// Remplit les emplacements restants du layout
		if (!lettersToArrange.isEmpty()) {
			// On crée une liste avec les lettres restantes et on la mélange
			// de façon à y piocher les lettres de façon aléatoire.
			List<String> letters = new ArrayList<String>(lettersToArrange);
			Collections.shuffle(letters);
			for (int curLine = 0; curLine < layout.length; curLine ++) {
				String line = layout[curLine];
				int length = line.length();
				for (int curColumn = 0; curColumn < length; curColumn++) {
					// Récupération du symbole du layout
					String symbol = line.substring(curColumn, curColumn + 1);
					// S'il s'agit d'un emplacement à contenu aléatoire,
					// on prend une lettre restante au hasard et on la place
					if (LayoutFactory.SLOT_ANY.equals(symbol)) {
						// Pour économiser un peu de CPU, on retire les éléments de
						// la liste par la fin.
						String letter = letters.remove(letters.size() - 1);
						grid[curLine][curColumn] = letter;
						// S'il ne reste plus de lettres à placer, on a finit le layout
						if (letters.isEmpty()) {
							return;
						}
					}
				}
			}
			
			// S'il reste des lettres, alors le layout n'est pas bon !
			if (!letters.isEmpty()) {
				throw new IllegalStateException("Some letters were not placed in the puzzle " + letters + ". The puzzle will be invalid. Check the layout.");
			}
		}
	}
	public static void main(String[] args) {
		PuzzleGraph graph = new PuzzleGraph();
		List<String> solutions = new ArrayList<String>();
		solutions.add("MURMURER");
		solutions.add("PRIER");
		solutions.add("DOUCE");
		solutions.add("CACHOT");
		solutions.add("RANGER");
		graph.load(solutions);
		
		String[] layout = {
				"?_?_?",
				"?_?_?",
				"?_M_?",
				"?_?_?",
				"?_?_?"
			};
		graph.layout(layout);
		for (String[] line : graph.grid) {
			for (String letter : line) {
				if (letter.isEmpty()) {
					System.out.print("_");
				} else {
					System.out.print(letter);
				}
			}
			System.out.println();
		}
	}
}
