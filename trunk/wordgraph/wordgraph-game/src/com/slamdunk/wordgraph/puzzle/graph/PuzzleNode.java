package com.slamdunk.wordgraph.puzzle.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.puzzle.obstacles.Obstacle;

/**
 * Repr�sente un noeud dans le PuzzleGraph. Un noeud est compos� d'une lettre,
 * d'un bouton et d'un ensemble de liens
 */
public class PuzzleNode {
	private final String letter;
	private TextButton button;
	private Map<String, PuzzleLink> links;
	private List<Obstacle> obstacles;
	
	public PuzzleNode(String letter) {
		this.letter = letter;
		links = new HashMap<String, PuzzleLink>();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PuzzleLink)) {
			return false;
		}
		// Deux noeuds sont consid�r�s comme identique s'ils ont
				// la m�me lettre
		PuzzleNode node = (PuzzleNode)obj;
		return letter.equals(node.letter);
	}
	
	@Override
	public int hashCode() {
		return letter.hashCode();
	}
	
	public String getLetter() {
		return letter;
	}
	public TextButton getButton() {
		return button;
	}
	/**
	 * Associe un bouton � ce noeud.
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
	/**
	 * Retourne le lien vers la lettre indiqu�e
	 * @param letter
	 * @return
	 */
	public PuzzleLink getLink(String letter) {
		return links.get(letter);
	}
	
	/**
	 * Ajoute un obstacle connect� � ce noeud.
	 * @param link
	 */
	public void addObstacle(Obstacle obstacle) {
		if (obstacles == null) {
			obstacles = new ArrayList<Obstacle>();
		}
		obstacles.add(obstacle);
	}
	
	public void removeObstacle(Obstacle obstacle) {
		if (obstacles != null) {
			obstacles.remove(obstacle);
		}
	}
	
	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	/**
	 * Retourne true s'il reste au moins 1 lien menant � ce noeud
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
