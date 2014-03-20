package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphEdge;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Gère tous les obstacles Intruder
 */
public class IntruderObstacleManager extends ObstacleManager<IntruderObstacle> {
	
	@Override
	public void graphLoaded(Graph graph) {
		super.graphLoaded(graph);
		hideIntrudedLetters();
	}
	
	@Override
	public void wordValidated(String word) {
		hideIntrudedLetters();
	}

	/**
	 * Cache les lettres qui ne sont connectées qu'à des lettres intruses
	 */
	private void hideIntrudedLetters() {
		List<String> usedLinks = new ArrayList<String>();
		Map<String, IntruderObstacle> obstacles = getObstacles();
		for (GraphNode node : getGraph().getNodes()) {
			// Récupère tous les liens de cette lettre et
			// regarde si au moins une lettre n'est pas un intrus
			boolean hasValidLinks = false;
			for (GraphEdge link : node.getEdges()) {
				String otherEnd = link.getOtherEnd(node.getName());
				// Si la lettre est liée à des lettres qui ne sont pas dans le
				// manager des intrus, alors elle contient au moins un lien
				// valide.
				if (!obstacles.containsKey(otherEnd)) {
					hasValidLinks = false;
				}
			}
			// Si la lettre n'a que des liens vers des intrus, alors
			// on peut la cacher
			if (!hasValidLinks) {
				// Désactive tous les liens de cette lettre
				for (GraphEdge link : node.getEdges()) {
					link.setUsed(true);
					link.setVisible(false);
					usedLinks.add(link.getName());
				}
				
				// Puis masque le noeud
				node.setVisible(false);
			}
		}
		// Mise à jour des liens masqués dans les préférences
		// TODO puzzlePreferences.setEdgeUsed(usedLinks, true);
	}
	
	@Override
	public void add(String target, IntruderObstacle obstacle) {
		super.add(target, obstacle);
		obstacle.setManager(this);
	}
}
