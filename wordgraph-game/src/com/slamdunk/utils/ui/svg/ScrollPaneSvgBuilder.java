package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScrollPaneSvgBuilder extends UISvgBuilder {
	
	@Override
	protected ScrollPane createEmpty(Skin skin, String style) {
		return new ScrollPane(null);
	}
}
