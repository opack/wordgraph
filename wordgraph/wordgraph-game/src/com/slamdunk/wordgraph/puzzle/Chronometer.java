package com.slamdunk.wordgraph.puzzle;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Le temps géré est en secondes
 */
public class Chronometer {
	private float elapsedTime = 0;
	private float standardUpdateInterval;
	
	private float pausedTime = 0;
	private float pausedUpdateInterval;
	
	private float timeSinceLastUpdate;

	private boolean running;
	private boolean pausing;
	
	private Label label;
	private String standardFormat;
	private String pauseFormat;
	
	public Chronometer() {
		this(0f);
	}
	
	public Chronometer(float initialTime) {
		this.elapsedTime = initialTime;
	}
	
	public void setFormatStrings(String standardFormat, String pauseFormat) {
		this.standardFormat = standardFormat;
		this.pauseFormat = pauseFormat;
	}

	/**
	 * 
	 * @param label
	 * @param updateTime En secondes
	 */
	public void setLabel(Label label, float updateTime) {
		standardUpdateInterval = updateTime;
		timeSinceLastUpdate %= updateTime;
		this.label = label;
	}

	public void update(float deltaTime) {
		if (!running) {
			return;
		}
		
		// Mise à jour du compteur
		float updateInterval = 0.1f;
		if (pausing) {
			updateInterval = pausedUpdateInterval;
			pausedTime -= deltaTime;
			if (pausedTime <= 0) {
				pausing = false;
				// Force un rafraîchissement
				timeSinceLastUpdate = standardUpdateInterval;
			}
		} else {
			updateInterval = standardUpdateInterval;
			elapsedTime += deltaTime;
		}
		
		// Mise à jour du libellé
	    timeSinceLastUpdate += deltaTime;
	    if (timeSinceLastUpdate >= updateInterval) {
	    	// Appel du listner
    		updateLabel();

	        // Reset timer (not set to 0)
    		timeSinceLastUpdate -= updateInterval;
	    }
	}
	
	public void updateLabel() {
		if (label == null) {
			return;
		}
		if (pausing) {
			label.setText(String.format(pauseFormat, pausedTime));
		} else {
			int minutes = (int)(elapsedTime / 60);
			int seconds = (int)(elapsedTime % 60);
			label.setText(String.format(standardFormat, minutes, seconds));
		}
	}

	public float getTime() {
		return elapsedTime;
	}

	public void start() {
		running = true;
		pausing = false;
	}
	
	public void stop() {
		running = false;
		pausing = false;
	}

	/**
	 * Met le chronomètre en pause pendant le temps indiqué.
	 * Le chronomètre affiche le décompte pendant la pause.
	 * @param time
	 */
	public void pause(float time) {
		pausing = true;
		pausedTime = time;
		// Force un rafraîchissement
		timeSinceLastUpdate = pausedUpdateInterval;
	}
}
