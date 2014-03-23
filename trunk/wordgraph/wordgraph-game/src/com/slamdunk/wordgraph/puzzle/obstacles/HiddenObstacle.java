package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

/**
 * Masque un indice
 */
public class HiddenObstacle extends ClueObstacle{
	private LabelStyle originalStyle;
	
	public HiddenObstacle(String target) {
		super(ObstaclesTypes.HIDDEN, target);
	}

	@Override
	public void applyEffect(Graph graph) {
		Label label = getLabel();
		if (isActive()) {
			if (originalStyle == null) {
				originalStyle = label.getStyle();
			}
			LabelStyle newStyle = new LabelStyle(originalStyle);
			newStyle.background = Assets.defaultPuzzleSkin.getDrawable("obstacle-hidden");
			
			label.setStyle(newStyle);
			label.setText("???");
			label.setAlignment(Align.center);
		} else {
			label.setStyle(originalStyle);
			label.setText(getRiddle().getClue());
			label.setAlignment(Align.left);
		}
	}
	
	@Override
	public void wordValidated(String word) {
		// Si le mot valid� est celui de cet obstacle, alors l'obstacle dispara�t
		if (word.equals(getRiddle().getSolution())) {
			setActive(false);
			applyEffect(null);
			writePreferenceObstacleActive(false);
		}
	}
}
