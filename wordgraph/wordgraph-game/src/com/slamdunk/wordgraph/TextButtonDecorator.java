package com.slamdunk.wordgraph;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

/**
 * Modifie un TextButton à sa création. Utile pour ajouter un listener
 * ou modifier son libellé ou tooltip par exemple
 */
public interface TextButtonDecorator {
	void decorate(String key, TextButton button);

	TextButtonStyle getDefaultStyle();
	
	TextButtonStyle getSelectedStyle();
	
	TextButtonStyle getHighlightedStyle();
}
