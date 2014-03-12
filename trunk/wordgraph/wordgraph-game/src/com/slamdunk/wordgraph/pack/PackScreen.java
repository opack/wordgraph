package com.slamdunk.wordgraph.pack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.pack.parsing.PackAttributesReader;

public class PackScreen implements Screen {
	
	private WordGraphGame game;
	private Stage stage;
	
	private String packName;
	private PackAttributes packAttributes;
	
	public PackScreen(WordGraphGame game) {
		this.game = game;
		
		stage = new Stage();
		stage.addListener(new InputListener(){
     		public boolean keyUp(InputEvent event, int keycode) {
     			onBack();
				return true;
     		}
     	});
	}
	
	public void setPackName(String packName) {
		this.packName = packName;
	}

	/**
	 * Méthode appelée quand le joueur appuie sur la touche Back
	 */
	private void onBack() {
		game.showPackListScreen();
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
     	// Création de l'interface
     	loadPack();
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
		// Création des composants depuis le layout définit dans le SVG
		SvgUICreator creator = new SvgUICreator(Assets.uiSkin);
		creator.load("layouts/pack.ui.svg");
		
		// Affectation des listeners
		creator.getActor("back").addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		onBack();
			}
        });
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setZIndex(0);
		background.setVisible(background.getDrawable() != null);
		
		// Ajoute un bouton pour chaque pack
		PackButtonDecorator decorator = new PackButtonDecorator(packAttributes, game);
		final Table scrollTable = new Table();
		int count = 0;
		for (PuzzleInfos puzzleInfos : packAttributes.getAllPuzzleInfos()) {
			TextButton button = new TextButton("", decorator.getDefaultStyle());
			decorator.decorate(puzzleInfos.getName(), button);
			scrollTable.add(button).size(175, 90).pad(10);
			if (count % 2 == 1) {
				scrollTable.row();
			}
			count++;
		}
		ScrollPane scroller = (ScrollPane)creator.getActor("scroller");
		scroller.setWidget(scrollTable);
		
		// Ajout des composants au stage
		creator.populate(stage);
	}
	
	/**
	 * Charge le pack de puzzles
	 * @param puzzleName
	 */
	public void loadPack() {
		// Chargement des attributs du puzzle
		packAttributes = new PackAttributesReader().read("puzzles/" + packName, "pack.properties");
	}
}
