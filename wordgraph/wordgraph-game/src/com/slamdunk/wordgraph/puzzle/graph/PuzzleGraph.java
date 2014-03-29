package com.slamdunk.wordgraph.puzzle.graph;

import static com.slamdunk.wordgraph.puzzle.graph.LayoutFactory.GRID_HEIGHT;
import static com.slamdunk.wordgraph.puzzle.graph.LayoutFactory.GRID_WIDTH;
import static com.slamdunk.wordgraph.puzzle.graph.LayoutFactory.SLOT_ANY;
import static com.slamdunk.wordgraph.puzzle.graph.LayoutFactory.SLOT_EMPTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.utils.graphics.point.Point;

/**
 * Repr�sentation logique du graphe du puzzle.
 * Le graph manipule �galement les boutons qui contiennent les diff�rentes lettres.
 * 		1. Charger le graphe avec {@link #load(List)}
 * 		2. Arranger le graphe avec {@link #layout(TextButton[][])} ou {@link #layout(String[], TextButton[][])}
 */
public class PuzzleGraph {
	
	/**
	 * Contient la grille des noeuds
	 */
	private PuzzleNode[][] nodesByPosition;
	
	/**
	 * Associe les noeuds aux lettres
	 */
	private Map<String, PuzzleNode> nodesByLetter;
	
	/**
	 * Listeners souhaitant �tre notifi�s des interventions sur ce graph
	 */
	private List<GraphListener> listeners;
	
	public PuzzleGraph() {
		nodesByPosition = new PuzzleNode[GRID_WIDTH][GRID_HEIGHT];
		nodesByLetter = new HashMap<String, PuzzleNode>();
	}
	
