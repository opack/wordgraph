package com.slamdunk.wordgraph.messagebox;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.wordgraph.messagebox.MessageBox.MessageBoxStyle;

public class MessageBoxBuilder {
	private Stage stage;
	private MessageBoxStyle style;
	private boolean modal;
	private List<EventListener> okListeners;
	private List<EventListener> cancelListeners;
	
	public MessageBoxBuilder() {
		modal = true;
		okListeners = new ArrayList<EventListener>();
		cancelListeners = new ArrayList<EventListener>();
	}
	
	public MessageBoxBuilder(Stage stage) {
		this();
		this.stage = stage;
	}

	public static void centerBox(MessageBox msg, Stage stage) {
		msg.setPosition((stage.getCamera().viewportWidth - msg.getWidth()) / 2, (stage.getCamera().viewportHeight - msg.getHeight()) / 2);
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setStyle(MessageBoxStyle style) {
		this.style = style;
	}
	

	public MessageBox createMessage(String title, String message, String buttonLabel){ //float centerX, float centerY) {
		MessageBox msg = new MessageBox(title, message, buttonLabel, style);
		msg.addLeftButtonListeners(okListeners);
		
		centerBox(msg, stage);
		msg.setModal(modal);
		stage.addActor(msg);
		
		return msg;
	}
	
	public MessageBox createConfirm(String title, String message, String okButtonLabel, String cancelButtonLabel){
		MessageBox msg = new MessageBox(title, message, okButtonLabel, cancelButtonLabel, style);
		msg.addLeftButtonListeners(okListeners);
		msg.addRightButtonListeners(cancelListeners);
		
		centerBox(msg, stage);
		msg.setModal(modal);
		stage.addActor(msg);
		
		return msg;
	}

	/**
	 * @param modal défaut=true
	 */
	public void setModal(boolean modal) {
		this.modal = modal;
	}

	public void addLeftButtonListener(EventListener listener) {
		okListeners.add(listener);
	}
	

	public void addRightButtonListener(EventListener listener) {
		cancelListeners.add(listener);
	}
}
