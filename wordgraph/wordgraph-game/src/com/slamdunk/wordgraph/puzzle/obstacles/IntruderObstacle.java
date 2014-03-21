package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Ne sert � rien � part noter qu'une lettre ne sert � aucun mot
 */
public class IntruderObstacle extends NodeObstacle{
	public IntruderObstacle(String target) {
		super(ObstaclesTypes.INTRUDER, target);
	}

	@Override
	public void applyEffect(Graph graph) {
		// Si l'effet n'est plus actif, on affiche une image
		if (!isActive() && getImage() == null) {
			createImage("obstacle-intruder-revealed");
		}
	}

	@Override
	public void linkUsed(GraphLink link) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nodeHidden(GraphNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wordValidated(String word) {
		// D�sactive l'obstacle si les lettres connect�es � cette lettre intruse
		// ne sont connect�es � aucune autre lettre
		 
		if (!isActive()) {
			return;
		}
		// Pour chaque lien de cet intrus
		String target = getTarget();
		Graph graph = getManager().getGraph();
		List<Obstacle> intruders = getManager().getObstacles(ObstaclesTypes.INTRUDER);
		for (GraphLink link : getNode().getLinks()) {
			// Chaque lettre � l'autre bout est r�cup�r�e
			String otherEnd = link.getOtherEnd(target);			
			GraphNode otherNode = graph.getNode(otherEnd);
			
			// Et chaque lien qu'elle a est analys� : si ce lien
			// n'est pas utilis� et pointe vers une lettre qui
			// n'est pas un intrus, alors la lettre reste en jeu.
			boolean hasValidLinks = false;
			for (GraphLink otherLink : otherNode.getLinks()) {
				// On regarde d'abord si le lien n'est pas utilis�
				if (!otherLink.isUsed()) {
					// On d�termine ensuite si l'autre bout du lien n'est pas un intrus
					String otherLinkEnd = otherLink.getOtherEnd(otherEnd);
					for (Obstacle intruder : intruders) {
						if (!intruder.getTarget().equals(otherLinkEnd)) {
							// Si l'autre bout n'est pas un intrus, alors c'est que le
							// noeud a au moins un lien valide
							hasValidLinks = true;
							break;
						}
					}
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
		if (!getNode().isReachable() && getImage() == null) {
			setActive(false);
			applyEffect(graph);
			writePreferenceObstacleActive(false);
		}
	}
}
