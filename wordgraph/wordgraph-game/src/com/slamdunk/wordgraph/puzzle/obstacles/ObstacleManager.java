package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.HashMap;
import java.util.Map;

import com.slamdunk.wordgraph.puzzle.graph.Graph;

public class ObstacleManager<O extends Obstacle> {
	private Map<String, O> obstacles;
	
	public ObstacleManager(){
		obstacles = new HashMap<String, O>();
	}
	
	public void add(String target, O obstacle) {
		obstacles.put(target, obstacle);
	}
	
	public Map<String, O> getObstacles() {
		return obstacles;
	}

	/**
	 * Initialise l'effet des obstacles
	 * @param graph
	 */
	public void init(Graph graph) {
		for (Obstacle obstacle : obstacles.values()) {
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
		for (Obstacle obstacle : obstacles.values()) {
        	if (obstacle.isActive()) {
        		obstacle.applyEffect(graph);
        	}
        }
	}
}
