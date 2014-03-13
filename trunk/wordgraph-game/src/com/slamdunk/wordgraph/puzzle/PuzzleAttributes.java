package com.slamdunk.wordgraph.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.slamdunk.wordgraph.pack.PuzzleInfos;

public class PuzzleAttributes {
	private PuzzleInfos infos;
	private List<Riddle> riddles;
	private String skinName;
	private Skin skin;
	
	public PuzzleAttributes() {
		riddles = new ArrayList<Riddle>();
	}

	public void addRiddle(Riddle riddle) {
		riddles.add(riddle);
	}
	
	public PuzzleInfos getInfos() {
		return infos;
	}

	public void setInfos(PuzzleInfos infos) {
		this.infos = infos;
	}

	/**
	 * Retourne l'enigme correspondant à la suggestion, s'il y en a une
	 * @param suggestion
	 * @return
	 */
	public Riddle getRiddle(String suggestion) {
		if (suggestion != null && !suggestion.isEmpty()) {
			for (Riddle riddle : riddles) {
				if (suggestion.equals(riddle.getSolution())) {
					return riddle;
				}
			}
		}
		return null;
	}

	/**
	 * Indique s'il reste au moins une enigme non solutionnée
	 * @return
	 */
	public boolean remainsRiddles() {
		for (Riddle riddle : riddles) {
			if (!riddle.isFound()) {
				return true;
			}
		}
		return false;
	}

	public List<Riddle> getRiddles() {
		return riddles;
	}

	public Skin getSkin() {
		return skin;
	}

	public void setSkin(Skin skin) {
		this.skin = skin;
	}

	public String getSkinName() {
		return skinName;
	}

	public void setSkinName(String skinName) {
		this.skinName = skinName;
	}
}
