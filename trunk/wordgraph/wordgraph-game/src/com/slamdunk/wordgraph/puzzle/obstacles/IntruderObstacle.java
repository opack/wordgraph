package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Ne sert � rien � part noter qu'une lettre ne sert � aucun mot
 */
public class IntruderObstacle extends NodeObstacle{
	public IntruderObstacle(String target) {
		super(ObstaclesTypes.INTRUDER, target);
	}

	@Override
	public void applyEffect(PuzzleGraph graph) {
		// Si l'effet n'est plus actif, on affiche une image
		if (!isActive() && getImage() == null) {
			createImage("obstacle-intruder-revealed");
		}
	}

	@Override
	public void wordValidated(String word) {
		// D�sactive l'obstacle si les lettres connect�es � cette lettre intruse
		// ne sont connect�es � aucune autre lettre
		 
		if (!isActive()) {
			return;
		}
		// Pour chaque lien de cet intrus
		PuzzleGraph graph = getManager().getPuzzleGraph();
		List<Obstacle> intruders = getManager().getObstacles(ObstaclesTypes.INTRUDER);
		PuzzleNode node1 = getNode();
		for (PuzzleLink link : node1.getLinks().values()) {
			// Chaque lettre � l'autre bout est r�cup�r�e
			PuzzleNode node2 = link.getOtherNode(node1);
			
			// Et chaque lien qu'elle a est analys� : si ce lien
			// n'est pas utilis� et pointe vers une lettre qui
			// n'est pas un intrus, alors la lettre reste en jeu.
			boolean hasValidLinks = false;
			for (PuzzleLink otherLink : node2.getLinks().values()) {
				// On regarde d'abord si le lien n'est pas utilis�
				if (otherLink.isAvailable()) {
					// On d�termine ensuite si l'autre bout du lien n'est pas un intrus
					PuzzleNode node3 = otherLink.getOtherNode(node2);
					String letter3 = node3.getLetter();
					for (Obstacle intruder : intruders) {
						if (!intruder.getTarget().equals(letter3)) {
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
				node2.getButton().setVisible(false);
				
				// Puis d�sactive tous les liens de cette lettre
				for (PuzzleLink otherLink : node2.getLinks().values()) {
					otherLink.setSize(0);
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