	public void addListener(GraphListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<GraphListener>();
		}
		listeners.add(listener);
	}
	
	/**
	 * Cr�e le graph � partir des solutions fournies
	 */
	public void load(List<String> solutions) {
		// Vide la table des noeuds
		nodesByLetter.clear();
		// Lit les solutions pour remplir la table des liens
		for (String solution : solutions) {
			parseSolution(solution);
		}
	}
	
	/**
	 * Extrait de la solution indiqu�e les lettres et les liens utilis�s
	 * @param solution
	 */
	private void parseSolution(String solution) {
		// S'il n'y a pas au moins 2 lettres, alors il ne peut pas y avoir
		// de lien
		final int length = solution.length();
		if (length < 2) {
			return;
		}
		// Parcourt les lettres 2 � 2
		for (int cur = 1; cur < length; cur++) {
			// R�cup�ration des 2 lettres
			String letter1 = solution.substring(cur - 1, cur);
			String letter2 = solution.substring(cur, cur + 1);
			
			// R�cup�ration des noeuds correspondants, ou cr�ation le cas �ch�ant
			PuzzleNode node1 = nodesByLetter.get(letter1);
			if (node1 == null) {
				node1 = addNode(letter1);
			}
			PuzzleNode node2 = nodesByLetter.get(letter2);
			if (node2 == null) {
				node2 = addNode(letter2);
			}
			
			// Ajoute le lien entre ces deux noeuds
			addLink(node1, node2);
		}
	}
	
	/**
	 * Ajoute un noeud au graphe avec la lettre indiqu�e.
	 * Si un noeud existe d�j� pour cette lettre, il sera
	 * �cras�.
	 * @param letter
	 * @return
	 */
	public PuzzleNode addNode(String letter) {
		// Cr�e et ajoute le noeud
		PuzzleNode node = new PuzzleNode(this, letter);
		nodesByLetter.put(letter, node);
		
		// Notifie les listeners de cet ajout
		if (listeners != null) {
			for (GraphListener listener : listeners) {
				listener.nodeAdded(node);
			}
		}
		return node;
	}
	
	/**
	 * Change la lettre associ�e � un noeud
	 * @param letter
	 */
	public void updateNodeLetter(String oldLetter, String newLetter) {
		if (nodesByLetter.isEmpty()) {
			return;
		}
		PuzzleNode node = nodesByLetter.remove(oldLetter);
		if (node != null) {
			nodesByLetter.put(newLetter, node);
		}
		// Notifie les listeners de cette modification
		if (listeners != null) {
			for (GraphListener listener : listeners) {
				listener.nodeLetterUpdated(oldLetter, node);
			}
		}
	}

	/**
	 * Ajoute un lien entre les 2 nodes indiqu�s
	 * @param node1
	 * @param node2
	 */
	public PuzzleLink addLink(PuzzleNode node1, PuzzleNode node2) {
		String letter1 = node1.getLetter();
		String letter2 = node2.getLetter();
		
		// R�cup�re l'�ventuel lien d�j� stock�
		PuzzleLink link = node1.getLink(letter2);
		
		// Si ce lien existe, on indique qu'il y en a une seconde occurence
		if (link != null) {
			link.setSize(link.getSize() + 1);
		}
		// Sinon, on le cr�e
		else {
			// Cr�ation du lien
			link = new PuzzleLink(node1, node2);
			
			// Stocke le lien pour les 2 sens de navigation possibles,
			// de fa�on � ce qu'on puisse le r�cup�rer en demandant
			// lettre1 -> lettre2 ou lettre1 <- lettre2.
			node1.setLink(letter2, link);
			node2.setLink(letter1, link);
		}
		
		// Notifie les listeners de cet ajout
		if (listeners != null) {
			for (GraphListener listener : listeners) {
				listener.linkAdded(link);
			}
		}
		return link;
	}

	/**
	 * Arrange les lettres du graphe dans une disposition par d�faut
	 * en fonction du nombre de lettres du graphe.
	 */
	public void layout(TextButton[][] buttons) {
		// R�cup�ration des lettres
		Set<String> letters = nodesByLetter.keySet();
		// R�cup�ration d'un layout pour les lettres disponibles
		LayoutFactory layouts = new LayoutFactory();
		layout(layouts.getLayout(letters.size()), buttons);
	}

	/**
	 * Choisit une position pour chaque lettre, en pr�f�rant la disposition
	 * fournie (le cas �ch�ant) au format :
	 * 	A?ERT
	 *  YUIOP
	 *  QSDFG
	 *  HJKLM
	 *  WXCV_
	 *  o� :
	 *  	- chaque lettre mentionn�e sera plac�e � sa position indiqu�e
	 *  	- le caract�re "_" marque un emplacement vide
	 *  	- le caract�re "?" marque un emplacement qui peut �tre occup�
	 *  	par n'importe quelle lettre.
	 *  Certains formats par d�faut sont utilis�s en fonction du nombre de
	 *  lettres diff�rentes. Ils sont fournis par la classe Layouts.
	 */
	public void layout(String[] layout, TextButton[][] buttons) {
		// R�z grille
		for (PuzzleNode[] line : nodesByPosition) {
			Arrays.fill(line, null);
		}
		
		// Par d�faut, tous les boutons sont masqu�s. Il seront
		// visibles uniquement s'ils contiennent une lettre
		for (TextButton[] buttonLine : buttons) {
			for (TextButton button : buttonLine) {
				button.setVisible(false);
			}
		}
		
		// R�cup�ration des lettres
		Set<String> lettersToArrange = new HashSet<String>(nodesByLetter.keySet());
		
		// Place les lettres fix�es du layout
		for (int curLine = 0; curLine < layout.length; curLine ++) {
			String line = layout[curLine];
			int length = line.length();
			for (int curColumn = 0; curColumn < length; curColumn++) {
				// R�cup�ration de la lettre du layout
				String letter = line.substring(curColumn, curColumn + 1);
				// Si la lettre mentionn�e dans le layout reste � placer,
				// on la place et on indique qu'elle n'est plus � placer
				if (lettersToArrange.contains(letter)) {
					setLetter(curLine, curColumn, letter, buttons[curLine][curColumn]);
					lettersToArrange.remove(letter);
				}
			}
		}
		
		// Remplit les emplacements restants du layout
		if (!lettersToArrange.isEmpty()) {
			// On cr�e une liste avec les lettres restantes et on la m�lange
			// de fa�on � y piocher les lettres de fa�on al�atoire.
			List<String> letters = new ArrayList<String>(lettersToArrange);
			Collections.shuffle(letters);
			for (int curLine = 0; curLine < layout.length; curLine ++) {
				String line = layout[curLine];
				int length = line.length();
				for (int curColumn = 0; curColumn < length; curColumn++) {
					// R�cup�ration du symbole du layout
					String symbol = line.substring(curColumn, curColumn + 1);
					// S'il s'agit d'un emplacement � contenu al�atoire,
					// on prend une lettre restante au hasard et on la place
					if (SLOT_ANY.equals(symbol)) {
						// Pour �conomiser un peu de CPU, on retire les �l�ments de
						// la liste par la fin.
						String letter = letters.remove(letters.size() - 1);
						setLetter(curLine, curColumn, letter, buttons[curLine][curColumn]);
						// S'il ne reste plus de lettres � placer, on a finit le layout
						if (letters.isEmpty()) {
							return;
						}
					}
				}
			}
			
			// S'il reste des lettres, alors le layout n'est pas bon !
			if (!letters.isEmpty()) {
				throw new IllegalStateException("Some letters were not placed in the puzzle " + letters + ". The puzzle will be invalid. Check the layout.");
			}
		}
	}

	/**
	 * D�finit la lettre � l'emplacement indiqu�, et met � jour le name du bouton
	 * correspondant et �ventuellement son libell�.
	 * @param line
	 * @param column
	 * @param letter
	 * @param buttonText Si diff�rent de null, repr�sente le libell� du bouton
	 */
	private void setLetter(int line, int column, String letter, TextButton button) {
		// R�cup�re le noeud correspondant � cette lettre
		PuzzleNode node = nodesByLetter.get(letter);
		
		// Affecte le bouton au noeud
		button.setText(letter);
		button.setName(letter);
		button.setVisible(true);
		node.setButton(button);
		
		// Place le noeud � l'emplacement indiqu�
		nodesByPosition[line][column] = node;
	}
	
	/**
	 * Retourne la lettre � la ligne / colonne indiqu�e
	 * @param line
	 * @param column
	 * @return
	 */
	public String getLetter(int line, int column) {
		PuzzleNode node = getNode(line, column);
		if (node == null) {
			return null;
		}
		return node.getLetter();
	}
	
	/**
	 * Retourne le noeud associ� � cette lettre
	 * @param letter
	 * @return
	 */
	public PuzzleNode getNode(String letter) {
		return nodesByLetter.get(letter);
	}
	
	/**
	 * Retourne le noeud � la position indiqu�e
	 * @param line
	 * @param column
	 * @return
	 */
	public PuzzleNode getNode(int line, int column) {
		if (line < 0 || line >= GRID_HEIGHT
		|| column < 0 || column >= GRID_WIDTH) {
			return null;
		}
		return nodesByPosition[line][column];
	}

	/**
	 * Retourne le bouton associ� � la lettre indiqu�e
	 * @param line
	 * @param column
	 * @return
	 */
	public TextButton getButton(String letter) {
		PuzzleNode node = getNode(letter);
		if (node == null) {
			return null;
		}
		return node.getButton();
	}
	
	/**
	 * Retourne l'ensemble des diff�rentes lettres du graphe
	 * @return
	 */
	public Set<String> getLetters() {
		return nodesByLetter.keySet();
	}
	
	/**
	 * Retourne l'ensemble des liens connect�s � cette lettre
	 * @param letter
	 * @return
	 */
	public Collection<PuzzleLink> getLinks(String letter) {
		PuzzleNode node = nodesByLetter.get(letter);
		if (node == null) {
			return null;
		}
		return node.getLinks().values();
	}
	
	/**
	 * Retourne le lien entre les 2 lettres indiqu�es, ou null
	 * @param letter1
	 * @param letter2
	 * @return
	 */
	public PuzzleLink getLink(String letter1, String letter2) {
		PuzzleNode node1 = nodesByLetter.get(letter1);
		if (node1 == null) {
			return null;
		}
		return node1.getLink(letter2);
	}

	public Collection<PuzzleNode> getNodes() {
		return nodesByLetter.values();
	}

	public void clear() {
		// R�z liste noeuds
		nodesByLetter.clear();
		// R�z grille
		for (PuzzleNode[] line : nodesByPosition) {
			Arrays.fill(line, null);
		}
	}

	/**
	 * Retourne la disposition actuelle des lettres
	 * @return
	 */
	public String[] getLayout() {
		String[] layout = new String[GRID_HEIGHT];
		StringBuilder lineString = new StringBuilder(GRID_WIDTH);
		for (int curLine = 0; curLine < GRID_HEIGHT; curLine++) {
			PuzzleNode[] line = nodesByPosition[curLine];
			lineString.setLength(0);
			for (int curColumn = 0; curColumn < GRID_WIDTH; curColumn++) {
				PuzzleNode node = line[curColumn];
				if (node == null) {
					lineString.append(SLOT_EMPTY);
				} else {
					lineString.append(node.getLetter());
				}
			}
			layout[curLine] = lineString.toString();
		}
		return layout;
	}

	/**
	 * Retourne la position de la lettre indiqu�e
	 * @param target
	 * @return
	 */
	public Point getPosition(String letter) {
		for (int curLine = 0; curLine < GRID_HEIGHT; curLine++) {
			for (int curColumn = 0; curColumn < GRID_WIDTH; curColumn++) {
				PuzzleNode node = nodesByPosition[curLine][curColumn];
				if (node != null && letter.equals(node.getLetter())) {
					return new Point(curLine, curColumn);
				}
			}
		}
		return null;
	}
}
