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
import com.slamdunk.wordgraph.puzzle.Riddle;

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
		
		// Charge les enigmes
		loadRiddles(propertiesFile, puzzleAttributes);

		return puzzleAttributes;
	}
	
	private void loadGeneralInfos(Properties propertiesFile, PuzzleAttributes puzzleAttributes) {
		String label = propertiesFile.getProperty("label", "");
		String description = propertiesFile.getProperty("description", "");
		int difficulty = Integer.parseInt(propertiesFile.getProperty("difficulty", "1"));
		
		float goldTime = Float.parseFloat(propertiesFile.getProperty("time.gold", "0"));
		float silverTime = Float.parseFloat(propertiesFile.getProperty("time.silver", "0"));
		float bronzeTime = Float.parseFloat(propertiesFile.getProperty("time.bronze", "0"));
		
		PuzzleInfos infos = new PuzzleInfos();
		infos.setLabel(label);
		infos.setDescription(description);
		infos.setDifficulty(difficulty);
		
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
//			Drawable "puzzle-background" : facultatif
//			TextButtonStyle "puzzle-letter"
			addDefault(skin, "puzzle-letter", TextButtonStyle.class, Assets.defaultPuzzleSkin);
//			TextButtonStyle "puzzle-letter-highlighted"
			addDefault(skin, "puzzle-letter-highlighted", TextButtonStyle.class, Assets.defaultPuzzleSkin);
//			TextButtonStyle "puzzle-validate"
			addDefault(skin, "puzzle-validate", TextButtonStyle.class, Assets.defaultPuzzleSkin);
//			TextButtonStyle "puzzle-cancel"
			addDefault(skin, "puzzle-cancel", TextButtonStyle.class, Assets.defaultPuzzleSkin);
//			TextButtonStyle "puzzle-back"
			addDefault(skin, "puzzle-back", TextButtonStyle.class, Assets.defaultPuzzleSkin);
//			TextButtonStyle "puzzle-joker"
			addDefault(skin, "puzzle-joker", TextButtonStyle.class, Assets.defaultPuzzleSkin);
//			LabelStyle "puzzle-riddle"
			addDefault(skin, "puzzle-riddle", LabelStyle.class, Assets.defaultPuzzleSkin);
//			LabelStyle "puzzle-solution"
			addDefault(skin, "puzzle-solution", LabelStyle.class, Assets.defaultPuzzleSkin);
//			LabelStyle "puzzle-score"
			addDefault(skin, "puzzle-score", LabelStyle.class, Assets.defaultPuzzleSkin);
//			LabelStyle "puzzle-suggestion"
			addDefault(skin, "puzzle-suggestion", LabelStyle.class, Assets.defaultPuzzleSkin);
//			LabelStyle "puzzle-title"
			addDefault(skin, "puzzle-title", LabelStyle.class, Assets.defaultPuzzleSkin);
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
	 * Charge les enigmes définies dans le fichier de propriétés
	 * @param propertiesFile
	 * @param puzzleAttributes
	 */
	private void loadRiddles(Properties propertiesFile, PuzzleAttributes puzzleAttributes) {
		int count = 0;
		String clue = null;
		String solution = null;
		int difficulty = 0;
		while ((solution = propertiesFile.getProperty("riddle." + count + ".solution")) != null) {
			clue = propertiesFile.getProperty("riddle." + count + ".clue", "");
			difficulty = Integer.parseInt(propertiesFile.getProperty("riddle." + count + ".difficulty", "1"));
					
			Riddle riddle = new Riddle();
			riddle.setClue(clue);
			riddle.setSolution(solution);
			riddle.setDifficulty(difficulty);
			puzzleAttributes.addRiddle(riddle);
			
			count++;
		}
	}
}
