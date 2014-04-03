package com.slamdunk.wordgraph.puzzle.obstacles;


/**
 * Ne sert � rien � part noter qu'une lettre ne sert � aucun mot
 */
public class IntruderObstacle extends CellObstacle{
	@Override
	public boolean isObstacleDrawn() {
		// Pour cet obstacle, il ne faut modifier l'image
		// du bouton que si l'obstacle est inactif
		return !isActive();
	}
}
