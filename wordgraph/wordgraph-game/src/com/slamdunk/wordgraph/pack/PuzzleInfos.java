package com.slamdunk.wordgraph.pack;

import com.slamdunk.wordgraph.puzzle.PuzzleTypes;

/**
 * Stocke les infos sur le puzzle d'un pack
 */
public class PuzzleInfos {
	private String name;
	private String label;
	private String description;
	private int difficulty;
	private PuzzleTypes type;
	private boolean available;
	
	private float goldTime;
	private float silverTime;
	private float bronzeTime;
	
	public float getGoldTime() {
		return goldTime;
	}
	
	public void setGoldTime(float goldTime) {
		this.goldTime = goldTime;
	}
	
	public float getSilverTime() {
		return silverTime;
	}
	
	public void setSilverTime(float silverTime) {
		this.silverTime = silverTime;
	}
	
	public float getBronzeTime() {
		return bronzeTime;
	}
	
	public void setBronzeTime(float bronzeTime) {
		this.bronzeTime = bronzeTime;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getDifficulty() {
		return difficulty;
	}
	
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public PuzzleTypes getType() {
		return type;
	}

	public void setType(PuzzleTypes type) {
		this.type = type;
	}
}
