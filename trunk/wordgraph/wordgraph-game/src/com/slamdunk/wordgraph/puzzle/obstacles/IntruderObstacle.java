package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphEdge;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Ne sert à rien à part noter qu'une lettre ne sert à aucun mot
 */
public class IntruderObstacle implements Obstacle{
	private IntruderObstacleManager manager;
	private String letter;
	private boolean isActive;
	private GraphNode node;
	
	public IntruderObstacle(String letter) {
		this.letter = letter;
		isActive = true;
	}

	public void setManager(IntruderObstacleManager manager) {
		this.manager = manager;
	}


	@Override
	public void applyEffect(Graph graph) {
		// Rien de spécial à faire
	}

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public ObstaclesTypes getType() {
		return ObstaclesTypes.INTRUDER;
	}
	
	public String getLetter() {
		return letter;
	}

	@Override
	public void init(Graph graph) {
		// Récupère la lettre intruse
		node = graph.getNode(letter);
		if (node == null) {
			// Aucun node n'existe avec cette lettre, on désactive l'obstacle
			isActive = false;
		}
	}
}
