package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.slamdunk.wordgraph.Assets;

/**
 * Masque un indice
 */
public class HiddenObstacle extends ClueObstacle{
	public HiddenObstacle(String target) {
		super(ObstaclesTypes.HIDDEN, target);
	}
	
	@Override
	public void wordValidated(String word) {
		// Si le mot validé est celui de cet obstacle, alors l'obstacle disparaît
		if (word.equals(getRiddle().getSolution())) {
			setActive(false);
			applyEffect(null);
			writePreferenceObstacleActive(false);
		}
	}
	
	@Override
	public LabelStyle getLabelStyle() {
		return Assets.defaultPuzzleSkin.get("obstacle-hidden", LabelStyle.class);
	}
}
