package com.slamdunk.utils.ui.json;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScrollPaneJsonBuilder extends UIJsonBuilder {
	
	@Override
	protected ScrollPane createEmpty(Skin skin, String style) {
		return new ScrollPane(null);
	}
}
