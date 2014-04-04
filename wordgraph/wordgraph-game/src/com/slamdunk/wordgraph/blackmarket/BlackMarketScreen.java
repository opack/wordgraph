package com.slamdunk.wordgraph.blackmarket;

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
import com.slamdunk.utils.PropertiesManager;
import com.slamdunk.utils.ui.ButtonClickListener;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.Options;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.grid.Grid;

public class BlackMarketScreen implements Screen {
	
	private WordGraphGame game;
	private Stage stage;
	
	private Grid grid;
	private PuzzleAttributes puzzleAttributes;
	
	private TextButton validateButton;
	
	private BlackMarketItem selectedItem;
	
	public BlackMarketScreen(WordGraphGame game) {
		this.game = game;
		PropertiesManager.init("blackmarket");
		stage = new Stage();
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
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
		// R�cup�ration de la skin � appliquer
		Skin skin = puzzleAttributes.getSkin();
		if (skin == null) {
			skin = Assets.uiSkin;
		}
		// Cr�ation des composants depuis le layout d�finit dans le SVG
		SvgUICreator creator = new SvgUICreator(skin);
		creator.load("layouts/blackmarket.ui.svg");
		
		validateButton = (TextButton)creator.getActor("validate");
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setVisible(background.getDrawable() != null);
		
		// Somme dans le portefeuille
		Label walletLabel = (Label)creator.getActor("wallet");
		walletLabel.setText("000");
		
		// Cr�ation d'un groupe de boutons de bonus pour que seul un bouton soit s�lectionn� � la fois
		ButtonGroup itemsButtons = new ButtonGroup();
		for (BlackMarketItem item : BlackMarketItem.values()) {
			TextButton button = (TextButton)creator.getActor(item.name());
			itemsButtons.add(button);
			
		}		
		itemsButtons.uncheckAll();
		
		// Affectation des listeners
		creator.getActor("back").addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
        		onBack();
			}
        });
		final Label nameLabel = (Label)creator.getActor("name");
		final Label descriptionLabel = (Label)creator.getActor("description");
		final Label priceLabel = (Label)creator.getActor("price");
		final EventListener itemSelectionListener = new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				// Choisit le joker s�lectionn�
				selectedItem = BlackMarketItem.valueOf(button.getName());
				
				// Change les libell�s
				String nameKey = selectedItem.name() + ".name." + Options.langCode;
				String nameText = PropertiesManager.getString("blackmarket", nameKey, "");
				nameLabel.setText(nameText);
				
				String descriptionKey = selectedItem.name() + ".description." + Options.langCode;
				String descriptionText = PropertiesManager.getString("blackmarket", descriptionKey, "");
				descriptionLabel.setText(descriptionText);
				
				String priceKey = selectedItem.name() + ".price." + Options.langCode;
				String priceText = PropertiesManager.getString("blackmarket", priceKey, "");
				priceLabel.setText(priceText);
				
				// Active si possible le bouton de validation
				activateValidation();
			}
		};
		for (Button button : itemsButtons.getButtons()) {
			button.addListener(itemSelectionListener);
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
		selectedItem = null;
		activateValidation();
	}
	
	private void activateValidation() {
		validateButton.setDisabled(selectedItem == null);
	}
	
	private void onValidate() {
		if (selectedItem != null) {
			selectedItem.use(grid);
			onBack();
		}
	}
}
