package com.slamdunk.wordgraph.puzzle.obstacles;

import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Masque une lettre
 */
public class FogObstacle extends NodeObstacle{
	public FogObstacle(String target) {
		super(ObstaclesTypes.FOG, target);
	}

	@Override
	public void applyEffect(Graph graph) {
		// Si l'obstacle est actif, on masque la lettre
		if (isActive()) {
			// Supprime le texte du bouton
			GraphNode node = getNode();
			node.setText("?");
			
			// Place une image de brouillard sur la lettre isolée
			if (getImage() == null) {
				createImage("obstacle-fog");
			}
		} else {
			// Sinon on supprime l'image de brouillard
			if (getImage() != null) {
				getImage().remove();
				getNode().setText(getTarget());
				setImage(null);
			}
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
		// Si la lettre de cet obstacle est contenue dans le mot validé,
		// alors le brouillard est dissipé
		if (word.indexOf(getTarget()) != -1) {
			setActive(false);
			writePreferenceObstacleActive(false);
			applyEffect(getManager().getGraph());
		}
	}
}
