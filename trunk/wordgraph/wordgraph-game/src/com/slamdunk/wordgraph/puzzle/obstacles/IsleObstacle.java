package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.GraphListener;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Isole une lettre
 */
public class IsleObstacle extends NodeObstacle implements GraphListener{
	private List<PuzzleLink> fakeLinks;
	
	public IsleObstacle(String target) {
		super(ObstaclesTypes.ISLE, target);
		fakeLinks = new ArrayList<PuzzleLink>();
	}
	
	@Override
	public void graphLoaded(PuzzleGraph graph) {
		super.graphLoaded(graph);
		
		String target = getTarget();
		PuzzleNode node1 = getNode();
		
		// Ajoute les liens vers tous les autres noeuds
		for (String letter : graph.getLetters()) {
			if (graph.getLink(target, letter) == null) {
				PuzzleNode node2 = graph.getNode(letter);
				PuzzleLink link = graph.addLink(node1, node2);
				fakeLinks.add(link);
			}
		}
		
		// On souhaite être averti des futurs ajouts de noeud
		graph.addListener(this);
	}

	@Override
	public void applyEffect(PuzzleGraph graph) {
		super.applyEffect(graph);
		if (!isActive()) {
			return;
		}
	}

	@Override
	public void nodeAdded(PuzzleNode node) {
		// Ajout d'un faux lien vers ce nouveau noeud
		PuzzleLink link = node.getGraph().addLink(getNode(), node);
		fakeLinks.add(link);
	}

	@Override
	public void linkAdded(PuzzleLink link) {
		// TODO Auto-generated method stub
	}

	@Override
	public void nodeLetterUpdated(String oldLetter, PuzzleNode updatedNode) {
		// TODO Auto-generated method stub
	}
}
