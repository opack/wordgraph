package com.slamdunk.wordgraph.puzzle;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

public class PuzzleButtonDecorator {

	private TextButtonStyle normalStyle;
	private TextButtonStyle selectedStyle;
	private TextButtonStyle jokerStyle;
	
	public PuzzleButtonDecorator(final Skin skin) {
		normalStyle = skin.get("puzzle-letter", TextButtonStyle.class);
		selectedStyle = skin.get("puzzle-letter-selected", TextButtonStyle.class);
		jokerStyle = skin.get("puzzle-letter-highlighted", TextButtonStyle.class);
	}

	public TextButtonStyle getNormalStyle() {
		return normalStyle;
	}

	public TextButtonStyle getSelectedStyle() {
		return selectedStyle;
	}

	public TextButtonStyle getJokerStyle() {
		return jokerStyle;
	}

	/**
	 * Remet le style normal sur tous les boutons de lettre
	 * @param highlighted
	 */
	public void setNormalStyleOnAllNodes(PuzzleGraph graph) {
		for (PuzzleNode node : graph.getNodes()) {
			setNormalStyle(node.getButton());
		}
	}

	/**
	 * Applique le style joker au bouton indiqué
	 * @param button
	 */
	public void setJokerStyle(TextButton button) {
		if (button != null) {
			button.setStyle(jokerStyle);
		}
		
	}

	/**
	 * Applique le style normal au bouton indiqué, le rend
	 * de nouveau enabled et le décoche
	 * @param button
	 */
	public void setNormalStyle(TextButton button) {
		if (button != null) {
			button.setStyle(normalStyle);
			button.setChecked(false);
			button.setDisabled(false);
		}
	}
	
	/**
	 * Applique le style selected ou normal au bouton indiqué
	 * @param button
	 * @param contains
	 */
	public void setSelectedStyle(TextButton button) {
		if (button != null) {
			button.setStyle(selectedStyle);
		}
	}
}
