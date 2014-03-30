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
		// Récupération de l'indice ciblé
		riddle = puzzleAttributes.getRiddle(Integer.valueOf(getTarget()));
		riddle.addObstacle(this);
		// Récupération du libellé de l'indice
		Actor actor = stage.getRoot().findActor("riddle" + getTarget());
		if (actor != null) {
			label = (Label)actor;
		}
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqué comme inactif dans les préférences
		setActive(riddle != null && isActive());
		// Application de l'effet de l'obstacle
		applyEffect(graph);
	}
	
	/**
	 * 
	 * @param regionName
	 * @param overLabel Indique si l'image doit être placée devant ou derrière
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
		// Lorsque l'effet doit être appliqué, on change le style
		// du bouton pour que son image reflète l'obstacle
		if (isActive()) {
			defaultStyle = label.getStyle();
			label.setStyle(getLabelStyle());
		} else {
			label.setStyle(defaultStyle);
		}
	}

	/**
	 * Retourne le style à appliquer au label de l'indice affecté par
	 * l'obstacle
	 */
	public abstract LabelStyle getLabelStyle();
}
