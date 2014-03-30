package com.slamdunk.wordgraph.puzzle;


public class Riddle extends ObstacleTarget {
	private int id;
	private String clue;
	private String solution;
	private int difficulty;
	private boolean found;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClue() {
		return clue;
	}
	public void setClue(String clue) {
		this.clue = clue;
	}
	public String getSolution() {
		return solution;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public boolean isFound() {
		return found;
	}
	public void setFound(boolean found) {
		this.found = found;
	}
}
