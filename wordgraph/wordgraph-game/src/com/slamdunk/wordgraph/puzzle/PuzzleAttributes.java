package com.slamdunk.wordgraph.puzzle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.slamdunk.wordgraph.pack.PuzzleInfos;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLayout;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstacleManager;

public class PuzzleAttributes {
	private PuzzleInfos infos;
	private List<Riddle> riddles;
	private List<String> riddleSentenceLines;
	private PuzzleLayout layout;
	private String skinName;
	private Skin skin;
	private ObstacleManager obstacleManager;
	
	public PuzzleAttributes() {
		riddles = new ArrayList<Riddle>();
		riddleSentenceLines = new ArrayList<String>();
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
	 * Retourne l'enigme correspondant � la suggestion, s'il y en a une
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
	 * Retourne l'�nigme d'identifiant indiqu�
	 * @param riddleId
	 * @return
	 */
	public Riddle getRiddle(int riddleId) {
		return riddles.get(riddleId);
	}

	/**
	 * Indique s'il reste au moins une enigme non solutionn�e
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

	public List<String> getRiddleSentenceLines() {
		return riddleSentenceLines;
	}

	public void addRiddleSentenceLine(String line) {
		riddleSentenceLines.add(line);
	}

	public String getRiddleSentenceLine(int lineNum) {
		return riddleSentenceLines.get(lineNum);
	}

	public ObstacleManager getObstacleManager() {
		return obstacleManager;
	}
	
	public void setObstacleManager(ObstacleManager obstacleManager) {
		this.obstacleManager = obstacleManager;
	}

	public PuzzleLayout getLayout() {
		return layout;
	}

	public void setLayout(PuzzleLayout layout) {
		this.layout = layout;
	}
}
