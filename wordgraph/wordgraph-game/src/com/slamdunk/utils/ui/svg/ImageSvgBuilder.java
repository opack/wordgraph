package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ImageSvgBuilder extends UISvgBuilder {
	
	@Override
	protected Image createEmpty(Skin skin, String style) {
		return new Image();
	}
	
	@Override
	public Actor build(Skin skin) {
		// G�re les propri�t�s basiques du widget
		Image image = (Image)super.build(skin);
		
		// G�re la propri�t� image
		parseImage(skin, image);
		
		return image;
	}

	private void parseImage(Skin skin, Image image) {
		if (hasAttribute("ui.image")) {
			String atlasRegionName = actorDescription.getAttribute("ui.image");
			TextureRegion region = skin.getAtlas().findRegion(atlasRegionName);
			if (region != null) {
				image.setDrawable(new TextureRegionDrawable(region));
			}
		}
	}
}
