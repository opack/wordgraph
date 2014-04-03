package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Grave une lettre dans la pierre, ce qui la rend impossible
 * � s�lectionner. L'effet se dissipe quand un mot est trouv�
 * avec une lettre adjacente.
 */
public class StoneObstacle extends CellObstacle{
	@Override
	public void applyEffect(Grid grid) {
		super.applyEffect(grid);
		
		TextButton button = getCell().getButton();
		// Si l'obstacle est actif, on masque la lettre
		if (isActive()) {
			// D�sactive la lettre
			button.setDisabled(true);
		} else {
			// Sinon on remet le bon texte
			button.setText(getTarget());
			button.setDisabled(false);
		}
	}

	@Override
	public void wordValidated(String word, List<GridCell> cells) {
		super.wordValidated(word, cells);
		// Si une lettre du mot valid� est adjacent � la lettre
		// de cet obstacle, alors la pierre se brise
		// R�cup�re la position de l'obstacle
		GridCell stonedCell = getCell();
		for (GridCell cell : cells) {
			if (cell.isAround(stonedCell)) {
				setActive(false);
				writePreferenceObstacleActive(false);
				applyEffect(getManager().getGrid());
				break;
			}
		}
	}
}
