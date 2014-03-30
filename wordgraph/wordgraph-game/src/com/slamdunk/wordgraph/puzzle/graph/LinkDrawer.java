package com.slamdunk.wordgraph.puzzle.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Classe chargée de dessiner les liens du graph
 */
public class LinkDrawer extends Actor {
	private static final float LINK_THICKNESS = 5;
	private TextureRegion linkTexture;
	private TextureRegion linkedNodeTexture;
	private PuzzleGraph graph;
	private Map<String, Integer> tmpNodeLinkCount;
	private String word;
	
	public LinkDrawer() {
		tmpNodeLinkCount = new HashMap<String, Integer>();
	}

	public void setLinkTexture(TextureRegion linkTexture) {
		this.linkTexture = linkTexture;
	}
	
	public void setLinkedNodeTexture(TextureRegion linkedNodeTexture) {
		this.linkedNodeTexture = linkedNodeTexture;
	}

	public void setGraph(PuzzleGraph graph) {
		this.graph = graph;
	}
	
	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (graph != null) {
			// Dessin des liens
			for (String letter : graph.getLetters()) {
				drawLinks(graph.getLinks(letter), batch);
			}
			
			// Dessin des noeuds liés
			if (word != null && !word.isEmpty()) {
				drawNodes(batch);
			}
		}
	}
	
	private void drawNodes(SpriteBatch batch) {
		// Compte le nombre de fois que chaque lettre apparaît dans le mot
		tmpNodeLinkCount.clear();
		final int length = word.length();
		for (int curChar = 0; curChar < length; curChar++) {
			incrementLetterCount(word.substring(curChar, curChar + 1));
		}
		
		// Dessin des noeuds
		for (Map.Entry<String, Integer> entries : tmpNodeLinkCount.entrySet()) {
			// Récupération du noeud associé à la lettre
			PuzzleNode node = graph.getNode(entries.getKey());
			Button button = node.getButton();
			Integer linkCount = entries.getValue();

			float halfThickness = LINK_THICKNESS * linkCount;
			float thickness = halfThickness * 2;
			float width = button.getWidth() + thickness;
			float height = button.getHeight() + thickness;
			batch.draw(
				linkedNodeTexture,
				button.getX() - halfThickness, button.getY() - halfThickness,
				0f, 0f,
				width, height,
				1f, 1f,
				0f);
		}
	}

	private void drawLinks(Collection<PuzzleLink> links, SpriteBatch batch) {
		for (PuzzleLink link : links) {
			if (link.getSelected() < 1 || !link.isVisible()) {
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
				linkTexture,
				A.x - halfThickness, A.y - halfThickness,
				0f, 0f,
				length, thickness,
				1f, 1f,
				angleDeg);
		}
	}

	private void incrementLetterCount(String letter) {
		Integer linkCount = tmpNodeLinkCount.get(letter);
		if (linkCount == null) {
			linkCount = 0;
		}
		tmpNodeLinkCount.put(letter, linkCount + 1);
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
