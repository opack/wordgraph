package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Masque une lettre
 */
public class FogObstacle extends CellObstacle{
	
	public FogObstacle() {
		setType(ObstaclesTypes.FOG);
	}
	
	@Override
	public void applyEffect(Grid grid) {
		super.applyEffect(grid);
		TextButton button = getCell().getButton();
		// Si l'obstacle est actif, on masque la lettre
		if (isActive()) {
			// Supprime le texte du bouton
			button.setText("?");
		} else {
			// Sinon on remet le texte du bouton
			button.setText(getCell().getLetter());
		}
	}

	@Override
	public void wordValidated(String word, List<GridCell> cells) {
		super.wordValidated(word, cells);
		// Si la lettre de cet obstacle est contenue dans le mot validé,
		// alors le brouillard est dissipé
		if (cells.contains(getCell())) {
			deactivate(getManager().getGrid());
		}
	}
}
