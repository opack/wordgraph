package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.LetterStates;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleButtonDecorator;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

public abstract class CellObstacle extends Obstacle {
	private int line;
	private int column;
	private GridCell cell;
	private Image image;

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public void applyEffect(Grid grid) {
		// Lorsque l'effet doit être appliqué, on change le style
		// du bouton pour que son image reflète l'obstacle
		PuzzleButtonDecorator.getInstance().setStyle(cell, LetterStates.NORMAL);
	}
	
	@Override
	public void puzzleLoaded(Grid grid, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(grid, puzzleAttributes, stage, puzzlePreferences);
		// Récupération du noeud ciblé
		cell = grid.getCell(line, column);
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqué comme inactif dans les préférences
		setActive(cell != null && isActive());
		if (isActive()) {
			cell.addObstacle(this);
		}
		// Application de l'effet de l'obstacle
		applyEffect(grid);
	}
	
	public GridCell getCell() {
		return cell;
	}

	public Image getImage() {
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}

	public void createImage(String regionName) {
		TextButton button = cell.getButton();
		if (button == null) {
			return;
		}
		image = new Image(Assets.defaultPuzzleSkin.getDrawable(regionName));
		button.addActor(image);
		image.setZIndex(0);
		image.setPosition(
			(button.getWidth() - image.getWidth()) / 2,
			(button.getHeight() - image.getHeight()) / 2);
	}
	
	@Override
	public void initFromProperties(PropertiesEx properties, String key) {
		super.initFromProperties(properties, key);
		
		// Lecture de la position de la cellule impactée par l'obstacle
		line = properties.getIntegerProperty(key + ".line", -1);
		column = properties.getIntegerProperty(key + ".column", -1);
	}
}
