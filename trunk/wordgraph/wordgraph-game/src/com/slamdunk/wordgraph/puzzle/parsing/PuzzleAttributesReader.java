package com.slamdunk.wordgraph.puzzle.parsing;

import java.io.IOException;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.pack.PuzzleInfos;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleTypes;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.obstacles.FogObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.FogObstacleManager;
import com.slamdunk.wordgraph.puzzle.obstacles.IntruderObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.IntruderObstacleManager;
import com.slamdunk.wordgraph.puzzle.obstacles.IsleObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.IsleObstacleManager;

public class PuzzleAttributesReader {

	/**
	 * Lit le fichier de propriétés spécifié et crée un objet PuzzleAttributes
	 * @param file
	 * @return
	 */
	public PuzzleAttributes read(String file) {
		// Ouverture du fichier
		Properties propertiesFile = new Properties();
		try {
			propertiesFile.load(Gdx.files.internal(file).reader());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PuzzleAttributes puzzleAttributes = new PuzzleAttributes();
		
		// Charge les attributs du puzzle
		loadGeneralInfos(propertiesFile, puzzleAttributes);
		
		// Charge les graphismes du puzzle (images de fond, images des boutons...)
		// ...
		
		// Charge la skin pour les widgets de l'UI
		loadSkin(propertiesFile, puzzleAttributes);

		// Charge les lignes d'indice
		if (puzzleAttributes.getInfos().getType() == PuzzleTypes.SENTENCE) {
			loadSentenceLines(propertiesFile, puzzleAttributes);
		}
		
		// Charge les enigmes
		loadRiddles(propertiesFile, puzzleAttributes);
		
		// Charge les obstacles
		loadObstacles(propertiesFile, puzzleAttributes);

		return puzzleAttributes;
	}
	
	private void loadGeneralInfos(Properties propertiesFile, PuzzleAttributes puzzleAttributes) {
		String label = propertiesFile.getProperty("label", "");
		String description = propertiesFile.getProperty("description", "");
		int difficulty = Integer.parseInt(propertiesFile.getProperty("difficulty", "1"));
		String type = propertiesFile.getProperty("type", "WORDS");
		
		float goldTime = Float.parseFloat(propertiesFile.getProperty("time.gold", "0"));
		float silverTime = Float.parseFloat(propertiesFile.getProperty("time.silver", "0"));
		float bronzeTime = Float.parseFloat(propertiesFile.getProperty("time.bronze", "0"));
		
		PuzzleInfos infos = new PuzzleInfos();
		infos.setLabel(label);
		infos.setDescription(description);
		infos.setDifficulty(difficulty);
		infos.setType(PuzzleTypes.valueOf(type.toUpperCase()));
		
		infos.setGoldTime(goldTime);
		infos.setSilverTime(silverTime);
		infos.setBronzeTime(bronzeTime);
		
		puzzleAttributes.setInfos(infos);
	}
	
	private void loadSkin(Properties propertiesFile, PuzzleAttributes puzzleAttributes) {
		String skinName = propertiesFile.getProperty("skin.name", "");
		if (!skinName.isEmpty()) {
			// Chargement de la skin indiquée
			Skin skin = Assets.getSkin(skinName);
			if (skin == null) {
				skin = Assets.loadSkin(skinName);
			}
			puzzleAttributes.setSkinName(skinName);
			puzzleAttributes.setSkin(skin);
			
			// Si la skin ne contient pas tous les styles nécessaires, on complète
			// avec la skin par défaut
			// TextButtonStyles
			addDefault(skin, "puzzle-back", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-backspace", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-joker", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-letter", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-letter-selected", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-letter-highlighted", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-validate", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "joker-riddle", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "joker-bishop", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "joker-rook", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "joker-knight", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "joker-queen", TextButtonStyle.class, Assets.defaultPuzzleSkin);
			// LabelStyles
			addDefault(skin, "mainTitle", LabelStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-title", LabelStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-riddle", LabelStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-score", LabelStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-solution", LabelStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "puzzle-suggestion", LabelStyle.class, Assets.defaultPuzzleSkin);
			addDefault(skin, "text", LabelStyle.class, Assets.defaultPuzzleSkin);

		}
	}

	/**
	 * Ajoute une valeur par défaut à la skin si elle ne contient pas le style indiqué.
	 * La valeur par défaut est tirée de la skin defaultSkin spécifiée.
	 * @param skinToCheck
	 * @param styleName
	 * @param styleClass
	 * @param defaultSkin
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addDefault(Skin skinToCheck, String styleName, Class styleClass, Skin defaultSkin) {
		if (!skinToCheck.has(styleName, styleClass)) {
			skinToCheck.add(styleName, defaultSkin.get(styleName, styleClass));
		}		
	}

	/**
	 * Charge les enigmes définies dans le fichier de propriétés.
	 * @param propertiesFile
	 * @param puzzleAttributes
	 */
	private void loadRiddles(Properties propertiesFile, PuzzleAttributes puzzleAttributes) {
		int id = 0;
		String clue = null;
		String solution = null;
		int difficulty = 0;
		while ((solution = propertiesFile.getProperty("riddle." + id + ".solution")) != null) {
			clue = propertiesFile.getProperty("riddle." + id + ".clue", "");
			difficulty = Integer.parseInt(propertiesFile.getProperty("riddle." + id + ".difficulty", "1"));

			Riddle riddle = new Riddle();
			riddle.setId(id);
			riddle.setClue(clue);
			riddle.setSolution(solution);
			riddle.setDifficulty(difficulty);
			puzzleAttributes.addRiddle(riddle);
			
			id++;
		}
	}
	
	/**
	 * Charge les lignes contenant la phrase à trouver définies dans le fichier de propriétés.
	 * @param propertiesFile
	 * @param puzzleAttributes
	 */
	private void loadSentenceLines(Properties propertiesFile, PuzzleAttributes puzzleAttributes) {
		int id = 0;
		String line = null;
		while ((line = propertiesFile.getProperty("sentence.line." + id)) != null) {
			puzzleAttributes.addLine(line);
			id++;
		}
	}
	
	/**
	 * Charge les obstacles définis dans le fichier de propriétés.
	 * @param propertiesFile
	 * @param puzzleAttributes
	 */
	private void loadObstacles(Properties propertiesFile, PuzzleAttributes puzzleAttributes) {
		// Lettres isolées
		String obstacleIsle = propertiesFile.getProperty("obstacles.isle", "");
		if (!obstacleIsle.isEmpty()) {
			IsleObstacleManager isleObstacleManager = new IsleObstacleManager();
			String[] isolatedLetters = obstacleIsle.split(",");
			for (String letter : isolatedLetters) {
				isleObstacleManager.add(letter, new IsleObstacle(letter));
			}
			puzzleAttributes.setIsleObstacleManager(isleObstacleManager);
		}
		
		// Lettres cachées
		String obstacleFog = propertiesFile.getProperty("obstacles.fog", "");
		if (!obstacleFog.isEmpty()) {
			FogObstacleManager fogObstacleManager = new FogObstacleManager();
			String[] maskedLetters = obstacleFog.split(",");
			for (String letter : maskedLetters) {
				fogObstacleManager.add(letter, new FogObstacle(letter));
			}
			puzzleAttributes.setFogObstacleManager(fogObstacleManager);
		}
		
		// Lettres intruses
		String obstacleIntruder = propertiesFile.getProperty("obstacles.intruder", "");
		if (!obstacleIntruder.isEmpty()) {
			IntruderObstacleManager intruderObstacleManager = new IntruderObstacleManager();
			String[] intrudingLetters = obstacleIntruder.split(",");
			for (String letter : intrudingLetters) {
				intruderObstacleManager.add(letter, new IntruderObstacle(letter));
			}
			puzzleAttributes.setIntruderObstacleManager(intruderObstacleManager);
		}
	}
}
