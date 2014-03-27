package com.slamdunk.wordgraph.puzzle.graph;

/**
 * Retourne des dispositions de lettre par défaut
 * en fonction du nombre de lettres différentes.
 */
public class LayoutFactory {
	public static final String SLOT_ANY = "?";
	public static final String SLOT_EMPTY = "_";
	
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
	private static final String[] L9 = {
		"_____",
		"_???_",
		"_???_",
		"_???_",
		"_____",
	};
	private static final String[] L12 = {
		"_???_",
		"_???_",
		"_???_",
		"_???_",
		"_____",
	};
	private static final String[] L15 = {
		"_____",
		"?????",
		"?????",
		"?????",
		"_____",
	};
	private static final String[] L20 = {
		"?????",
		"?????",
		"?????",
		"?????",
		"_____",
	};
	
	public String[] getLayout(int nbLetters) {
		if (nbLetters <= 6) {
			return L6;
		}
		if (nbLetters <= 9) {
			return L9;
		}
		if (nbLetters <= 12) {
			return L12;
		}
		if (nbLetters <= 15) {
			return L15;
		}
		if (nbLetters <= 20) {
			return L20;
		}
		return DEFAULT;
	}
}
