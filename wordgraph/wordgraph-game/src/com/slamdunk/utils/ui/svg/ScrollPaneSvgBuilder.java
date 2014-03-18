package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScrollPaneSvgBuilder extends UISvgBuilder {
	
	@Override
	protected ScrollPane createEmpty(Skin skin, String style) {
		return new ScrollPane(null);
	}
	
	@Override
	public Actor build(Skin skin) {
		// G�re les propri�t�s basiques du widget
		ScrollPane scrollPane = (ScrollPane)super.build(skin);
		
		// G�re la propri�t� image
		parseOverscrollX(scrollPane);
		
		return scrollPane;
	}

	private void parseOverscrollX(ScrollPane scrollPane) {
		if (hasAttribute("ui.overscroll")) {
			boolean overscroll = actorDescription.getBooleanAttribute("ui.overscroll");
			scrollPane.setOverscroll(overscroll, overscroll);
		}
	}
}
