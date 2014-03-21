package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Ne sert � rien � part noter qu'une lettre ne sert � aucun mot
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
		// Rien de sp�cial � faire
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
		// R�cup�re la lettre intruse
		node = graph.getNode(letter);
		if (node == null) {
			// Aucun node n'existe avec cette lettre, on d�sactive l'obstacle
			isActive = false;
		}
	}

	/**
	 * D�sactive l'obstacle si les lettres connect�es � cette lettre intruse
	 * ne sont connect�es � aucune autre lettre
	 */
	public void hideIfNecessary() {
		if (!isActive) {
			return;
		}
		// Pour chaque lien de cet intrus
		for (GraphLink link : node.getLinks()) {
			// Chaque lettre � l'autre bout est r�cup�r�e
			String otherEnd = link.getOtherEnd(letter);			
			GraphNode otherNode = manager.getGraph().getNode(otherEnd);
			
			// Et chaque lien qu'elle a est analys� : si ce lien
			// n'est pas utilis� et pointe vers une lettre qui
			// n'est pas un intrus, alors la lettre reste en jeu.
			boolean hasValidLinks = false;
			for (GraphLink otherLink : otherNode.getLinks()) {
				String otherLinkEnd = otherLink.getOtherEnd(otherEnd);
				if (!otherLink.isUsed() && !manager.getObstacles().containsKey(otherLinkEnd)) {
					hasValidLinks = true;
					break;
				}
			}
			
			// Sinon, la lettre li�e � cet intrus est masqu�e et  
			// ses liens vers les intrus aussi.
			if (!hasValidLinks) {
				// Masque le noeud
				otherNode.setVisible(false);
				
				// Puis d�sactive tous les liens de cette lettre
				for (GraphLink otherLink : otherNode.getLinks()) {
					otherLink.setUsed(true);
					otherLink.setVisible(false);
				}
			}
		}
		
		// S'il ne reste aucune lettre en jeu connect�e � cet intrus,
		// alors l'intrus est r�v�l�.
		if (!node.isReachable() && revealed == null) {
			revealed = new Image(Assets.defaultPuzzleSkin.getDrawable("obstacle-intruder-revealed"));
			node.addActor(revealed);
			revealed.setZIndex(0);
			revealed.setPosition((node.getWidth() - revealed.getWidth()) / 2, (node.getHeight() - revealed.getHeight()) / 2);
		}
	}
}
