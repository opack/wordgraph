package com.slamdunk.wordgraph.packlist;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.TextButtonDecorator;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.pack.PackAttributes;
import com.slamdunk.wordgraph.pack.parsing.PackAttributesReader;

public class PackListButtonDecorator implements TextButtonDecorator {

	private final WordGraphGame game;
	private final InputListener buttonListener;
	
	public PackListButtonDecorator(WordGraphGame game) {
		this.game = game;
		
		// Listener appelé lors d'un clic sur un noeud du graphe
		buttonListener = new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TextButton btn = (TextButton)event.getListenerActor();
				selectPack(btn);
			}
		};
	}
	
	private void selectPack(TextButton button) {
		// Récupère le puzzle sélectionné
		String selected = button.getName().toString();
		
		// Affiche ce puzzle
		game.showPackScreen(selected);
	}

	@Override
	public void decorate(String key, TextButton button) {
		PackAttributes packAttributes = new PackAttributes();
		
		// Charge les infos générales du pack
		PackAttributesReader packReader = new PackAttributesReader();
		packReader.loadFile("puzzles/" + key + "/pack.properties");
		packReader.loadPackInfos(packAttributes);
		
		// Paramètre le bouton
		button.setName(packAttributes.getName());
		button.setText(packAttributes.getLabel());
		//DBG Remplacer puzzle-letter par un style 9-patch
		button.setStyle(Assets.uiSkin.get("puzzle-letter", TextButtonStyle.class));
		if (packAttributes.isAvailable()) {
			button.addListener(buttonListener);
			//DBG
//			switch (packAttributes.getDifficulty()) {
//				case 3:
//					button.setStyle(Assets.uiSkin.get("red", TextButtonStyle.class));
//					break;
//				case 2:
//					button.setStyle(Assets.uiSkin.get("yellow", TextButtonStyle.class));
//					break;
//				case 1:
//				default:
//					button.setStyle(Assets.uiSkin.get("green", TextButtonStyle.class));
//			}
		} else {
			button.setDisabled(true);
			// DBG Mettre un style gris pour un bouton désactivé
			//DBGbutton.setStyle(Assets.uiSkin.get("grey", TextButtonStyle.class));
		}
		
		// Paramètre le libellé
		Label label = button.getLabel();
		label.setAlignment(Align.center);
		label.setWrap(true);
	}
	
	public TextButtonStyle getDefaultStyle() {
		//DBGreturn Assets.uiSkin.get("grey", TextButtonStyle.class);
		return Assets.uiSkin.get("puzzle-letter", TextButtonStyle.class);
	}

	@Override
	public TextButtonStyle getSelectedStyle() {
		return getDefaultStyle();
	}
	
	@Override
	public TextButtonStyle getHighlightedStyle() {
		return getDefaultStyle();
	}
}
