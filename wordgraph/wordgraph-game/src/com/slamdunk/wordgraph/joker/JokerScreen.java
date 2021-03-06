package com.slamdunk.wordgraph.joker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.slamdunk.utils.PropertiesManager;
import com.slamdunk.utils.ui.ButtonClickListener;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.Options;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleScreen;
import com.slamdunk.wordgraph.puzzle.PuzzleTypes;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.obstacles.CategoryObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.HiddenObstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes;

public class JokerScreen implements Screen {
	
	private WordGraphGame game;
	private Stage stage;
	
	private PuzzleScreen puzzleScreen;
	
	private TextButton validateButton;
	
	private Map<String, Riddle> riddlesByName;
	private Riddle selectedRiddle;
	private Joker selectedJoker;
	
	public JokerScreen(WordGraphGame game) {
		this.game = game;
		PropertiesManager.init("jokers");
		stage = new Stage();
		riddlesByName = new HashMap<String, Riddle>();
	}
	
	public void setPuzzleScreen(PuzzleScreen puzzleScreen) {
		this.puzzleScreen = puzzleScreen;
	}

	/**
	 * M�thode appel�e quand le joueur appuie sur la touche Back
	 */
	private void onBack() {
		// Retour � l'�cran puzzle
		game.showPuzzleScreen();
	};
	
