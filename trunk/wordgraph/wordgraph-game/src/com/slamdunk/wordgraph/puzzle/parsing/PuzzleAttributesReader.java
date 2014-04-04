package com.slamdunk.wordgraph.puzzle.parsing;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.pack.PuzzleInfos;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleTypes;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLayout;
import com.slamdunk.wordgraph.puzzle.obstacles.BombObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.CategoryObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.FogObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.HiddenObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.IntruderObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.IsleObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.MorphObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.Obstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstacleManager;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes;
import com.slamdunk.wordgraph.puzzle.obstacles.StoneObstacle;

public class PuzzleAttributesReader {

	/**
	 * Lit le fichier de propriétés spécifié et crée un objet PuzzleAttributes
	 * @param file
	 * @return
	 */
	public PuzzleAttributes read(String file) {
		// Ouverture du fichier
		PropertiesEx properties = new PropertiesEx();
		try {
			properties.load(Gdx.files.internal(file).reader());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PuzzleAttributes puzzleAttributes = new PuzzleAttributes();
		
		// Charge les attributs du puzzle
		loadGeneralInfos(properties, puzzleAttributes);
		
		// Charge les graphismes du puzzle (images de fond, images des boutons...)
		// ...
		
		// Charge la skin pour les widgets de l'UI
		loadSkin(properties, puzzleAttributes);

		// Charge les lignes d'indice
		if (puzzleAttributes.getInfos().getType() == PuzzleTypes.SENTENCE) {
			loadSentenceLines(properties, puzzleAttributes);
		}
		
		// Charge les enigmes
		loadRiddles(properties, puzzleAttributes);
		
		// Charge les obstacles
		loadObstacles(properties, puzzleAttributes);
		
		// Charge le layout du puzzle
		loadLayout(properties, puzzleAttributes);

		return puzzleAttributes;
	}
	
	private void loadGeneralInfos(PropertiesEx properties, PuzzleAttributes puzzleAttributes) {
		String label = properties.getProperty("label", "");
		String description = properties.getProperty("description", "");
		int difficulty = properties.getIntegerProperty("difficulty", 1);
		String type = properties.getProperty("type", "WORDS");
		boolean grid = properties.getBooleanProperty("grid", false);
		
		float goldTime = properties.getFloatProperty("time.gold", 0);
		float silverTime = properties.getFloatProperty("time.silver", 0);
		float bronzeTime = properties.getFloatProperty("time.bronze", 0);
		
		PuzzleInfos infos = new PuzzleInfos();
		infos.setLabel(label);
		infos.setDescription(description);
		infos.setDifficulty(difficulty);
		infos.setType(PuzzleTypes.valueOf(type.toUpperCase()));
		infos.setGrid(grid);
		
		infos.setGoldTime(goldTime);
		infos.setSilverTime(silverTime);
		infos.setBronzeTime(bronzeTime);
		
		puzzleAttributes.setInfos(infos);
	}
	
	private void loadSkin(PropertiesEx properties, PuzzleAttributes puzzleAttributes) {
		String skinName = properties.getProperty("skin.name", "");
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
			addDefault(skin, "puzzle-bullet", TextButtonStyle.class, Assets.defaultPuzzleSkin);
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
			// TextureRegions
			// Pas possible d'utiliser les TextureRegions d'un autre atlas car si on décharge
			// l'atlas enrichi avec la valeur par défaut, la TextureRegion source sera déchargée
			// aussi. Du coup, l'atlas source sera incohérent et cela provoque des problèmes
			// d'affichage. Le plus simple est donc de s'assurer que les skins disposent bien
			// des TextureRegions nécessaires.
			// -> link-normal
			// -> link-highlighted
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
	 * @param properties
	 * @param puzzleAttributes
	 */
	private void loadRiddles(PropertiesEx properties, PuzzleAttributes puzzleAttributes) {
		int id = 0;
		String clue = null;
		String solution = null;
		int difficulty = 0;
		while ((solution = properties.getProperty("riddle." + id + ".solution")) != null) {
			clue = properties.getProperty("riddle." + id + ".clue", "");
			difficulty = properties.getIntegerProperty("riddle." + id + ".difficulty", 1);

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
	 * @param properties
	 * @param puzzleAttributes
	 */
	private void loadSentenceLines(PropertiesEx properties, PuzzleAttributes puzzleAttributes) {
		int id = 0;
		String line = null;
		while ((line = properties.getProperty("sentence.line." + id)) != null) {
			puzzleAttributes.addRiddleSentenceLine(line);
			id++;
		}
	}
	
	/**
	 * Charge les obstacles définis dans le fichier de propriétés.
	 * @param properties
	 * @param puzzleAttributes
	 */
	private void loadObstacles(PropertiesEx properties, PuzzleAttributes puzzleAttributes) {
		// Création du gestionnaire d'obstacles
		ObstacleManager manager = new ObstacleManager();
		puzzleAttributes.setObstacleManager(manager);
		
		// Parcourt de tous les obstacles placés sur la grille
		int obstacleId = 0;
		String type;
		while ((type = properties.getProperty("obstacle." + obstacleId + ".type")) != null) {
			Obstacle obstacle = null;
			switch (ObstaclesTypes.valueOf(type)) {
			case ISLE:
				obstacle = new IsleObstacle();
				break;
			case FOG:
				obstacle = new FogObstacle();
				break;
			case INTRUDER:
				obstacle = new IntruderObstacle();
				break;
			case MORPH:
				obstacle = new MorphObstacle();
				break;
			case BOMB:
				obstacle = new BombObstacle();
				break;
			case STONE:
				obstacle = new StoneObstacle();
				break;
			case CATEGORY:
				obstacle = new CategoryObstacle();
				break;
			case HIDDEN:
				obstacle = new HiddenObstacle();
				break;
			}
			obstacle.setId(obstacleId);
			obstacle.initFromProperties(properties, "obstacle." + obstacleId);
			manager.add(obstacle);
			
			obstacleId++;
		}
	}

	/**
	 * Charge les lignes contenant le layout
	 * @param properties
	 * @param puzzleAttributes
	 */
	private void loadLayout(PropertiesEx properties, PuzzleAttributes puzzleAttributes) {
		// Récupération de la taille de la grille et des lettres
		int width = properties.getIntegerProperty("layout.width", -1);
		int height = properties.getIntegerProperty("layout.height", -1);
		PuzzleLayout layout = new PuzzleLayout(width, height);
		
		for (int curLine = 0; curLine < height; curLine ++) {
			// Récupère les lettres du layout
			String[] letters = properties.getProperty("layout.line" + curLine).split(",");
			
			// Remplit le layout
			for (int curColumn = 0; curColumn < width; curColumn++) {
				layout.setLetter(curLine, curColumn, letters[curColumn]);
			}
		}
		
		puzzleAttributes.setLayout(layout);
	}
}
