package com.slamdunk.wordgraph.puzzle.graph;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.puzzle.ObstacleTarget;

/**
 * Représente un noeud dans le PuzzleGraph. Un noeud est composé d'une lettre,
 * d'un bouton et d'un ensemble de liens
 */
public class PuzzleNode extends ObstacleTarget {
	private PuzzleGraph graph;
	private String letter;
	private TextButton button;
	private Map<String, PuzzleLink> links;
	
	public PuzzleNode(PuzzleGraph graph, String letter) {
		this.graph = graph;
		this.letter = letter;
		links = new HashMap<String, PuzzleLink>();
	}
	
	public PuzzleGraph getGraph() {
		return graph;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PuzzleNode)) {
			return false;
		}
		// Deux noeuds sont considérés comme identique s'ils ont
				// la même lettre
		PuzzleNode node = (PuzzleNode)obj;
		return letter.equals(node.letter);
	}
	
	@Override
	public int hashCode() {
		return letter.hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(links.size() + 1);
		sb.append(letter);
		sb.append("[");
		for(String connectedLetter : links.keySet()) {
			sb.append(connectedLetter);
		}
		sb.append("]");
		return sb.toString();
	}
	
	public String getLetter() {
		return letter;
	}
	
	public void setLetter(String letter) {
		this.letter = letter;
	}

	public TextButton getButton() {
		return button;
	}
	/**
	 * Associe un bouton à ce noeud.
	 * @param button
	 */
	public void setButton(TextButton button) {
		this.button = button;
	}
	public Map<String, PuzzleLink> getLinks() {
		return links;
	}
	public void setLink(String letter, PuzzleLink link) {
		links.put(letter, link);
	}
	public void removeLink(String letter) {
		links.remove(letter);
	}
	public void updateLink(String oldLetter, String newLetter) {
		PuzzleLink link = links.remove(oldLetter);
		links.put(newLetter, link);
	}
	/**
	 * Retourne le lien vers la lettre indiquée
	 * @param letter
	 * @return
	 */
	public PuzzleLink getLink(String letter) {
		return links.get(letter);
	}
	
	/**
	 * Retourne true s'il reste au moins 1 lien menant à ce noeud
	 * @return
	 */
	public boolean isReachable() {
		for (PuzzleLink link : links.values()) {
			if (link.getSize() > 0) {
				return true;
			}
		}
		return false;
	}
}
