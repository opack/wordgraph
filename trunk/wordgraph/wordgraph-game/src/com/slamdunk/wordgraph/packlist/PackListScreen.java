package com.slamdunk.wordgraph.packlist;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.utils.ui.ButtonClickListener;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.packlist.parsing.PackListAttributesReader;

public class PackListScreen implements Screen {
	
	private WordGraphGame game;
	private Stage stage;
	
	private List<String> packList;
	
	public PackListScreen(WordGraphGame game) {
		this.game = game;
		stage = new Stage();
	}
	
	/**
	 * Méthode appelée quand le joueur appuie sur la touche Back
	 */
	private void onBack() {
		game.showMainScreen();
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
        // Chargement des packs installés
     	packList = new PackListAttributesReader().read("puzzles/packlist");
        
     	// Création de l'interface
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
		creator.load("layouts/packlist.ui.svg");
		
		// Affectation des listeners
		creator.getActor("back").addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
        		onBack();
			}
        });
        stage.addListener(new InputListener(){
        	@Override
     		public boolean keyUp(InputEvent event, int keycode) {
     			onBack();
				return true;
     		};
     	});
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setVisible(background.getDrawable() != null);
		
		// Ajoute un bouton pour chaque pack
		PackListButtonDecorator decorator = new PackListButtonDecorator(game);
		final Table scrollTable = new Table();
		int count = 0;
		for (String packName : packList) {
			TextButton button = new TextButton(packName, decorator.getDefaultStyle());
			decorator.decorate(packName, button);
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
		
		// On place le background tout au fond
		background.setZIndex(0);
	}
}
