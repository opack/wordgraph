package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.slamdunk.wordgraph.puzzle.LinkDrawer;

public class LinkDrawerSvgBuilder extends UISvgBuilder {
	
	@Override
	protected LinkDrawer createEmpty(Skin skin, String style) {
		return new LinkDrawer();
	}
	
	@Override
	public LinkDrawer build(Skin skin) {
		// Gère les propriétés basiques du widget
		LinkDrawer linkDrawer = (LinkDrawer)super.build(skin);
		
		// Gère les propriétés spéciales
		parseLinkImage(skin, linkDrawer);
		
		return linkDrawer;
	}
	
	private void parseLinkImage(Skin skin, LinkDrawer linkDrawer) {
		if (hasAttribute("ui.link-image")) {
			String atlasRegionName = actorDescription.getAttribute("ui.link-image");
			TextureRegion region = skin.getAtlas().findRegion(atlasRegionName);
			if (region != null) {
				linkDrawer.setLinkTexture(region);
			}
		}
	}
}
