package com.slamdunk.utils.ui.json;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelJsonBuilder extends UIJsonBuilder {
	private String language;
	
	public LabelJsonBuilder(String language) {
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
		if (hasProperty("text")) {
			label.setText(actorDescription.getString("text"));
		}
	}
	
	protected void parseTextKey(Label label) {
		if (hasProperty("text-key")) {
			String key = actorDescription.getString("text-key");
			label.setText(getValueString(key, language));
		}
	}
	
	protected void parseAlign(Label label) {
		if (hasProperty("align")) {
			label.setAlignment(actorDescription.getInt("align"));
		}
	}
	
	protected void parseAlignKey(Label label) {
		if (hasProperty("align-key")) {
			String key = actorDescription.getString("align-key");
			label.setAlignment(values.getInt(key));
		}
	}
	
	protected void parseWrap(Label label) {
		if (hasProperty("wrap")) {
			label.setWrap(actorDescription.getBoolean("wrap"));
		}
	}
	
	protected void parseWrapKey(Label label) {
		if (hasProperty("wrap-key")) {
			String key = actorDescription.getString("wrap-key");
			label.setWrap(values.getBoolean(key));
		}
	}
}
