package com.slamdunk.wordgraph.puzzle.graph;


/**
 * Représente un ou plusieurs liens entre 2 lettres, et leur état
 */
public class PuzzleLink {
	/**
	 * Noeud à une extrémité du lien
	 */
	private final PuzzleNode node1;
	/**
	 * Noeud à une extrémité du lien
	 */
	private final PuzzleNode node2;
	/**
	 * Nom du lien. Correspond à la concaténation de
	 * {@link #node1} et {@link #node2}
	 */
	private final String name;
	/**
	 * Nombre de liens sélectionnés
	 */
	private int selected;
	/**
	 * Nombre de liens encore disponibles, c'est-à-dire
	 * qui n'ont pas encore été validés précédemment
	 * mais qui peuvent être actuellement sélectionnés
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
		// Deux liens sont considérés comme identiques s'ils
		// relient les mêmes lettres, et donc qu'ils ont le
		// même nom
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
	 * Retourne true si le lien est sélectionné, donc que
	 * getSelected()>0
	 * @return
	 */
	public boolean isSelected() {
		return selected > 0;
	}

	/**
	 * Retourne le noeud situé à l'autre extrémité du lien
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
