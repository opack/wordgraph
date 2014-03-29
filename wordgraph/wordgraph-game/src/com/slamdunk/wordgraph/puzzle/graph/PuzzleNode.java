package com.slamdunk.wordgraph.puzzle.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.puzzle.obstacles.Obstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes;

/**
 * Repr�sente un noeud dans le PuzzleGraph. Un noeud est compos� d'une lettre,
 * d'un bouton et d'un ensemble de liens
 */
public class PuzzleNode {
	private PuzzleGraph graph;
	private String letter;
	private TextButton button;
	private Map<String, PuzzleLink> links;
	private List<Obstacle> obstacles;
	
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
		// Deux noeuds sont consid�r�s comme identique s'ils ont
				// la m�me lettre
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
	public void updateLink(String oldLetter, String newLetter) {
		PuzzleLink link = links.remove(oldLetter);
		links.put(newLetter, link);
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
	 * Indique s'il existe un obstacle actif du type indiqu� qui a ce
	 * noeud pour cible.
	 * @param obstacleManager
	 * @param letter
	 * @return
	 */
	public boolean isTargeted(ObstaclesTypes type) {
		if (obstacles != null) {
			for (Obstacle obstacle : obstacles) {
				if (obstacle.getType() == type
				&& obstacle.isActive()
				&& obstacle.getTarget().equals(letter)) {
						return true;
				}
			}
		}
		return false;
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