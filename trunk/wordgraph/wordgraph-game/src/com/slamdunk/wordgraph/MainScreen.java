package com.slamdunk.wordgraph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.utils.MessageBoxUtils;
import com.slamdunk.utils.ui.ButtonClickListener;
import com.slamdunk.utils.ui.svg.SvgUICreator;

public class MainScreen implements Screen {

	private WordGraphGame game;
	private Stage stage;
	
	public MainScreen(WordGraphGame game) {
		this.game = game;
		stage = new Stage();
	}
	
	@Override
	public void render(float delta) {
		// Mise à jour des acteurs
        stage.act(Gdx.graphics.getDeltaTime());
        
        // Dessin des autres composants du stage (label, boutons, noeuds...)
        Gdx.gl.glClearColor(245 / 255f, 237 / 255f, 217 / 255f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.draw();
	}

	/**
	 * Méthode appelée quand le joueur appuie sur la touche Back
	 */
	private void onBack() {
		MessageBoxUtils.showConfirm(
			"Quitter le jeu ?",
			stage,
			new ButtonClickListener() {
				@Override
				public void clicked(Button button) {
					Gdx.app.exit();
				}
			},
			null);
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
		// Création des composants depuis le layout définit dans le SVG
		SvgUICreator creator = new SvgUICreator(Assets.uiSkin);
		creator.load("layouts/main.ui.svg");
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setVisible(background.getDrawable() != null);
		
		// Affectation des listeners
		creator.getActor("play").addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				game.showPackListScreen();
			}
        });
		creator.getActor("quit").addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
        		onBack();
			}
        });
        // Ajout du listener du bouton back
        stage.addListener(new InputListener(){
        	@Override
     		public boolean keyUp(InputEvent event, int keycode) {
     			onBack();
				return true;
     		};
     	});
        
        // Ajout des composants au stage
        creator.populate(stage);
        
        // On place le background tout au fond
     	background.setZIndex(0);
	}
}
