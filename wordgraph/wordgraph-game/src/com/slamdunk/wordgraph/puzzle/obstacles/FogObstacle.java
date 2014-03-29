package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;

/**
 * Masque une lettre
 */
public class FogObstacle extends NodeObstacle{
	public FogObstacle(String target) {
		super(ObstaclesTypes.FOG, target);
	}

	@Override
	public void applyEffect(PuzzleGraph graph) {
		super.applyEffect(graph);
		TextButton button = getNode().getButton();
		// Si l'obstacle est actif, on masque la lettre
		if (isActive()) {
			// Supprime le texte du bouton
			button.setText("?");
		} else {
			// Sinon on remet le texte du bouton
			button.setText(getTarget());
		}
	}

	@Override
	public void wordValidated(String word) {
		// Si la lettre de cet obstacle est contenue dans le mot validé,
		// alors le brouillard est dissipé
		if (word.indexOf(getTarget()) != -1) {
			setActive(false);
			writePreferenceObstacleActive(false);
			applyEffect(getManager().getPuzzleGraph());
		}
	}
}
