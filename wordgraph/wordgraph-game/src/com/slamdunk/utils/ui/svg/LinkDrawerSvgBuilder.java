package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.slamdunk.wordgraph.puzzle.graph.LinkDrawer;

public class LinkDrawerSvgBuilder extends UISvgBuilder {
	
	@Override
	protected LinkDrawer createEmpty(Skin skin, String style) {
		return new LinkDrawer();
	}
	
	@Override
	public LinkDrawer build(Skin skin) {
		// Gère les propriétés basiques du widget
		LinkDrawer linkDrawer = (LinkDrawer)super.build(skin);
		
		// Gère la propriété image
		parseImage(skin, linkDrawer);
		
		return linkDrawer;
	}
	
	private void parseImage(Skin skin, LinkDrawer linkDrawer) {
		if (hasAttribute("ui.image")) {
			String atlasRegionName = actorDescription.getAttribute("ui.image");
			TextureRegion region = skin.getAtlas().findRegion(atlasRegionName);
			if (region != null) {
				linkDrawer.setTextureRegion(region);
			}
		}
	}
}
