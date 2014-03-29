package com.slamdunk.wordgraph.puzzle.graph;

public interface GraphListener {
	/**
	 * Méthode appelée lorsqu'un noeud est créé
	 * @param node
	 */
	void nodeAdded(PuzzleNode node);
	
	/**
	 * Méthode appelée lorsqu'un lien est créé
	 * @param node
	 */
	void linkAdded(PuzzleLink link);
	
	/**
	 * Méthode appelée lorsque la lettre d'un noeud
	 * est modifiée
	 * @param node
	 */
	void nodeLetterUpdated(String oldLetter, PuzzleNode updatedNode);
}
