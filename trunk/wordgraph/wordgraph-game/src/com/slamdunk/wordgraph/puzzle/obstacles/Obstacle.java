package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleListener;
import com.slamdunk.wordgraph.puzzle.graph.DELETE.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.DELETE.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.DELETE.PuzzleNode;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Représente un obstacle qui peut gêner le joueur.
 */
public abstract class Obstacle implements PuzzleListener {
	private ObstacleManager manager;
	private boolean isActive;
	private ObstaclesTypes type;
	private String target;
	private PuzzlePreferencesHelper puzzlePreferences;
	
	public Obstacle() {
		this.isActive = true;
	}
	
	public void setType(ObstaclesTypes type) {
		this.type = type;
	}

	/**
	 * Applique l'effet de l'obstacle sur le graphe.
	 * Cette méthode doit être appelée à chaque rafraîchissement
	 * du graphe.
	 * L'effet ne devrait être appliqué que si l'obstacle est
	 * toujours actif.
	 * @param graph
	 * @see #isActive()
	 */
	public abstract void applyEffect(Grid grid);
	
	/**
	 * Indique si l'obstacle est toujours actif ou non.
	 * @return
	 */
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * Indique si l'image de l'obstacle doit être dessinée ou
	 * non. Le comportement par défaut répond true si isActive
	 * @return
	 */
	public boolean isObstacleDrawn() {
		return isActive;
	}
	
	/**
	 * Définit si l'obstacle est actif ou non
	 * @param isActive
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * Retourne le type de l'obstacle
	 * @return
	 */
	public ObstaclesTypes getType() {
		return type;
	}
	
	/**
	 * Retourne l'identifiant de la cible sur laquelle est appliqué l'obstacle
	 * @return
	 */
	public String getTarget() {
		return target;
	}
	
	/**
	 * Renvoie le manager contenant cet obstacle
	 * @return
	 */
	public ObstacleManager getManager() {
		return manager;
	}

	public void setManager(ObstacleManager manager) {
		this.manager = manager;
	}
	
	@Override
	public void puzzleLoaded(Grid grid, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		this.puzzlePreferences = puzzlePreferences;
		// Regarde dans les préférences si cet obstacle est actif
		setActive(readPreferenceObstacleActive());
	}
	
	@Override
	public void linkUsed(PuzzleLink link) {
		// TODO Auto-generated method stub
	}

	@Override
	public void nodeHidden(PuzzleNode node) {
		// TODO Auto-generated method stub
	}

	@Override
	public void wordValidated(String word, List<GridCell> cells) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void wordRejected(String word, List<GridCell> cells) {
		// TODO Auto-generated method stub
	}

	@Override
	public void letterSelected(String letter) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void letterUnselected(String letter) {
		// TODO Auto-generated method stub
	}

	@Override
	public void timeElapsed(float delta) {
		// TODO Auto-generated method stub
	}
	
	public PuzzlePreferencesHelper getPuzzlePreferences() {
		return puzzlePreferences;
	}
	
	protected boolean readPreferenceObstacleActive() {
		if (puzzlePreferences != null) {
			return puzzlePreferences.isObstacleActive(getType().toString(), getTarget());
		}
		// Par défaut, l'obstacle est actif
		return true;
	}
	
	protected void writePreferenceObstacleActive(boolean isActive) {
		if (puzzlePreferences != null) {
			puzzlePreferences.setObstacleActive(getType().toString(), getTarget(), isActive);
		}
	}
	
	protected int readPreferenceMorphCurrentLetterIndex() {
		if (puzzlePreferences != null) {
			return puzzlePreferences.getMorphCurrentLetterIndex(getType().toString(), getTarget());
		}
		// Par défaut, l'obstacle affichera la première lettre
		return 0;
	}
	
	protected void writePreferenceMorphCurrentLetterIndex(int index) {
		if (puzzlePreferences != null) {
			puzzlePreferences.setMorphCurrentLetterIndex(getType().toString(), getTarget(), index);
		}
	}
	
	/**
	 * Initialise complètement un obstacle, ce qui revient à appeler
	 * d'un seul coup les méthodes {@link #graphLoaded(PuzzleGraph)}
	 * et {@link #puzzleLoaded(PuzzleGraph, PuzzleAttributes, Stage, PuzzlePreferencesHelper)}.
	 * Utile pour les obstacles qui sont créés après l'initialisation
	 * du graphe, à la volée.
	 * @param puzzleGraph
	 * @param puzzleAttributes
	 * @param stage
	 * @param puzzlePreferences
	 */
	public void init(Grid grid,
			PuzzleAttributes puzzleAttributes,
			Stage stage,
			PuzzlePreferencesHelper puzzlePreferences) {
		puzzleLoaded(grid, puzzleAttributes, stage, puzzlePreferences);
	}

	/**
	 * Initialise les données de cet obstacle à partir des clés du fichier de propriétés
	 * @param properties
	 * @param key Clé de l'obstacle sous laquelle se trouvent ses données
	 */
	public void initFromProperties(PropertiesEx properties, String key) {
		// Lecture du type de l'obstacle
		type = ObstaclesTypes.valueOf(properties.getProperty(key + ".type"));
		
		// Par défaut, on n'a rien d'autre de spécial à lire dans le fichier
	}
}
