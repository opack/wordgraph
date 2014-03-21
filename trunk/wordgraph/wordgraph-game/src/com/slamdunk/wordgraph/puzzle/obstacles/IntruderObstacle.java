package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Ne sert à rien à part noter qu'une lettre ne sert à aucun mot
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
		// Désactive l'obstacle si les lettres connectées à cette lettre intruse
		// ne sont connectées à aucune autre lettre
		 
		if (!isActive()) {
			return;
		}
		// Pour chaque lien de cet intrus
		String target = getTarget();
		Graph graph = getManager().getGraph();
		List<Obstacle> intruders = getManager().getObstacles(ObstaclesTypes.INTRUDER);
		for (GraphLink link : getNode().getLinks()) {
			// Chaque lettre à l'autre bout est récupérée
			String otherEnd = link.getOtherEnd(target);			
			GraphNode otherNode = graph.getNode(otherEnd);
			
			// Et chaque lien qu'elle a est analysé : si ce lien
			// n'est pas utilisé et pointe vers une lettre qui
			// n'est pas un intrus, alors la lettre reste en jeu.
			boolean hasValidLinks = false;
			for (GraphLink otherLink : otherNode.getLinks()) {
				// On regarde d'abord si le lien n'est pas utilisé
				if (!otherLink.isUsed()) {
					// On détermine ensuite si l'autre bout du lien n'est pas un intrus
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
		if (!getNode().isReachable() && getImage() == null) {
			setActive(false);
			applyEffect(graph);
			writePreferenceObstacleActive(false);
		}
	}
}
