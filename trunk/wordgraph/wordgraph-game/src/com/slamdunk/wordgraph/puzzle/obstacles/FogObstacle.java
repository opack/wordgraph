package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Isole une lettre
 */
public class FogObstacle implements Obstacle{
	private String letter;
	private boolean isActive;
	private GraphNode node;
	private Image image;
	
	public FogObstacle(String letter) {
		this.letter = letter;
		isActive = true;
	}

	@Override
	public void applyEffect(Graph graph) {
		// Rien de spécial à faire
	}

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		
		// Supprime l'image de brouillard s'il n'est plus actif
		if (!isActive) {
			image.remove();
			node.setText(letter);
		}
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public ObstaclesTypes getType() {
		return ObstaclesTypes.FOG;
	}
	
	public String getLetter() {
		return letter;
	}

	@Override
	public void init(Graph graph) {
		// Récupère la lettre isolée
		node = graph.getNode(letter);
		if (node == null) {
			// Aucun node n'existe avec cette lettre, on désactive l'obstacle
			isActive = false;
		}
		
		// Supprime le texte du bouton
		node.setText("?");
		
		// Place une image de brouillard sur la lettre isolée
		image = new Image(Assets.defaultPuzzleSkin.getDrawable("obstacle-fog"));
		node.addActor(image);
		image.setZIndex(0);
		image.setPosition((node.getWidth() - image.getWidth()) / 2, (node.getHeight() - image.getHeight()) / 2);
	}
}
