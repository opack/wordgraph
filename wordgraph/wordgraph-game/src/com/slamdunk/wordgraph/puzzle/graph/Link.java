package com.slamdunk.wordgraph.puzzle.graph;

/**
 * Repr�sente un ou plusieurs liens entre 2 lettres, et leur �tat
 */
public class Link {
	private String endpoint1;
	private String endpoint2;
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
	
	public String getEndpoint1() {
		return endpoint1;
	}
	public void setEndpoint1(String endpoint1) {
		this.endpoint1 = endpoint1;
	}
	public String getEndpoint2() {
		return endpoint2;
	}
	public void setEndpoint2(String endpoint2) {
		this.endpoint2 = endpoint2;
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
	 * Retourne vrai s'il reste au moins 1 lien traversable,
	 * donc que getSize()-getSelected()>0.
	 * @return
	 */
	public boolean isAvailable() {
		return size - selected > 0;
	}
}
