package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Cr�e un actor � partir de donn�es SVG.
 * Doit �tre stateless !!!
 */
public abstract class UISvgBuilder {
	/**
	 * Hauteur de l'�cran. Cette information est importante car
	 * dans le SVG l'origine du syst�me orthonorm� est en haut �
	 * gauche alors qu'elle est en bas � gauche pour LibGDX.
	 * Il faudra donc "inverser" les coordonn�es Y.
	 */
	protected float screenHeight;
	protected Element actorDescription;
	protected Element globalValues;
	
	public float getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(float screenHeight) {
		this.screenHeight = screenHeight;
	}

	public Element getWidgetDescription() {
		return actorDescription;
	}

	public void setWidgetDescription(Element widgetDescription) {
		this.actorDescription = widgetDescription;
	}
	
	public Element getGlobalValues() {
		return globalValues;
	}

	public void setGlobalValues(Element globalValues) {
		this.globalValues = globalValues;
	}

	/**
	 * Construit l'objet
	 * @return
	 */
	public Actor build(Skin skin) {
		if (actorDescription == null) {
			throw new IllegalStateException("Call setWidgetDescription() first.");
		}
		
		// Construit un objet vierge
		String style = null;
		if (hasAttribute("ui.style")) {
			style = actorDescription.getAttribute("ui.style");
		}
		Actor actor = createEmpty(skin, style);
		
		// G�re la propri�t� name
		parseName(actor);
		
		// G�re la propri�t� w
		parseWKey(actor);
		parseW(actor);
		
		// G�re la propri�t� h
		parseHKey(actor);
		parseH(actor);
		
		// G�re la propri�t� x
		parseXKey(actor);
		parseX(actor);		
		
		// G�re la propri�t� y
		parseYKey(actor);
		parseY(actor);
		
		// G�re la propri�t� visible
		parseVisibleKey(actor);
		parseVisible(actor);
		
		return actor;
	}
	
	protected abstract Actor createEmpty(Skin skin, String style);
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseName(Actor actor) {
		actor.setName(actorDescription.getAttribute("id"));
		return true;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseX(Actor actor) {
		if (hasAttribute("x")) {
			actor.setX(actorDescription.getFloatAttribute("x"));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseXKey(Actor actor) {
		if (hasAttribute("ui.x-key")) {
			String key = actorDescription.getAttribute("ui.x-key");
			actor.setX(globalValues.getFloat(key));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseY(Actor actor) {
		if (hasAttribute("y")) {
			// Pour le SVG, le Y=0 est en haut, alors qu'il est en bas pour libGDX.
			// On doit donc "inverser" l'ordonn�e.
			actor.setY(screenHeight - actor.getHeight() - actorDescription.getFloatAttribute("y"));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseYKey(Actor actor) {
		if (hasAttribute("ui.y-key")) {
			String key = actorDescription.getAttribute("ui.y-key");
			// Pour le SVG, le Y=0 est en haut, alors qu'il est en bas pour libGDX.
			// On doit donc "inverser" l'ordonn�e.
			actor.setY(screenHeight - actor.getHeight() - globalValues.getFloat(key));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseW(Actor actor) {
		if (hasAttribute("width")) {
			actor.setWidth(actorDescription.getFloatAttribute("width"));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseWKey(Actor actor) {
		if (hasAttribute("ui.w-key")) {
			String key = actorDescription.getAttribute("ui.w-key");
			actor.setWidth(globalValues.getFloat(key));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseH(Actor actor) {
		if (hasAttribute("height")) {
			actor.setHeight(actorDescription.getFloatAttribute("height"));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseHKey(Actor actor) {
		if (hasAttribute("ui.h-key")) {
			String key = actorDescription.getAttribute("ui.h-key");
			actor.setHeight(globalValues.getFloat(key));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseVisible(Actor actor) {
		if (hasAttribute("ui.visible")) {
			actor.setVisible(actorDescription.getBooleanAttribute("ui.visible"));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propri�t� a �t� trouv�e et lue
	 */
	protected boolean parseVisibleKey(Actor actor) {
		if (hasAttribute("ui.visible-key")) {
			String key = actorDescription.getAttribute("ui.visible-key");
			actor.setVisible(globalValues.getBoolean(key));
			return true;
		}
		return false;
	}

	protected boolean hasAttribute(String attribute) {
		return actorDescription.getAttributes().containsKey(attribute);
	}
	
	/**
	 * Retourne la valeur de la table values associ�e � la cl� key.
	 * Si cette valeur est un objet, la valeur correspondant �
	 * la cl� discriminant est retourn�e.
	 * 
	 * @param key
	 * @param discriminant
	 * @return
	 */
	protected String getValueString(String key, String discriminant) {
		return globalValues.get(key + "." + discriminant);
	}
}
