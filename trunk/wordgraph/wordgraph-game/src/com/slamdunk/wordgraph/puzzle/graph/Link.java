package com.slamdunk.wordgraph.puzzle.graph;

/**
 * Représente un lien entre 2 lettres, et son état
 */
public class Link {
	private String endpoint1;
	private String endpoint2;
	private boolean used;
	private boolean selected;
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
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
