package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Masque un indice
 */
public class HiddenObstacle extends ClueObstacle{
	public HiddenObstacle() {
		setType(ObstaclesTypes.HIDDEN);
	}
	
	@Override
	public void wordValidated(String word, List<GridCell> cells) {
		super.wordValidated(word, cells);
		// Si le mot valid� est celui de cet obstacle, alors l'obstacle dispara�t
		if (word.equals(getRiddle().getSolution())) {
			setActive(false);
			applyEffect(null);
			saveToPreferences();
		}
	}
	
	@Override
	public LabelStyle getLabelStyle() {
		return Assets.defaultPuzzleSkin.get("obstacle-hidden", LabelStyle.class);
	}
}
