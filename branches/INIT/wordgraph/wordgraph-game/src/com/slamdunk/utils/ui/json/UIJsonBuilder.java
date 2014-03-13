package com.slamdunk.utils.ui.json;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Crée un actor à partir de données JSON.
 * Doit être stateless !!!
 */
public abstract class UIJsonBuilder {
	protected JsonValue actorDescription;
	protected JsonValue values;
	
	public JsonValue getWidgetDescription() {
		return actorDescription;
	}

	public void setWidgetDescription(JsonValue widgetDescription) {
		this.actorDescription = widgetDescription;
	}

	public JsonValue getValues() {
		return values;
	}

	public void setValues(JsonValue values) {
		this.values = values;
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
		if (hasProperty("style")) {
			style = actorDescription.getString("style");
		}
		Actor actor = createEmpty(skin, style);
		
		// Gère la propriété name
		parseName(actor);
		
		// Gère la propriété x
		if (!parseX(actor)) {
			parseXKey(actor);
		}
		
		
		// Gère la propriété y
		parseYKey(actor);
		parseY(actor);
		
		// Gère la propriété w
		parseWKey(actor);
		parseW(actor);
		
		// Gère la propriété h
		parseHKey(actor);
		parseH(actor);
		
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
		actor.setName(actorDescription.getString("name"));
		return true;
	}
	
	/**
	 * 
	 * @param actor
	 * @return true si la propriété a été trouvée et lue
	 */
	protected boolean parseX(Actor actor) {
		if (hasProperty("x")) {
			actor.setX(actorDescription.getFloat("x"));
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
		if (hasProperty("x-key")) {
			String key = actorDescription.getString("x-key");
			actor.setX(values.getFloat(key));
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
		if (hasProperty("y")) {
			actor.setY(actorDescription.getFloat("y"));
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
		if (hasProperty("y-key")) {
			String key = actorDescription.getString("y-key");
			actor.setY(values.getFloat(key));
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
		if (hasProperty("w")) {
			actor.setWidth(actorDescription.getFloat("w"));
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
		if (hasProperty("w-key")) {
			String key = actorDescription.getString("w-key");
			actor.setWidth(values.getFloat(key));
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
		if (hasProperty("h")) {
			actor.setHeight(actorDescription.getFloat("h"));
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
		if (hasProperty("h-key")) {
			String key = actorDescription.getString("h-key");
			actor.setHeight(values.getFloat(key));
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
		if (hasProperty("visible")) {
			actor.setVisible(actorDescription.getBoolean("visible"));
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
		if (hasProperty("visible-key")) {
			String key = actorDescription.getString("visible-key");
			actor.setVisible(values.getBoolean(key));
			return true;
		}
		return false;
	}

	protected boolean hasProperty(String property) {
		return actorDescription.get(property) != null;
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
		JsonValue value = values.get(key);
		if (value.isString()) {
			return value.asString();
		}
		if (value.isObject()) {
			return value.getString(discriminant);
		}
		return null;
	}
}
