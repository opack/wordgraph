package com.slamdunk.utils.ui.svg;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Crée un actor à partir de données SVG.
 * Doit être stateless !!!
 */
public abstract class UISvgBuilder {
	/**
	 * Hauteur de l'écran. Cette information est importante car
	 * dans le SVG l'origine du système orthonormé est en haut à
	 * gauche alors qu'elle est en bas à gauche pour LibGDX.
	 * Il faudra donc "inverser" les coordonnées Y.
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
		
		// Gère la propriété name
		parseName(actor);
		
		// Gère la propriété w
		parseWKey(actor);
		parseW(actor);
		
		// Gère la propriété h
		parseHKey(actor);
		parseH(actor);
		
		// Gère la propriété x
		parseXKey(actor);
		parseX(actor);		
		
		// Gère la propriété y
		parseYKey(actor);
		parseY(actor);
		
		// Gère la propriété visible
		parseVisibleKey(actor);
		parseVisible(actor);
		
		return actor;
	}
	
	protected abstract Actor createEmpty(Skin skin, String style);
	
	/**
	 * 
	 * @param actor
	 * @return true si la propriété a été trouvée et lue
	 */
	protected boolean parseName(Actor actor) {
		actor.setName(actorDescription.getAttribute("id"));
		return true;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propriété a été trouvée et lue
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
	 * @return true si la propriété a été trouvée et lue
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
	 * @return true si la propriété a été trouvée et lue
	 */
	protected boolean parseY(Actor actor) {
		if (hasAttribute("y")) {
			// Pour le SVG, le Y=0 est en haut, alors qu'il est en bas pour libGDX.
			// On doit donc "inverser" l'ordonnée.
			actor.setY(screenHeight - actor.getHeight() - actorDescription.getFloatAttribute("y"));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propriété a été trouvée et lue
	 */
	protected boolean parseYKey(Actor actor) {
		if (hasAttribute("ui.y-key")) {
			String key = actorDescription.getAttribute("ui.y-key");
			// Pour le SVG, le Y=0 est en haut, alors qu'il est en bas pour libGDX.
			// On doit donc "inverser" l'ordonnée.
			actor.setY(screenHeight - actor.getHeight() - globalValues.getFloat(key));
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propriété a été trouvée et lue
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
	 * @return true si la propriété a été trouvée et lue
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
	 * @return true si la propriété a été trouvée et lue
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
	 * @return true si la propriété a été trouvée et lue
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
	 * @return true si la propriété a été trouvée et lue
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
	 * @return true si la propriété a été trouvée et lue
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
	 * Retourne la valeur de la table values associée à la clé key.
	 * Si cette valeur est un objet, la valeur correspondant à
	 * la clé discriminant est retournée.
	 * 
	 * @param key
	 * @param discriminant
	 * @return
	 */
	protected String getValueString(String key, String discriminant) {
		return globalValues.get(key + "." + discriminant);
	}
}
