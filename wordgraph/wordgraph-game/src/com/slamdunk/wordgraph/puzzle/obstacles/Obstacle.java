package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleListener;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Représente un obstacle qui peut gêner le joueur.
 */
public abstract class Obstacle implements PuzzleListener {
	private ObstacleManager manager;
	private boolean isActive;
	private ObstaclesTypes type;
	private String target;
	private PuzzlePreferencesHelper puzzlePreferences;
	
	public Obstacle(ObstaclesTypes type, String target) {
		this.type = type;
		this.target = target;
		this.isActive = true;
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
	public abstract void applyEffect(PuzzleGraph graph);
	
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
	public void puzzleLoaded(PuzzleGraph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		this.puzzlePreferences = puzzlePreferences;
		// Regarde dans les préférences si cet obstacle est actif
		setActive(readPreferenceObstacleActive());
	}
	
	@Override
	public void graphLoaded(PuzzleGraph graph) {
		// TODO Auto-generated method stub
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
	public void wordValidated(String word) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void wordRejected(String word) {
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
	public void init(PuzzleGraph graph,
			PuzzleAttributes puzzleAttributes,
			Stage stage,
			PuzzlePreferencesHelper puzzlePreferences) {
		graphLoaded(graph);
		puzzleLoaded(graph, puzzleAttributes, stage, puzzlePreferences);
	}
}
