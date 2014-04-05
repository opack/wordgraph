package com.slamdunk.wordgraph.puzzle.obstacles;

import static com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes.BOMB;
import static com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes.CATEGORY;
import static com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes.FOG;
import static com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes.HIDDEN;
import static com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes.INTRUDER;
import static com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes.ISLE;
import static com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes.MORPH;
import static com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes.STONE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleListener;
import com.slamdunk.wordgraph.puzzle.graph.DELETE.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.DELETE.PuzzleNode;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

public class ObstacleManager implements PuzzleListener {
	private static final ObstaclesTypes[] CLUE_OBSTACLE_TYPES = {
		CATEGORY,
		HIDDEN
	};
	
	private static final ObstaclesTypes[] LETTER_OBSTACLE_TYPES = {
		BOMB,
		FOG,
		INTRUDER,
		ISLE,
		MORPH,
		STONE
	};
	
	private Grid grid;
	private PuzzleAttributes puzzleAttributes;
	private Stage stage;
	
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
	 * Crée une liste avec les obstacles actifs du type indiqué
	 * @param types
	 * @return
	 */
	public List<Obstacle> getActiveObstacles(ObstaclesTypes... types) {
		List<Obstacle> letterObstacles = new ArrayList<Obstacle>();
		for (ObstaclesTypes type : types) {
			List<Obstacle> obstacles = obstaclesByType.get(type);
			for (Obstacle obstacle : obstacles) {
				if (obstacle.isActive()) {
					letterObstacles.add(obstacle);
				}
			}
		}
		return letterObstacles;
	}

	/**
	 * Applique l'effet des obstacles
	 * @param graph
	 */
	public void applyEffect() {
		for (Obstacle obstacle : getTempObstaclesList()) {
    		obstacle.applyEffect(grid);
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

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public PuzzleAttributes getPuzzleAttributes() {
		return puzzleAttributes;
	}

	public void setPuzzleAttributes(PuzzleAttributes puzzleAttributes) {
		this.puzzleAttributes = puzzleAttributes;
	}
	
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}


	@Override
	public void puzzleLoaded(Grid grid, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		setPuzzleAttributes(puzzleAttributes);
		setStage(stage);
		setGrid(grid);
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.puzzleLoaded(grid, puzzleAttributes, stage, puzzlePreferences);
        	}
        }
	}

	@Override
	public void linkUsed(PuzzleLink link) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.linkUsed(link);
        	}
        }
	}
	
	@Override
	public void wordValidated(String word, List<GridCell> cells) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.wordValidated(word, cells);
        	}
        }
	}

	@Override
	public void nodeHidden(PuzzleNode node) {
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
	public void wordRejected(String word, List<GridCell> cells) {
		for (Obstacle obstacle : getTempObstaclesList()) {
        	if (obstacle.isActive()) {
        		obstacle.wordRejected(word, cells);
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

	/**
	 * Indique s'il y a au moins 1 obstacle applicable à une lettre
	 * encore actif.
	 * @return
	 */
	public boolean hasLetterObstacles() {
		return hasActiveObstacles(LETTER_OBSTACLE_TYPES);
	}
	
	/**
	 * Indique s'il y a au moins 1 obstacle applicable à un indice
	 * encore actif.
	 * @return
	 */
	public boolean hasClueObstacles() {
		return hasActiveObstacles(CLUE_OBSTACLE_TYPES);
	}

	private boolean hasActiveObstacles(ObstaclesTypes... types) {
		for (ObstaclesTypes type : types) {
			List<Obstacle> obstacles = obstaclesByType.get(type);
			for (Obstacle obstacle : obstacles) {
				if (obstacle.isActive()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retourne la liste des obstacles actifs appliqués sur des lettres
	 * @return
	 */
	public List<Obstacle> getLetterObstacles() {
		return getActiveObstacles(LETTER_OBSTACLE_TYPES);
	}
	
	/**
	 * Retourne la liste des obstacles actifs appliqués sur des lettres
	 * @return
	 */
	public List<Obstacle> getClueObstacles() {
		return getActiveObstacles(CLUE_OBSTACLE_TYPES);
	}
}
