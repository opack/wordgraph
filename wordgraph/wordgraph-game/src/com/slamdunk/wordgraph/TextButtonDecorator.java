package com.slamdunk.wordgraph;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

/**
 * Modifie un TextButton � sa cr�ation. Utile pour ajouter un listener
 * ou modifier son libell� ou tooltip par exemple
 */
public interface TextButtonDecorator {
	void decorate(String key, TextButton button);

	TextButtonStyle getDefaultStyle();
	
	TextButtonStyle getSelectedStyle();
	
	TextButtonStyle getHighlightedStyle();
}
