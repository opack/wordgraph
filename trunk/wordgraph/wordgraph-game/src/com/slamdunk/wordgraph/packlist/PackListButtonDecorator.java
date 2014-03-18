package com.slamdunk.wordgraph.packlist;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.TextButtonDecorator;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.pack.PackAttributes;
import com.slamdunk.wordgraph.pack.parsing.PackAttributesReader;

public class PackListButtonDecorator implements TextButtonDecorator {

	private final WordGraphGame game;
	private final EventListener buttonListener;
	
	public PackListButtonDecorator(WordGraphGame game) {
		this.game = game;
		
		// Listener appelé lors d'un clic sur un noeud du graphe
		buttonListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
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
		} else {
			button.setDisabled(true);
		}
		
		// Paramètre le libellé
		Label label = button.getLabel();
		label.setAlignment(Align.center);
		label.setWrap(true);
	}
	
	public TextButtonStyle getDefaultStyle() {
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
