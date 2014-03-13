package com.slamdunk.wordgraph.puzzle;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Le temps géré est en secondes
 */
public class Chronometer {
	private float updateTime;
	private float elapsedTime = 0;
	private float timeSinceLastLabelUpdate;
	private Label label;
	private boolean running;
	private String formatString;
	
	public Chronometer() {
		this(0f);
	}
	
	public Chronometer(float initialTime) {
		this.elapsedTime = initialTime;
	}
	
	public String getFormatString() {
		return formatString;
	}

	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	/**
	 * 
	 * @param label
	 * @param updateTime En secondes
	 */
	public void setLabel(Label label, float updateTime) {
		this.updateTime = updateTime;
		timeSinceLastLabelUpdate = timeSinceLastLabelUpdate % updateTime;
		this.label = label;
		formatString = label.getText().toString();
		updateLabel();
	}

	public void update(float deltaTime) {
		if (!running) {
			return;
		}
		
	    elapsedTime += deltaTime;
	    timeSinceLastLabelUpdate += deltaTime;
	    if (timeSinceLastLabelUpdate >= updateTime) {
	    	// Appel du listner
    		updateLabel();

	        // Reset timer (not set to 0)
    		timeSinceLastLabelUpdate -= updateTime;
	    }
	}
	
	public void updateLabel() {
		if (label == null) {
			return;
		}
		int minutes = (int)(elapsedTime / 60);
		int seconds = (int)(elapsedTime % 60);
		label.setText(String.format(formatString, minutes, seconds));
	}

	public float getTime() {
		return elapsedTime;
	}

	public void start() {
		running = true;
	}
	
	public void stop() {
		running = false;
	}
}
