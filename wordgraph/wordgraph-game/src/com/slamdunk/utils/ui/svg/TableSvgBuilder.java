package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TableSvgBuilder extends UISvgBuilder {
	
	@Override
	protected Table createEmpty(Skin skin, String style) {
		return new Table(skin);
	}
	
	@Override
	public Actor build(Skin skin) {
		// Gère les propriétés basiques du widget
		Table table = (Table)super.build(skin);
		
		// Gère la propriété image
		parseBackground(skin, table);
		
		return table;
	}
	
	private void parseBackground(Skin skin, Table table) {
		if (hasAttribute("ui.background")) {
			String atlasRegionName = actorDescription.getAttribute("ui.background");
			NinePatch patch = skin.getPatch(atlasRegionName);
			if (patch != null) {
				table.setBackground(new NinePatchDrawable(patch));
			} else {
				// Pas de 9-Patch, on tente de trouver une simple image
				TextureRegion region = skin.getRegion(atlasRegionName);
				if (region != null) {
					table.setBackground(new TextureRegionDrawable(region));
				}
			}
		}
	}
}
