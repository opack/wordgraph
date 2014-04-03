package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Remplace un indice par une catégorie
 */
public class CategoryObstacle extends ClueObstacle{
	private String category;
	
	public String getCategory() {
		return category;
	}

	@Override
	public void applyEffect(Grid grid) {
		super.applyEffect(grid);
		Label label = getLabel();
		if (label != null) {
			if (isActive()) {
				label.setText(category);
				label.setAlignment(Align.center);
			} else {
				label.setText(getRiddle().getClue());
				label.setAlignment(Align.left);
			}
		}
	}
	
	@Override
	public void wordValidated(String word, List<GridCell> cells) {
		super.wordValidated(word, cells);
		// Si le mot validé est celui de cet obstacle, alors l'obstacle disparaît
		if (word.equals(getRiddle().getSolution())) {
			setActive(false);
			applyEffect(null);
			writePreferenceObstacleActive(false);
		}
	}

	@Override
	public LabelStyle getLabelStyle() {
		return Assets.defaultPuzzleSkin.get("obstacle-category", LabelStyle.class);
	}
	
	@Override
	public void initFromProperties(PropertiesEx properties, String key) {
		super.initFromProperties(properties, key);
		
		// Lecture de la position de la cellule impactée par l'obstacle
		category = properties.getStringProperty(key + ".category", "");
	}
}
