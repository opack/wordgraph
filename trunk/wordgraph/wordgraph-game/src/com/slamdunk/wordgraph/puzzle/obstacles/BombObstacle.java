package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Place une bombe sur la lettre qui va exploser et casser tous les liens
 * au bout d'un certain nombre de touches
 */
public class BombObstacle extends NodeObstacle{
	private int countDown;
	private Label label;
	
	public BombObstacle(String target, int countDown) {
		super(ObstaclesTypes.BOMB, target);
		this.countDown = countDown;
	}
	
	@Override
	public void puzzleLoaded(Graph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(graph, puzzleAttributes, stage, puzzlePreferences);
		// Initialise le compte
		int prefsCountDown = puzzlePreferences.getBombCountDown(getTarget());
		if (prefsCountDown != -1) {
			countDown = prefsCountDown;
		}
		applyEffect(graph);
	}

	@Override
	public void applyEffect(Graph graph) {
		// Si l'obstacle est actif, on masque la lettre
		if (isActive()) {
			// Place une image de bombe et un libell� avec le d�compte
			if (getImage() == null) {
				createImage("obstacle-bomb");
				label = new Label(String.valueOf(countDown), Assets.defaultPuzzleSkin.get("text", LabelStyle.class));
				GraphNode node = getNode();
				node.addActor(label);
				label.setZIndex(0);
				label.setPosition((node.getWidth() - label.getWidth()) / 2, 0);
			}
			// Met � jour le libell� de d�compte et, si n�cessaire, fait exploser la bombe
			updateAndDetonate();
		}
		// Si, apr�s le updateAndDetonate, la bombe n'est plus active, alors
		// on retire la bombe
		if (!isActive()) {
			// Sinon on supprime l'image et le libell�
			if (getImage() != null) {
				getImage().remove();
				setImage(null);
				label.remove();
				label = null;
			}
		}
	}
	
	/**
	 * Met � jour le libell� de d�compte et, si n�cessaire, fait exploser la bombe
	 */
	private void updateAndDetonate() {
		label.setText(String.valueOf(countDown));
		if (countDown == 0) {
			ObstacleManager manager = getManager();
			// Suppression de l'obstacle Bomb
			setActive(false);
			// Cr�ation d'un obstacle Isle
			IsleObstacle isle = new IsleObstacle(getTarget());
			isle.puzzleLoaded(manager.getGraph(), manager.getPuzzleAttributes(), manager.getStage(), getPuzzlePreferences());
			manager.add(isle);
			// Note 
		}
	}

	@Override
	public void letterSelected(String letter) {
		super.letterSelected(letter);
		// A chaque lettre s�lectionn�e, le compte-�-rebours baisse
		countDown--;
		applyEffect(getManager().getGraph());
		// Sauvegarde le compte
		getPuzzlePreferences().setBombCountDown(getTarget(), countDown);
	}
	
	/**
	 * Cr�e un BombObstacle initialis� avec les donn�es lues dans le fichier
	 * properties d�crivant le puzzle. Ces donn�es ont la forme suivante :
	 * [L]|[t] avec :
	 *   [L] : lettre du graphe sur laquelle est plac� l'obstacle
	 *   [t] : nombre de touches avant que la bombe n'explose
	 * Exemple :
	 * 	- "C|5" : obstacle sur la lettre C, explose dans 5 touches
	 * @param propertiesDescription
	 * @return
	 */
	public static BombObstacle createFromProperties(String propertiesDescription) {
		String[] parameters = propertiesDescription.split("\\|");
		if (parameters.length != 2) {
			throw new IllegalArgumentException("BombObstacle : Failure to split '" + propertiesDescription + "' in the 2 required parts.");
		}
		String target = parameters[0];
		int countDown = Integer.valueOf(parameters[1]);
		return new BombObstacle(target, countDown);
	}
}
