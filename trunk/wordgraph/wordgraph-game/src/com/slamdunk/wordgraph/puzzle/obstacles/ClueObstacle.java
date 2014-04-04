package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.grid.Grid;

public abstract class ClueObstacle extends Obstacle {
	private Riddle riddle;
	private Label label;
	private Image image;
	private LabelStyle defaultStyle;
	private int riddleId;

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
	public void puzzleLoaded(Grid grid, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(grid, puzzleAttributes, stage, puzzlePreferences);
		// R�cup�ration de l'indice cibl�
		riddle = puzzleAttributes.getRiddle(riddleId);
		riddle.addObstacle(this);
		// R�cup�ration du libell� de l'indice
		Actor actor = stage.getRoot().findActor("riddle" + riddleId);
		if (actor != null) {
			label = (Label)actor;
			defaultStyle = label.getStyle();
		}
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqu� comme inactif dans les pr�f�rences
		setActive(riddle != null && isActive());
		// Application de l'effet de l'obstacle
		applyEffect(grid);
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
	public void applyEffect(Grid grid) {
		// Lorsque l'effet doit �tre appliqu�, on change le style
		// du bouton pour que son image refl�te l'obstacle
		if (isActive()) {
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
	
	@Override
	public void initFromProperties(PropertiesEx properties, String key) {
		super.initFromProperties(properties, key);
		
		// Lecture de la position de la cellule impact�e par l'obstacle
		riddleId = properties.getIntegerProperty(key + ".riddleId", -1);
	}
}
