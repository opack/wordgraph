package com.slamdunk.wordgraph.pack;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.TextButtonDecorator;
import com.slamdunk.wordgraph.WordGraphGame;

public class PackButtonDecorator implements TextButtonDecorator {

	private final WordGraphGame game;
	private final PackAttributes packAttributes;
	private final EventListener nodeListener;
	
	public PackButtonDecorator(PackAttributes packAttributes, WordGraphGame game) {
		this.game = game;
		this.packAttributes = packAttributes;
		
		// Listener appelé lors d'un clic sur un noeud du graphe
		nodeListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
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
			
			PuzzlePreferencesHelper puzzlePreferences = new PuzzlePreferencesHelper(packAttributes.getName(), infos.getName());
			if (puzzlePreferences.isFinished()) {
				// Le puzzle est terminé
				button.setStyle(Assets.uiSkin.get("pack-puzzle-finished", TextButtonStyle.class));
			} else if (puzzlePreferences.getElapsedTime() >= 1.0){
				// Le puzzle a été commencé
				button.setStyle(Assets.uiSkin.get("pack-puzzle-in_progress", TextButtonStyle.class));
			} else {
				// Le puzzle n'a pas encore été commencé
				button.setStyle(Assets.uiSkin.get("pack-puzzle-not_started", TextButtonStyle.class));
			}
			if (infos.isAvailable()) {
				button.addListener(nodeListener);
			} else {
				button.setDisabled(true);
			}
		}
	}

	@Override
	public TextButtonStyle getDefaultStyle() {
		return Assets.uiSkin.get("pack-puzzle-not_started", TextButtonStyle.class);
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
