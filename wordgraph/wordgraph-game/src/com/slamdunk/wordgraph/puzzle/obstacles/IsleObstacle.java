package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Isole une lettre
 */
public class IsleObstacle extends NodeObstacle{
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
	}

	@Override
	public void applyEffect(PuzzleGraph graph) {
		super.applyEffect(graph);
		if (!isActive()) {
			return;
		}
	}
}
