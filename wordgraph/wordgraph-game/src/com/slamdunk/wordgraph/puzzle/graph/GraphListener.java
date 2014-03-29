package com.slamdunk.wordgraph.puzzle.graph;

public interface GraphListener {
	/**
	 * M�thode appel�e lorsqu'un noeud est cr��
	 * @param node
	 */
	void nodeAdded(PuzzleNode node);
	
	/**
	 * M�thode appel�e lorsqu'un lien est cr��
	 * @param node
	 */
	void linkAdded(PuzzleLink link);
	
	/**
	 * M�thode appel�e lorsque la lettre d'un noeud
	 * est modifi�e
	 * @param node
	 */
	void nodeLetterUpdated(String oldLetter, PuzzleNode updatedNode);
}
