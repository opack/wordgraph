package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Ne sert � rien � part noter qu'une lettre ne sert � aucun mot
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
	 * Retourne les lettres connect�es � cet intrus
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
		// D�sactive l'obstacle si les lettres connect�es � cette lettre intruse
		// ne sont connect�es � aucune autre lettre
		
		if (!isActive()) {
			return;
		}
		
		PuzzleGraph graph = getManager().getPuzzleGraph();
		List<Obstacle> intruders = getManager().getObstacles(ObstaclesTypes.INTRUDER);
		PuzzleNode node1 = getNode();
		// Pour chaque lien de cet intrus
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
		if (!getNode().isReachable() && isActive()) {
			setActive(false);
			applyEffect(graph);
			writePreferenceObstacleActive(false);
		}
	}
	
	/**
	 * Cr�e un IntruderObstacle initialis� avec les donn�es lues dans le fichier
	 * properties d�crivant le puzzle. Ces donn�es ont la forme suivante :
	 * [L]|[XXXX] avec :
	 *   [L] : lettre du graphe intruse � cr�er
	 *   [XXXX] : diff�rentes lettres qui devront �tre reli�es � celle-ci
	 * Exemple :
	 * 	- "P|TU" : cr�ation d'un intrus avec la lettre P, reli� aux lettres T et U 
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
