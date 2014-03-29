package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Ne sert à rien à part noter qu'une lettre ne sert à aucun mot
 */
public class IntruderObstacle extends NodeObstacle{
	private Collection<String> linkedLetters;
	
	public IntruderObstacle(String target, Collection<String> connectedLetters) {
		super(ObstaclesTypes.INTRUDER, target);
		this.linkedLetters = connectedLetters;
	}
	
	@Override
	public boolean isObstacleDrawn() {
		// Pour cet obstacle, il ne faut modifier l'image
		// du bouton que si l'obstacle est inactif
		return !isActive();
	}
	
	/**
	 * Retourne les lettres connectées à cet intrus
	 * @return
	 */
	public Collection<String> getLinkedLetters() {
		return linkedLetters;
	}
	
	@Override
	public void graphLoaded(PuzzleGraph graph) {
		// Ajoute le noeud au graphe
		PuzzleNode node1 = graph.addNode(getTarget());
		
		// Ajoute les liens vers les autres noeuds
		for (String letter : linkedLetters) {
			PuzzleNode node2 = graph.getNode(letter);
			graph.addLink(node1, node2);
		}
		
		// ATTENTION ! Pour l'intrus, il faut faire le super A LA FIN
		// car avant le node n'existe pas !
		super.graphLoaded(graph);
	}

	@Override
	public void wordValidated(String word) {
		// Désactive l'obstacle si les lettres connectées à cette lettre intruse
		// ne sont connectées à aucune autre lettre
		
		if (!isActive()) {
			return;
		}
		
		PuzzleGraph graph = getManager().getPuzzleGraph();
		List<Obstacle> intruders = getManager().getObstacles(ObstaclesTypes.INTRUDER);
		PuzzleNode node1 = getNode();
		// Pour chaque lien de cet intrus
		for (PuzzleLink link : node1.getLinks().values()) {
			// Chaque lettre à l'autre bout est récupérée
			PuzzleNode node2 = link.getOtherNode(node1);
			
			// Et chaque lien qu'elle a est analysé : si ce lien
			// n'est pas utilisé et pointe vers une lettre qui
			// n'est pas un intrus, alors la lettre reste en jeu.
			boolean hasValidLinks = false;
			for (PuzzleLink otherLink : node2.getLinks().values()) {
				// On regarde d'abord si le lien n'est pas utilisé
				if (otherLink.isAvailable()) {
					// On détermine ensuite si l'autre bout du lien n'est pas un intrus
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
			
			// Sinon, la lettre liée à cet intrus est masquée et  
			// ses liens vers les intrus aussi.
			if (!hasValidLinks) {
				// Masque le noeud
				node2.getButton().setVisible(false);
				
				// Puis désactive tous les liens de cette lettre
				for (PuzzleLink otherLink : node2.getLinks().values()) {
					otherLink.setSize(0);
				}
			}
		}
		
		// S'il ne reste aucune lettre en jeu connectée à cet intrus,
		// alors l'intrus est révélé.
		if (!getNode().isReachable() && isActive()) {
			setActive(false);
			applyEffect(graph);
			writePreferenceObstacleActive(false);
		}
	}
	
	/**
	 * Crée un IntruderObstacle initialisé avec les données lues dans le fichier
	 * properties décrivant le puzzle. Ces données ont la forme suivante :
	 * [L]|[XXXX] avec :
	 *   [L] : lettre du graphe intruse à créer
	 *   [XXXX] : différentes lettres qui devront être reliées à celle-ci
	 * Exemple :
	 * 	- "P|TU" : création d'un intrus avec la lettre P, relié aux lettres T et U 
	 * @param propertiesDescription
	 * @return
	 */
	public static IntruderObstacle createFromProperties(String propertiesDescription) {
		String[] parameters = propertiesDescription.split("\\|");
		if (parameters.length != 2) {
			throw new IllegalArgumentException("IntruderObstacle : Failure to split '" + propertiesDescription + "' in the 2 required parts.");
		}
		String target = parameters[0];
		String letters = parameters[1];
		List<String> connectedLetters = new ArrayList<String>();
		for (int curChar = 0; curChar < letters.length(); curChar++) {
			connectedLetters.add(String.valueOf(letters.charAt(curChar)));
		}
		return new IntruderObstacle(target, connectedLetters);
	}
}
