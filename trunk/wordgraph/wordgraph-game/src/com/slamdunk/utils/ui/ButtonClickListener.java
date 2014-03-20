package com.slamdunk.utils.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Un simple listener qui ne fait rien si le bouton est désactivé
 * @author Yed
 *
 */
public abstract class ButtonClickListener extends ClickListener {
	@Override
	public void clicked(InputEvent event, float x, float y) {
		Button button = (Button)event.getListenerActor();
		if (button.isDisabled()) {
			return;
		}
		clicked(button);
	}
	
	/**
	 * Méthode appelée lorsque le bouton est cliqué alors qu'il est actif
	 * @param button
	 */
	public abstract void clicked(Button button);
}
