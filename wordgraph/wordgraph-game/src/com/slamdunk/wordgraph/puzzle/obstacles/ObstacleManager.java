package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.Graph;

public class ObstacleManager<O extends Obstacle> {
	private List<O> obstacles;
	
	public ObstacleManager(){
		obstacles = new ArrayList<O>();
	}
	
	public void add(O obstacle) {
		obstacles.add(obstacle);
	}
	
	public List<O> getObstacles() {
		return obstacles;
	}

	/**
	 * Initialise l'effet des obstacles
	 * @param graph
	 */
	public void init(Graph graph) {
		for (Obstacle obstacle : obstacles) {
        	if (obstacle.isActive()) {
        		obstacle.init(graph);
        	}
        }
	}
	
	/**
	 * Applique l'effet des obstacles
	 * @param graph
	 */
	public void applyEffect(Graph graph) {
		for (Obstacle obstacle : obstacles) {
        	if (obstacle.isActive()) {
        		obstacle.applyEffect(graph);
        	}
        }
	}
}
