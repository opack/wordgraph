package com.slamdunk.wordgraph.joker;

import java.util.List;

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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.slamdunk.utils.PropertiesManager;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.Options;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleTypes;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

public class JokerScreen implements Screen {
	
	private WordGraphGame game;
	private Stage stage;
	private Graph graph;
	private PuzzleAttributes puzzleAttributes;
	
	private TextButton validateButton;
	
	private Riddle selectedRiddle;
	private Joker selectedJoker;
	
	public JokerScreen(WordGraphGame game) {
		this.game = game;
		PropertiesManager.init("jokers");
		stage = new Stage();
		stage.addListener(new InputListener(){
     		public boolean keyUp(InputEvent event, int keycode) {
     			onBack();
				return true;
     		}
     	});
	}
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public void setPuzzleAttributes(PuzzleAttributes puzzleAttributes) {
		this.puzzleAttributes = puzzleAttributes;
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
		// R�cup�ration de la skin � appliquer
		Skin skin = puzzleAttributes.getSkin();
		if (skin == null) {
			skin = Assets.uiSkin;
		}
		// Cr�ation des composants depuis le layout d�finit dans le SVG
		SvgUICreator creator = new SvgUICreator(skin);
		if (puzzleAttributes.getInfos().getType() == PuzzleTypes.WORDS) {
			creator.load("layouts/joker-words.ui.svg");
		} else {
			creator.load("layouts/joker-sentence.ui.svg");
		}
		validateButton = (TextButton)creator.getActor("validate");
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setZIndex(0);
		background.setVisible(background.getDrawable() != null);
		
		// Personnalise chaque enigme
		ButtonGroup riddleButtons = new ButtonGroup();
		List<Riddle> riddles = puzzleAttributes.getRiddles();
		for (int idx = 0; idx < 7; idx ++) {
			TextButton clue = (TextButton)creator.getActor("riddle" + idx);
			if (idx >= riddles.size()) {
				// Il n'y a pas d'enigme pour ce bouton : on le d�sactive
				clue.setDisabled(true);
			} else {
				// Il existe une enignme pour ce bouton
				Riddle riddle = riddles.get(idx);
				clue.setText(riddle.getClue());
	
				// Si la solution a �t� trouv�e pr�c�demment, on met � jour l'interface
				if (riddle.isFound()) {
					clue.setDisabled(true);
				}
				
				// Ajout du Riddle au bouton
				clue.setUserObject(riddle);
				
				// Ajout au ButtonGroup pour que seul un bouton soit s�lectionn� � la fois
				riddleButtons.add(clue);
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
		creator.getActor("back").addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		onBack();
			}
        });
		final EventListener riddleListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Button button = (Button)event.getListenerActor();
				if (button.isDisabled()) {
					return;
				}
				selectedRiddle = (Riddle)button.getUserObject();
				activateValidation();
			}
		};
		for (Button button : riddleButtons.getButtons()) {
			button.addListener(riddleListener);
		}
		final Label descriptionLabel = (Label)creator.getActor("description");
		final EventListener jokerListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Choisit le joker s�lectionn�
				selectedJoker = Joker.valueOf(event.getListenerActor().getName());
				// Change les libell�s
				String funnyKey = selectedJoker.toString() + ".funny." + Options.langCode;
				String funnyText = PropertiesManager.asString("jokers", funnyKey, "");
				String descriptionKey = selectedJoker.toString() + ".description." + Options.langCode;
				String descriptionText = PropertiesManager.asString("jokers", descriptionKey, "");
				descriptionLabel.setText(funnyText + "\r\n" + descriptionText);
				// Active si possible le bouton de validation
				activateValidation();
			}
		};
		for (Button button : jokerButtons.getButtons()) {
			button.addListener(jokerListener);
		}
		validateButton.addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Button button = (Button)event.getListenerActor();
        		if (button.isDisabled()) {
        			return;
        		}
        		onValidate();
			}
        });
		activateValidation();
		
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
			selectedJoker.decorate(selectedRiddle.getSolution(), graph);
			onBack();
		}
	}
}