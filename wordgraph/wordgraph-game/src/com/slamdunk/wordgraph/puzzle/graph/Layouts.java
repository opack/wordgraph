package com.slamdunk.wordgraph.puzzle.graph;

/**
 * Retourne des dispositions de lettre par défaut
 * en fonction du nombre de lettres différentes.
 */
public class Layouts {
	private static final String[] DEFAULT = {
		"?????",
		"?????",
		"?????",
		"?????",
		"?????",
	};
	private static final String[] L6 = {
		"_____",
		"_???_",
		"_???_",
		"_____",
		"_____",
	};
	
	public String[] getLayout(int nbLetters) {
		if (nbLetters <= 6) {
			return L6;
		}
		return DEFAULT;
	}
}
