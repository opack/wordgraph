package com.slamdunk.wordgraph.puzzle;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Classe chargée de dessiner les liens du graph
 */
public class LinkDrawer extends Actor {
	private static final float LINK_THICKNESS = 20;
	private static final float LINK_HALF_THICKNESS = LINK_THICKNESS / 2;
	
	private TextureRegion linkTexture;
	private List<GridCell> cells;
	
	private Vector2 a;
	private Vector2 b;
	
	public LinkDrawer() {
		a = new Vector2();
		b = new Vector2();
	}
	
	public void setLinkTexture(TextureRegion linkTexture) {
		this.linkTexture = linkTexture;
	}
	
	public void setCells(List<GridCell> cells) {
		this.cells = cells;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		GridCell previous = null;
		Vector2 tmp;
		float parentX = 0;
		for (GridCell cell : cells) {
			if (previous == null) {
				previous = cell;
				parentX = cell.getButton().getParent().getX();
				continue;
			}
			// Récupération du centre des 2 cellules
			getCellCenter(previous, a);
			getCellCenter(cell, b);
			
			// Cherche le point le plus proche de l'origine
			if (b.dst2(0, 0) < a.dst2(0, 0)) {
				tmp = a;
				a = b;
				b = tmp;
			}
			
			// Détermine la taille du trait
			float length = a.dst(b);
			
			// Détermine l'angle du trait
			// SOH-CAH-TOAH -> cos(a) = AC/AB, si A et B sont les 2 points et C
			// la projection de B sur l'axe des abscisses.
			// Or AC = xC - xA (les 2 sont sur les abscisses) = xB - xA (car C
			// est la projection de B sur x), et AB = width.
			float angleRad = (float)Math.acos((b.x - a.x) / length);
			float angleDeg = angleRad * MathUtils.radiansToDegrees;
			if (a.y > b.y) {
				// Si le premier point est au-dessus du second, alors l'angle est négatif
				angleDeg *= -1;
			}
			
			// Dessin du lien
			batch.draw(
				linkTexture,
				a.x + parentX, a.y,
				0, LINK_HALF_THICKNESS,
				length, LINK_THICKNESS,
				1f, 1f,
				angleDeg);
			
			previous = cell;
		}
	}

	/**
	 * Retourne un vecteur indiquant le centre du noeud
	 * @param node
	 * @return
	 */
	private void getCellCenter(GridCell cell, Vector2 result) {
		TextButton button = cell.getButton();
		result.x = button.getX() + button.getWidth() / 2;
		result.y = button.getY() + button.getHeight() / 2;
	}
}
