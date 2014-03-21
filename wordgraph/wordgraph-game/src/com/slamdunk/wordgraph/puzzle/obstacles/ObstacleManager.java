package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleListener;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

public class ObstacleManager implements PuzzleListener {
	private Graph graph;
	private Map<ObstaclesTypes, List<Obstacle>> obstaclesByType;
	private List<Obstacle> obstacles;
	private List<Obstacle> tmpObstacles;
	
	public ObstacleManager(){
		obstacles = new ArrayList<Obstacle>();
		tmpObstacles = new ArrayList<Obstacle>();
		obstaclesByType = new HashMap<ObstaclesTypes, List<Obstacle>>();
	}
	
	public void add(Obstacle obstacle) {
		obstacle.setManager(this);
		obstacles.add(obstacle);
		
		ObstaclesTypes type = obstacle.getType();
		List<Obstacle> listForType = obstaclesByType.get(type);
		if (listForType == null) {
			listForType = new ArrayList<Obstacle>();
			obstaclesByType.put(type, listForType);
		}
		listForType.add(obstacle);
	}
	
	public void remove(Obstacle obstacle) {
		obstacles.remove(obstacle);
		
		List<Obstacle> listForType = obstaclesByType.get(obstacle.getType());
		if (listForType != null) {
			listForType.remove(obstacle);
		}
	}
	
	public List<Obstacle> getObstacles() {
		return obstacles;
	}
	
	public List<Obstacle> getObstacles(ObstaclesTypes type) {
		return obstaclesByType.get(type);
	}

	/**
	 * Applique l'effet des obstacles
	 * @param graph
	 */
	public void applyEffect() {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.applyEffect(graph);
        	}
        }
	}
	
	/**
	 * Retourne une liste contenant les obstacles de ce manager.
	 * Cette méthode est utile pour les notifiers car elle permet aux
	 * obstacles d'ajouter d'autres obstacles pendant le parcours de
	 * la liste initiale et d'éviter ainsi les ConcurrentModification
	 * @return
	 */
	private List<Obstacle> getTempObstaclesList() {
		tmpObstacles.clear();
		tmpObstacles.addAll(obstacles);
		return tmpObstacles;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	@Override
	public void graphLoaded(Graph graph, PuzzlePreferencesHelper puzzlePreferences) {
		setGraph(graph);
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.graphLoaded(graph, puzzlePreferences);
        	}
        }
	}

	@Override
	public void linkUsed(GraphLink link) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.linkUsed(link);
        	}
        }
	}
	
	@Override
	public void wordValidated(String word) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.wordValidated(word);
        	}
        }
	}

	@Override
	public void nodeHidden(GraphNode node) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.nodeHidden(node);
        	}
        }
	}

	@Override
	public void letterSelected(String letter) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.letterSelected(letter);
        	}
        }
	}
	
	@Override
	public void timeElapsed(float delta) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.timeElapsed(delta);
        	}
        }
	}

	@Override
	public void wordRejected(String word) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.wordRejected(word);
        	}
        }
	}

	@Override
	public void letterUnselected(String letter) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.letterUnselected(letter);
        	}
        }
	}
}