	@Override
	public void render(float delta) {
		stage.act();
		
		Gdx.gl.glClearColor(245 / 255f, 237 / 255f, 217 / 255f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(480, 800, true);
	}

	@Override
	public void show() {
        createUI();
        Gdx.input.setInputProcessor(stage);
    }

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
	
	private void createUI() {
		stage.clear();
		
		PuzzleAttributes puzzleAttributes = puzzleScreen.getPuzzleAttributes();
		
		// R�cup�ration de la skin � appliquer
		Skin skin = puzzleAttributes.getSkin();
		if (skin == null) {
			skin = Assets.uiSkin;
		}
		// Cr�ation des composants depuis le layout d�finit dans le SVG
		SvgUICreator creator = new SvgUICreator(skin);
		creator.load("layouts/joker-common.ui.svg");
		if (puzzleAttributes.getInfos().getType() == PuzzleTypes.WORDS) {
			creator.load("layouts/joker-words.ui.svg");
		} else {
			creator.load("layouts/joker-sentence.ui.svg");
		}
		validateButton = (TextButton)creator.getActor("validate");
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setVisible(background.getDrawable() != null);
		
		// Personnalise chaque enigme
		ButtonGroup riddleButtons = new ButtonGroup();
		List<Riddle> riddles = puzzleAttributes.getRiddles();
		if (puzzleAttributes.getInfos().getType() == PuzzleTypes.WORDS) {
			for (int idx = 0; idx < 6; idx ++) {
				TextButton clue = (TextButton)creator.getActor("riddle" + idx);
				if (idx >= riddles.size()) {
					// Il n'y a pas d'enigme pour ce bouton : on le d�sactive
					clue.setDisabled(true);
				} else {
					// Il existe une enigme pour ce bouton
					Riddle riddle = riddles.get(idx);
					
					// Choix du libell� en fonction de la pr�sence d'un obstacle
					if (riddle.isTargeted(ObstaclesTypes.CATEGORY)) {
						// Obstacle cat�gorie : on affiche la cat�gorie
						CategoryObstacle obstacle = (CategoryObstacle)riddle.getObstacles().get(0);
						clue.setText(obstacle.getCategory());
					} else if (riddle.isTargeted(ObstaclesTypes.HIDDEN)) {
						// Obstacle hidden : on affiche le message flou
						HiddenObstacle obstacle = (HiddenObstacle)riddle.getObstacles().get(0);
						LabelStyle labelStyle = obstacle.getLabelStyle();
						TextButtonStyle buttonStyle = new TextButtonStyle(clue.getStyle());
						buttonStyle.font = labelStyle.font;
						clue.setStyle(buttonStyle);
						clue.setText(riddle.getClue());
					} else {
						// Aucun obstacle, on affiche l'indice
						clue.setText(riddle.getClue());
					}
		
					// Si la solution a �t� trouv�e pr�c�demment, on met � jour l'interface
					if (riddle.isFound()) {
						clue.setDisabled(true);
					}
					
					// Ajout du Riddle au bouton
					riddlesByName.put(clue.getName(), riddle);
					
					// Ajout au ButtonGroup pour que seul un bouton soit s�lectionn� � la fois
					riddleButtons.add(clue);
				}
			}
		} else {
			// R�cup�ration des mod�les
			Label textModel = (Label)creator.getActor("riddle");
			TextButton solutionModel = (TextButton)creator.getActor("solution");
			
			// Cr�ation des lignes
			int idx = 0;
			for (String line : puzzleAttributes.getRiddleSentenceLines()) {
				// R�cup�ration de la Table dans laquelle on va �crire
				Table table = (Table)creator.getActor("line" + idx);
				
				// Recherche le prochain place holder
				int start = 0;
				int prevPlaceholderEnd = 0;
				while ((start = line.indexOf("[", prevPlaceholderEnd)) != -1) {
					// Cr�e un label pour mettre tout le texte pr�c�dant le placeholder
					if (start > prevPlaceholderEnd) {
						String blabla = line.substring(prevPlaceholderEnd, start);
						Label blablaLabel = new Label(blabla, textModel.getStyle());
						table.add(blablaLabel);
					}
					
					// R�cup�re le num�ro de l'�nigme, et l'�nigme elle-m�me
					int riddleId = Integer.parseInt(line.substring(start + 1, start + 2));
					Riddle riddle = riddles.get(riddleId);
					
					// Cr�e le bouton et le place dans la table
					TextButton clue = new TextButton(riddle.getClue(), solutionModel.getStyle());
					table.add(clue).height(solutionModel.getHeight());
					if (riddle.isFound()) {
						clue.setText(riddle.getSolution());
						clue.setDisabled(true);
					}
					// Ajout du Riddle au bouton
					riddlesByName.put(clue.getName(), riddle);
					
					// Ajout au ButtonGroup pour que seul un bouton soit s�lectionn� � la fois
					riddleButtons.add(clue);
					
					// Continue la recherche � partir de la fin de ce placeholder
					prevPlaceholderEnd = start + 3;
				}
				// Cr�e un label pour mettre tout le texte restant sur
				// la ligne apr�s le dernier placeholder
				if (prevPlaceholderEnd < line.length()) {
					String blabla = line.substring(prevPlaceholderEnd);
					Label blablaLabel = new Label(blabla, textModel.getStyle());
					table.add(blablaLabel);
				}
				idx++;
			}
		}
		riddleButtons.uncheckAll();
		
		// Ajout des boutons de joker � un group pour que seul un bouton soit s�lectionn� � la fois
		ButtonGroup jokerButtons = new ButtonGroup();
		jokerButtons.add((TextButton)creator.getActor("bishop"));
		jokerButtons.add((TextButton)creator.getActor("rook"));
		jokerButtons.add((TextButton)creator.getActor("knight"));
		jokerButtons.add((TextButton)creator.getActor("queen"));
		jokerButtons.uncheckAll();
		
		// Affectation des listeners
		creator.getActor("back").addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
        		onBack();
			}
        });
		final EventListener riddleListener = new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				selectedRiddle = riddlesByName.get(button.getName());
				activateValidation();
			}
		};
		for (Button button : riddleButtons.getButtons()) {
			button.addListener(riddleListener);
		}
		final Label nameLabel = (Label)creator.getActor("name");
		final Label descriptionLabel = (Label)creator.getActor("description");
		final EventListener jokerListener = new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				// Choisit le joker s�lectionn�
				selectedJoker = Joker.valueOf(button.getName());
				// Change les libell�s
				String nameKey = selectedJoker.toString() + ".name." + Options.langCode;
				String nameText = PropertiesManager.getString("jokers", nameKey, "");
				nameLabel.setText(nameText);
				String descriptionKey = selectedJoker.toString() + ".description." + Options.langCode;
				String descriptionText = PropertiesManager.getString("jokers", descriptionKey, "");
				descriptionLabel.setText(descriptionText);
				// Active si possible le bouton de validation
				activateValidation();
			}
		};
		for (Button button : jokerButtons.getButtons()) {
			button.addListener(jokerListener);
		}
		validateButton.addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
        		onValidate();
			}
        });
		activateValidation();
        stage.addListener(new InputListener(){
        	@Override
     		public boolean keyUp(InputEvent event, int keycode) {
     			onBack();
				return true;
     		};
     	});
		
		// Ajout des composants au stage
		creator.populate(stage);
		
		// Raz de la s�lection
		selectedRiddle = null;
		selectedJoker = null;
		activateValidation();
	}
	
	private void activateValidation() {
		validateButton.setDisabled(
			selectedRiddle == null
			|| selectedRiddle.isFound()
			|| selectedJoker == null);
	}
	
	private void onValidate() {
		if (selectedRiddle != null && selectedJoker != null) {
			selectedJoker.decorate(selectedRiddle, puzzleScreen);
			onBack();
		}
	}
}
