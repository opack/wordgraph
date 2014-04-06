package com.slamdunk.wordgraph.effect;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.wordgraph.puzzle.PuzzleScreen;

/**
 * Fait clignoter le label pendant quelques secondes
 */
public class HighlightClueEffect implements VisualEffect {
	/**
	 * Durée de l'effet, en secondes
	 */
	private static final float HIGHLIGHT_TIME = 5.0f;
	/**
	 * Interval de clignottement, en secondes
	 */
	private static final float FLASHING_INTERVAL = 0.4f;
	
	private Image flashingImage;
	private float elapsed;
	private float flashTime;
	private Actor actor;
	
	public void setHighlightActor(Actor actor) {
		this.actor = actor;
	}

	@Override
	public void init(PuzzleScreen puzzleScreen) {
		NinePatch highlightPatch = puzzleScreen.getPuzzleAttributes().getSkin().getPatch("highlight_clue");
		flashingImage = new Image(highlightPatch);
		flashingImage.setBounds(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
		
		actor.getParent().addActor(flashingImage);
	}
	
	@Override
	public boolean update(SpriteBatch batch, float delta) {
		flashTime += delta;
		if (flashTime >= FLASHING_INTERVAL) {
			flashingImage.setVisible(!flashingImage.isVisible());
			flashTime -= FLASHING_INTERVAL;
		}
		
		elapsed += delta;
		if (elapsed >= HIGHLIGHT_TIME) {
			flashingImage.remove();
			return false;
		}
		return true;
	}
}
