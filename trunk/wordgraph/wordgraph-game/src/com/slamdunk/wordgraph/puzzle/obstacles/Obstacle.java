package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleListener;
import com.slamdunk.wordgraph.puzzle.graph.DELETE.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.DELETE.PuzzleNode;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Repr�sente un obstacle qui peut g�ner le joueur.
 */
public abstract class Obstacle implements PuzzleListener {
	private int id;
	private ObstacleManager manager;
	private boolean active;
	private ObstaclesTypes type;
	private PuzzlePreferencesHelper puzzlePreferences;
	private String preferencesKey;
	
	public Obstacle() {
		this.active = true;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		preferencesKey = "obstacle." + id;
	}

	public void setType(ObstaclesTypes type) {
		this.type = type;
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
	public abstract void applyEffect(Grid grid);
	
	/**
	 * Indique si l'obstacle est toujours actif ou non.
	 * @return
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Indique si l'image de l'obstacle doit �tre dessin�e ou
	 * non. Le comportement par d�faut r�pond true si isActive
	 * @return
	 */
	public boolean isObstacleDrawn() {
		return active;
	}
	
	/**
	 * D�finit si l'obstacle est actif ou non
	 * @param isActive
	 */
	public void setActive(boolean isActive) {
		this.active = isActive;
	}
	
	/**
	 * Active l'obstacle, en faisant tous les effets graphiques n�cessaires
	 */
	public void activate(Grid grid) {
		setActive(true);
		applyEffect(grid);
	}
	
	/**
	 * D�sctive l'obstacle, en faisant tous les effets graphiques n�cessaires
	 */
	public void deactivate(Grid grid) {
		setActive(false);
		applyEffect(grid);
		saveToPreferences();
	}
	
	/**
	 * Retourne le type de l'obstacle
	 * @return
	 */
	public ObstaclesTypes getType() {
		return type;
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
		loadFromPreferences();
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
	
	/**
	 * Initialise les donn�es de cet obstacle � partir des cl�s du fichier de propri�t�s
	 * @param properties
	 * @param key Cl� de l'obstacle sous laquelle se trouvent ses donn�es
	 */
	public void initFromProperties(PropertiesEx properties, String key) {
		// Lecture du type de l'obstacle
		type = ObstaclesTypes.valueOf(properties.getProperty(key + ".type"));
		
		// Par d�faut, on n'a rien d'autre de sp�cial � lire dans le fichier
	}
	
	protected String getPreferencesKey() {
		return preferencesKey;
	}

	/**
	 * Lit les informations de l'instance de l'obstacle depuis les pr�f�rences
	 */
	public void loadFromPreferences() {
		active = puzzlePreferences.getBoolean(preferencesKey + ".active", true);
	}
	
	/**
	 * Enregistre les informationsde l'instance de l'obstacle dans les pr�f�rences
	 */
	public void saveToPreferences() {
		puzzlePreferences.putBoolean(preferencesKey + ".active", active);
		puzzlePreferences.flush();
	}
}
