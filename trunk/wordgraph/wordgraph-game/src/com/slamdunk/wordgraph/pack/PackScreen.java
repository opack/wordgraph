package com.slamdunk.wordgraph.pack;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.pack.parsing.PackAttributesReader;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

public class PackScreen implements Screen {
	private WordGraphGame game;
	
	private String packName;
	private Stage stage;
	private Graph graph;
	private PackAttributes packAttributes;
	
	public PackScreen(WordGraphGame game) {
		this.game = game;
		
		stage = new Stage();
		stage.addListener(new InputListener(){
     		public boolean keyUp(InputEvent event, int keycode) {
     			onBack();
				return true;
     		};
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
	
	private void createUI() {
		stage.clear();
		// Création des composants depuis le layout définit dans le SVG
		SvgUICreator creator = new SvgUICreator(Assets.uiSkin);
		creator.load("layouts/pack.ui.svg");
		
		// Chargement du graph
		graph = (Graph)creator.getActor("graph");
		graph.load("puzzles/" + packName + "/pack.gml", new PackButtonDecorator(packAttributes, game));
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setZIndex(0);
		background.setVisible(background.getDrawable() != null);
		
		// Affectation des listeners
		creator.getActor("back").addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		onBack();
			}
        });
		
		// Ajout des composants au stage
		creator.populate(stage);
	}
	
	/**
	 * Charge un puzzle et crée le graphe
	 * @param puzzleName
	 */
	public void loadPack() {
		// Chargement des attributs du puzzle
		packAttributes = new PackAttributesReader().read("puzzles/" + packName, "pack.properties");
		
		if (packAttributes.isAvailable()) {
			// Préparation de la table des libellés
			Map<String, String> labels = new HashMap<String, String>();
			for (PuzzleInfos infos : packAttributes.getAllPuzzleInfos()) {
				labels.put(infos.getName(), infos.getLabel());
			}
		}
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		graph.dispose();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Mise à jour des acteurs
        stage.act(Gdx.graphics.getDeltaTime());
        
        // Dessin des lignes du graphe
        graph.drawEdges();
        
        // Dessin des autres composants du stage (label, boutons, noeuds...)
        stage.draw();     
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(480, 800, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
        loadPack();
        createUI();
        Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}
}
