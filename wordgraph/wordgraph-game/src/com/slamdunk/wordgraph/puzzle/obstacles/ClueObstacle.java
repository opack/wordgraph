package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

public abstract class ClueObstacle extends Obstacle {
	private Riddle riddle;
	private Label label;
	private Image image;

	public ClueObstacle(ObstaclesTypes type, String target) {
		super(type, target);
	}
	
	public Riddle getRiddle() {
		return riddle;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public Image getImage() {
		return image;
	}
	
	@Override
	public void puzzleLoaded(Graph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(graph, puzzleAttributes, stage, puzzlePreferences);
		// R�cup�ration de l'indice cibl�
		riddle = puzzleAttributes.getRiddle(Integer.valueOf(getTarget()));
		// R�cup�ration du libell� de l'indice
		Actor actor = stage.getRoot().findActor("riddle" + getTarget());
		if (actor != null) {
			label = (Label)actor;
		}
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqu� comme inactif dans les pr�f�rences
		setActive(riddle != null && readPreferenceObstacleActive());
		// Application de l'effet de l'obstacle
		applyEffect(graph);
	}
	
	public void createImage(String regionName) {
		image = new Image(Assets.defaultPuzzleSkin.getDrawable(regionName));
		image.setZIndex(0);
		image.setPosition((label.getWidth() - image.getWidth()) / 2, (label.getHeight() - image.getHeight()) / 2);
	}
}