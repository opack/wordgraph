package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelSvgBuilder extends UISvgBuilder {
	private String language;
	
	public LabelSvgBuilder(String language) {
		this.language = language;
	}
	
	@Override
	protected Label createEmpty(Skin skin, String style) {
		if (style == null) {
			return new Label("", skin);
		}
		return new Label("", skin, style);
	}
	
	@Override
	public Label build(Skin skin) {
		// Gère les propriétés basiques du widget
		Label label = (Label)super.build(skin);
		
		// Gère les propriétés spécifiques du Label
		parseTextKey(label);
		parseText(label);
		
		parseAlignKey(label);
		parseAlign(label);
		
		parseWrapKey(label);
		parseWrap(label);
		
		return label;
	}

	protected void parseText(Label label) {
		if (hasAttribute("ui.text")) {
			label.setText(actorDescription.getAttribute("ui.text"));
		}
	}
	
	protected void parseTextKey(Label label) {
		if (hasAttribute("ui.text-key")) {
			String key = actorDescription.getAttribute("ui.text-key");
			label.setText(getValueString(key, language));
		}
	}
	
	protected void parseAlign(Label label) {
		if (hasAttribute("ui.align")) {
			label.setAlignment(actorDescription.getIntAttribute("ui.align"));
		}
	}
	
	protected void parseAlignKey(Label label) {
		if (hasAttribute("ui.align-key")) {
			String key = actorDescription.getAttribute("ui.align-key");
			label.setAlignment(globalValues.getIntAttribute(key));
		}
	}
	
	protected void parseWrap(Label label) {
		if (hasAttribute("ui.wrap")) {
			label.setWrap(actorDescription.getBooleanAttribute("ui.wrap"));
		}
	}
	
	protected void parseWrapKey(Label label) {
		if (hasAttribute("ui.wrap-key")) {
			String key = actorDescription.getAttribute("ui.wrap-key");
			label.setWrap(globalValues.getBooleanAttribute(key));
		}
	}
}
