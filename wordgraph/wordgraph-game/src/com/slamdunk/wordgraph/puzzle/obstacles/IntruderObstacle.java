package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Ne sert à rien à part noter qu'une lettre ne sert à aucun mot
 */
public class IntruderObstacle implements Obstacle{
	private IntruderObstacleManager manager;
	private String letter;
	private boolean isActive;
	private GraphNode node;
	private Image revealed;
	
	public IntruderObstacle(String letter) {
		this.letter = letter;
		isActive = true;
	}

	public void setManager(IntruderObstacleManager manager) {
		this.manager = manager;
	}


	@Override
	public void applyEffect(Graph graph) {
		// Rien de spécial à faire
	}

	@Override
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public ObstaclesTypes getType() {
		return ObstaclesTypes.INTRUDER;
	}
	
	public String getLetter() {
		return letter;
	}

	@Override
	public void init(Graph graph) {
		// Récupère la lettre intruse
		node = graph.getNode(letter);
		if (node == null) {
			// Aucun node n'existe avec cette lettre, on désactive l'obstacle
			isActive = false;
		}
	}

	/**
	 * Désactive l'obstacle si les lettres connectées à cette lettre intruse
	 * ne sont connectées à aucune autre lettre
	 */
	public void hideIfNecessary() {
		if (!isActive) {
			return;
		}
		// Pour chaque lien de cet intrus
		for (GraphLink link : node.getLinks()) {
			// Chaque lettre à l'autre bout est récupérée
			String otherEnd = link.getOtherEnd(letter);			
			GraphNode otherNode = manager.getGraph().getNode(otherEnd);
			
			// Et chaque lien qu'elle a est analysé : si ce lien
			// n'est pas utilisé et pointe vers une lettre qui
			// n'est pas un intrus, alors la lettre reste en jeu.
			boolean hasValidLinks = false;
			for (GraphLink otherLink : otherNode.getLinks()) {
				String otherLinkEnd = otherLink.getOtherEnd(otherEnd);
				if (!otherLink.isUsed() && !manager.getObstacles().containsKey(otherLinkEnd)) {
					hasValidLinks = true;
					break;
				}
			}
			
			// Sinon, la lettre liée à cet intrus est masquée et  
			// ses liens vers les intrus aussi.
			if (!hasValidLinks) {
				// Masque le noeud
				otherNode.setVisible(false);
				
				// Puis désactive tous les liens de cette lettre
				for (GraphLink otherLink : otherNode.getLinks()) {
					otherLink.setUsed(true);
					otherLink.setVisible(false);
				}
			}
		}
		
		// S'il ne reste aucune lettre en jeu connectée à cet intrus,
		// alors l'intrus est révélé.
		if (!node.isReachable() && revealed == null) {
			revealed = new Image(Assets.defaultPuzzleSkin.getDrawable("obstacle-intruder-revealed"));
			node.addActor(revealed);
			revealed.setZIndex(0);
			revealed.setPosition((node.getWidth() - revealed.getWidth()) / 2, (node.getHeight() - revealed.getHeight()) / 2);
		}
	}
}
