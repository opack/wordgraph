package com.slamdunk.wordgraph.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.slamdunk.wordgraph.pack.PuzzleInfos;
import com.slamdunk.wordgraph.puzzle.obstacles.FogObstacleManager;
import com.slamdunk.wordgraph.puzzle.obstacles.IsleObstacleManager;

public class PuzzleAttributes {
	private PuzzleInfos infos;
	private List<Riddle> riddles;
	private List<String> lines;
	private String skinName;
	private Skin skin;
	private IsleObstacleManager isleObstacleManager;
	private FogObstacleManager fogObstacleManager;
	
	public PuzzleAttributes() {
		riddles = new ArrayList<Riddle>();
		lines = new ArrayList<String>();
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
	 * Retourne l'énigme d'identifiant indiqué
	 * @param riddleId
	 * @return
	 */
	public Riddle getRiddle(int riddleId) {
		return riddles.get(riddleId);
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

	public List<String> getLines() {
		return lines;
	}

	public void addLine(String line) {
		lines.add(line);
	}

	public String getLine(int lineNum) {
		return lines.get(lineNum);
	}

	public IsleObstacleManager getIsleObstacleManager() {
		return isleObstacleManager;
	}
	
	public void setIsleObstacleManager(IsleObstacleManager isleObstacleManager) {
		this.isleObstacleManager = isleObstacleManager;
	}

	public FogObstacleManager getFogObstacleManager() {
		return fogObstacleManager;
	}

	public void setFogObstacleManager(FogObstacleManager fogObstacleManager) {
		this.fogObstacleManager = fogObstacleManager;
	}
}
