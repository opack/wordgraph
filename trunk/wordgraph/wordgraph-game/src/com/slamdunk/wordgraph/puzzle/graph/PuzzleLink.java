package com.slamdunk.wordgraph.puzzle.graph;


/**
 * Repr�sente un ou plusieurs liens entre 2 lettres, et leur �tat
 */
public class PuzzleLink {
	/**
	 * Noeud � une extr�mit� du lien
	 */
	private final PuzzleNode node1;
	/**
	 * Noeud � une extr�mit� du lien
	 */
	private final PuzzleNode node2;
	/**
	 * Nom du lien. Correspond � la concat�nation de
	 * {@link #node1} et {@link #node2}
	 */
	private final String name;
	/**
	 * Nombre de liens s�lectionn�s
	 */
	private int selected;
	/**
	 * Nombre de liens encore disponibles, c'est-�-dire
	 * qui n'ont pas encore �t� valid�s pr�c�demment
	 * mais qui peuvent �tre actuellement s�lectionn�s
	 */
	private int size;
	/**
	 * Indique s'il faut ou non afficher ces liens
	 */
	private boolean visible;
	
	public PuzzleLink(PuzzleNode node1, PuzzleNode node2) {
		this.node1 = node1;
		this.node2 = node2;
		this.name = node1.getLetter() + node2.getLetter();
		size = 1;
		visible = true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PuzzleLink)) {
			return false;
		}
		// Deux liens sont consid�r�s comme identiques s'ils
		// relient les m�mes lettres, et donc qu'ils ont le
		// m�me nom
		PuzzleLink link = (PuzzleLink)obj;
		return name.equals(link.name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public PuzzleNode getNode1() {
		return node1;
	}
	public PuzzleNode getNode2() {
		return node2;
	}
	public String getName() {
		return name;
	}
	public int getSelected() {
		return selected;
	}
	public void setSelected(int selected) {
		this.selected = selected;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * Retourne true s'il reste au moins 1 lien traversable,
	 * donc que getSize()-getSelected()>0.
	 * @return
	 */
	public boolean isAvailable() {
		return size - selected > 0;
	}

	/**
	 * Retourne true si le lien est s�lectionn�, donc que
	 * getSelected()>0
	 * @return
	 */
	public boolean isSelected() {
		return selected > 0;
	}

	/**
	 * Retourne le noeud situ� � l'autre extr�mit� du lien
	 * @param node
	 * @return
	 */
	public PuzzleNode getOtherNode(PuzzleNode node) {
		if (node1.equals(node)) {
			return node2;
		}
		return node1;
	}
}
