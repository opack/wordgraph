package com.slamdunk.wordgraph.pack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PackAttributes {
	private String name;
	private String label;
	private int difficulty;
	private boolean available;
	private Map<String, PuzzleInfos> puzzles;
	
	public PackAttributes() {
		puzzles = new HashMap<String, PuzzleInfos>();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public void setPuzzleInfos(String name, PuzzleInfos infos) {
		puzzles.put(name, infos);
	}

	public PuzzleInfos getPuzzleInfos(String name) {
		return puzzles.get(name);
	}

	public Collection<PuzzleInfos> getAllPuzzleInfos() {
		return puzzles.values();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
}
