package com.slamdunk.wordgraph.pack;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.TextButtonDecorator;
import com.slamdunk.wordgraph.WordGraphGame;

public class PackButtonDecorator implements TextButtonDecorator {

	private final WordGraphGame game;
	private final PackAttributes packAttributes;
	private final ClickListener nodeListener;
	
	public PackButtonDecorator(PackAttributes packAttributes, WordGraphGame game) {
		this.game = game;
		this.packAttributes = packAttributes;
		
		// Listener appelé lors d'un clic sur un noeud du graphe
		nodeListener = new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TextButton btn = (TextButton)event.getListenerActor();
				selectPuzzle(btn);
			}
		};
	}
	
	private void selectPuzzle(TextButton button) {
		// Récupère le puzzle sélectionné
		String selected = button.getName().toString();
		
		// Affiche ce puzzle
		PuzzleInfos infos = packAttributes.getPuzzleInfos(selected);
		if (infos != null && infos.isAvailable()) {
			game.showPuzzleScreen(packAttributes.getName(), infos.getName());
		}
	}

	@Override
	public void decorate(String key, TextButton button) {
		PuzzleInfos infos = packAttributes.getPuzzleInfos(key);
		if (infos != null) {
			button.setName(infos.getName());
			button.setText(infos.getLabel());
			//DBG Remplacer puzzle-letter par un style 9-patch
			button.setStyle(Assets.uiSkin.get("puzzle-letter", TextButtonStyle.class));
			if (infos.isAvailable()) {
				button.addListener(nodeListener);
				//DBG
//				switch (infos.getDifficulty()) {
//					case 3:
//						button.setStyle(Assets.uiSkin.get("red", TextButtonStyle.class));
//						break;
//					case 2:
//						button.setStyle(Assets.uiSkin.get("yellow", TextButtonStyle.class));
//						break;
//					case 1:
//					default:
//						button.setStyle(Assets.uiSkin.get("green", TextButtonStyle.class));
//				}
			} else {
				button.setDisabled(true);
				// DBG Mettre un style gris pour un bouton désactivé
				//DBGbutton.setStyle(Assets.uiSkin.get("grey", TextButtonStyle.class));
			}
		}
	}

	@Override
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
