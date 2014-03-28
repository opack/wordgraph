package com.slamdunk.wordgraph.puzzle.graph;

import java.util.Collection;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Classe chargée de dessiner les liens du graph
 */
public class LinkDrawer extends Actor {
	private static final float LINK_THICKNESS = 5;
	private TextureRegion textureRegion;
	private PuzzleGraph graph;

	public void setTextureRegion(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

	public void setGraph(PuzzleGraph graph) {
		this.graph = graph;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (graph != null) {
			for (String letter : graph.getLetters()) {
				draw(graph.getLinks(letter), batch);
			}
		}
	}
	
	private void draw(Collection<PuzzleLink> links, SpriteBatch batch) {
		// TODO A PERFECTIONNER !!!
		// TODO Faire un lien épais lorsqu'il y a plusieurs liens entre les 2 mêmes lettres
		for (PuzzleLink link : links) {
			if (link.getSelected() < 1) {
				continue;
			}
			Vector2 A = getNodeCenter(link.getNode1());
			Vector2 B = getNodeCenter(link.getNode2());
			Vector2 tmp;
			
			// Cherche le point le plus proche de l'origine
			if (B.dst2(0, 0) < A.dst2(0, 0)) {
				tmp = A;
				A = B;
				B = tmp;
			}
			
			// Détermine la taille du trait
			float length = A.dst(B);
			
			// Détermine l'angle du trait
			// SOH-CAH-TOAH -> cos(a) = AC/AB, si A et B sont les 2 points et C
			// la projection de B sur l'axe des abscisses.
			// Or AC = xC - xA (les 2 sont sur les abscisses) = xB - xA (car C
			// est la projection de B sur x), et AB = width.
			float angleRad = (float)Math.acos((B.x - A.x) / length);
			float angleDeg = angleRad * MathUtils.radiansToDegrees;
			if (A.y > B.y) {
				// Si le premier point est au-dessus du second, alors l'angle est négatif
				angleDeg *= -1;
			}
			
			// Dessin du lien
			float thickness = LINK_THICKNESS * link.getSelected();
			float halfThickness = thickness / 2;
			batch.draw(
				textureRegion,
				A.x - halfThickness, A.y - halfThickness,
				0f, 0f,
				length, thickness,
				1f, 1f,
				angleDeg);
		}
	}

	/**
	 * Retourne un vecteur indiquant le centre du noeud
	 * @param node
	 * @return
	 */
	private Vector2 getNodeCenter(PuzzleNode node) {
		TextButton button = node.getButton();
		Vector2 center = new Vector2();
		center.x = button.getX() + button.getWidth() / 2;
		center.y = button.getY() + button.getHeight() / 2;
		return center;
	}
}
