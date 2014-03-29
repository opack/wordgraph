package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.utils.graphics.point.Point;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;

/**
 * Grave une lettre dans la pierre, ce qui la rend impossible
 * à sélectionner. L'effet se dissipe quand un mot est trouvé
 * avec une lettre adjacente.
 */
public class StoneObstacle extends NodeObstacle{
	private static final int[][] NEIGHBORS = {
		{-1, -1},
		{-1, 0},
		{-1, +1},
		{0, -1},
		{0, +1},
		{+1, -1},
		{+1, 0},
		{+1, +1},
	};
	
	public StoneObstacle(String target) {
		super(ObstaclesTypes.STONE, target);
	}

	@Override
	public void applyEffect(PuzzleGraph graph) {
		TextButton button = getNode().getButton();
		// Si l'obstacle est actif, on masque la lettre
		if (isActive()) {
			// Place une image de pierre sur la lettre isolée
			if (getImage() == null) {
				createImage("obstacle-stone");
			}
			// Désactive la lettre
			button.setDisabled(true);
		} else {
			// Sinon on supprime l'image de brouillard
			if (getImage() != null) {
				getImage().remove();
				button.setText(getTarget());
				setImage(null);
				button.setDisabled(false);
			}
		}
	}

	@Override
	public void wordValidated(String word) {
		// Si une lettre du mot validé est adjacent à la lettre
		// de cet obstacle, alors la pierre se brise
		// Récupère la position de l'obstacle
		PuzzleGraph graph = getManager().getPuzzleGraph();
		Point obstaclePos = graph.getPosition(getTarget());
		
		// Pour chaque position voisine, on regarde si la lettre
		// fait partie du mot validé
		for (int[] neighbor : NEIGHBORS) {
			String letter = graph.getLetter(obstaclePos.getX() + neighbor[0], obstaclePos.getY() + neighbor[1]);
			if (letter != null && word.indexOf(letter) != -1) {
				setActive(false);
				writePreferenceObstacleActive(false);
				applyEffect(getManager().getPuzzleGraph());
				break;
			}
		}
	}
}
