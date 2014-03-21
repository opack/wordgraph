package com.slamdunk.wordgraph.puzzle.obstacles;

import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleListener;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

/**
 * Repr�sente un obstacle qui peut g�ner le joueur.
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
	 * Cette m�thode doit �tre appel�e � chaque rafra�chissement
	 * du graphe.
	 * L'effet ne devrait �tre appliqu� que si l'obstacle est
	 * toujours actif.
	 * @param graph
	 * @see #isActive()
	 */
	public abstract void applyEffect(Graph graph);
	
	/**
	 * Indique si l'obstacle est toujours actif ou non.
	 * @return
	 */
	public boolean isActive() {
		return isActive;
	}
	
	/**
	 * D�finit si l'obstacle est actif ou non
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
	 * Retourne l'identifiant de la cible sur laquelle est appliqu� l'obstacle
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
	public void graphLoaded(Graph graph, PuzzlePreferencesHelper puzzlePreferences) {
		this.puzzlePreferences = puzzlePreferences;
		// Regarde dans les pr�f�rences si cet obstacle est actif
		setActive(readPreferenceObstacleActive());
	}

	public PuzzlePreferencesHelper getPuzzlePreferences() {
		return puzzlePreferences;
	}
	
	protected boolean readPreferenceObstacleActive() {
		if (puzzlePreferences != null) {
			return puzzlePreferences.isObstacleActive(getType().toString(), getTarget());
		}
		// Par d�faut, l'obstacle est actif
		return true;
	}
	
	protected void writePreferenceObstacleActive(boolean isActive) {
		if (puzzlePreferences != null) {
			puzzlePreferences.setObstacleActive(getType().toString(), getTarget(), isActive);
		}
	}
}
