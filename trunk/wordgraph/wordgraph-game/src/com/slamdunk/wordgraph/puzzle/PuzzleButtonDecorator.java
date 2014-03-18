package com.slamdunk.wordgraph.puzzle;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.slamdunk.wordgraph.TextButtonDecorator;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

public class PuzzleButtonDecorator implements TextButtonDecorator {

	private final EventListener nodeListener;
	private Skin skin;
	
	public PuzzleButtonDecorator(final PuzzleScreen screen, final Skin skin) {
		this.skin = skin;
		
		// Listener appel� lors d'un clic sur un noeud du graphe
		nodeListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GraphNode btn = (GraphNode)event.getListenerActor();
				screen.selectLetter(btn);
			}
		};
	}

	@Override
	public void decorate(String key, TextButton button) {
		button.setName(key);
		button.addListener(nodeListener);
		button.setStyle(getDefaultStyle());
	}

	@Override
	public TextButtonStyle getDefaultStyle() {
		return skin.get("puzzle-letter", TextButtonStyle.class);
	}
	
	@Override
	public TextButtonStyle getSelectedStyle() {
		return skin.get("puzzle-letter-selected", TextButtonStyle.class);
	}
	
	@Override
	public TextButtonStyle getHighlightedStyle() {
		return skin.get("puzzle-letter-highlighted", TextButtonStyle.class);
	}
}
