package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;

public abstract class ClueObstacle extends Obstacle {
	private Riddle riddle;
	private Label label;
	private Image image;
	private LabelStyle defaultStyle;

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
	public void puzzleLoaded(PuzzleGraph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(graph, puzzleAttributes, stage, puzzlePreferences);
		// R�cup�ration de l'indice cibl�
		riddle = puzzleAttributes.getRiddle(Integer.valueOf(getTarget()));
		riddle.addObstacle(this);
		// R�cup�ration du libell� de l'indice
		Actor actor = stage.getRoot().findActor("riddle" + getTarget());
		if (actor != null) {
			label = (Label)actor;
		}
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqu� comme inactif dans les pr�f�rences
		setActive(riddle != null && isActive());
		// Application de l'effet de l'obstacle
		applyEffect(graph);
	}
	
	/**
	 * 
	 * @param regionName
	 * @param overLabel Indique si l'image doit �tre plac�e devant ou derri�re
	 * le label
	 */
	public Image createImage(String regionName, boolean overLabel) {
		image = new Image(Assets.defaultPuzzleSkin.getDrawable(regionName));
		image.setPosition(label.getX(), label.getY() + (label.getHeight() - image.getHeight()) / 2);
		getManager().getStage().addActor(image);
		if (overLabel) {
			image.setZIndex(label.getZIndex() + 1);
		} else {
			image.setZIndex(label.getZIndex() - 1);
		}
		return image;
	}
	
	@Override
	public void applyEffect(PuzzleGraph graph) {
		// Lorsque l'effet doit �tre appliqu�, on change le style
		// du bouton pour que son image refl�te l'obstacle
		if (isActive()) {
			defaultStyle = label.getStyle();
			label.setStyle(getLabelStyle());
		} else {
			label.setStyle(defaultStyle);
		}
	}

	/**
	 * Retourne le style � appliquer au label de l'indice affect� par
	 * l'obstacle
	 */
	public abstract LabelStyle getLabelStyle();
}
