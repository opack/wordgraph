package com.slamdunk.wordgraph.messagebox;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MessageBox extends Window {
	private static final int PAD_UNDER_MESSAGE = 20;
	
	static public class MessageBoxStyle {
		public TextButtonStyle leftButtonStyle;
		public TextButtonStyle rightButtonStyle;
		public LabelStyle messageStyle;
		public WindowStyle windowStyle;
		public float buttonWidth;
		public float buttonHeight;
	}
	
	private TextButton leftButton;
	private TextButton rightButton;
	private Label message;
	
	public MessageBox(String title, String messageText, String buttonText, MessageBoxStyle style) {
		super(title, style.windowStyle);
		//DBGsetBackground(new NinePatchDrawable(style.ninePatch));
		padTop(30);
		
		message = new Label(messageText, style.messageStyle);
		
		leftButton = new TextButton(buttonText, style.leftButtonStyle);
		leftButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		});
		
		add(message).expandX().padBottom(PAD_UNDER_MESSAGE);
		row();
		add(leftButton).size(style.buttonWidth, style.buttonHeight);
		
		pack();
		
		setVisible(false);
	}
	
	public MessageBox(String title, String messageText, String leftButtonText, String rightButtonText, MessageBoxStyle style) {
		super(title, style.windowStyle);
		padTop(30);
		
		message = new Label(messageText, style.messageStyle);
		
		ClickListener closeListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setVisible(false);
			}
		};
		leftButton = new TextButton(leftButtonText, style.leftButtonStyle);
		leftButton.addListener(closeListener);
		
		rightButton = new TextButton(rightButtonText, style.rightButtonStyle);
		rightButton.addListener(closeListener);
		
		add(message).colspan(2).expandX().padBottom(PAD_UNDER_MESSAGE);
		row();
		add(leftButton).size(style.buttonWidth, style.buttonHeight).left().padRight(20);
		add(rightButton).size(style.buttonWidth, style.buttonHeight).right();
		
		pack();
		
		setVisible(false);
	}

	public void addLeftButtonListener(EventListener listener) {
		if (listener != null) {
			leftButton.addListener(listener);
		}
	}
	
	public void addLeftButtonListeners(List<EventListener> listeners) {
		if (listeners != null) {
			for (EventListener listener : listeners) {
				leftButton.addListener(listener);
			}
		}
	}
	
	public void addRightButtonListener(EventListener listener) {
		if (listener != null) {
			rightButton.addListener(listener);
		}
	}
	
	public void addRightButtonListeners(List<EventListener> listeners) {
		if (listeners != null) {
			for (EventListener listener : listeners) {
				rightButton.addListener(listener);
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (isVisible()) {
			super.draw(batch, parentAlpha);
		}
	}

	public void hide() {
		setVisible(false);
	}

	public void setMessage(String text) {
		message.setText(text);
		pack();
	}

	public void setStyle(MessageBoxStyle style) {
		if (style == null)
			throw new IllegalArgumentException("style cannot be null.");
		
		message.setStyle(style.messageStyle);
		leftButton.setStyle(style.leftButtonStyle);
		
		invalidateHierarchy();
	}
	
	public void show() {
		// On s'assure que la box sera au premier-plan
		setZIndex(getParent().getChildren().size + 1);
		setVisible(true);
	}
}