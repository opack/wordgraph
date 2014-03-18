package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class TextButtonSvgBuilder extends UISvgBuilder {
	private String language;
	
	public TextButtonSvgBuilder(String language) {
		this.language = language;
	}
	
	@Override
	protected TextButton createEmpty(Skin skin, String style) {
		if (style == null) {
			return new TextButton("", skin);
		}
		return new TextButton("", skin, style);
	}
	
	@Override
	public Actor build(Skin skin) {
		// Gère les propriétés basiques du widget
		TextButton button = (TextButton)super.build(skin);
		
		// Gère les propriétés spécifiques du bouton
		parseTextKey(button);
		parseText(button);
		
		return button;
	}

	protected void parseText(TextButton button) {
		if (hasAttribute("ui.text")) {
			button.setText(actorDescription.getAttribute("ui.text"));
		}
	}
	
	protected void parseTextKey(TextButton button) {
		if (hasAttribute("ui.text-key")) {
			String key = actorDescription.getAttribute("ui.text-key");
			button.setText(getValueString(key, language));
		}
	}
}
