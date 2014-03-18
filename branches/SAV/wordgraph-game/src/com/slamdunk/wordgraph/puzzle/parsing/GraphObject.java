package com.slamdunk.wordgraph.puzzle.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphObject {
	private Map<String, Integer> integerAttributes;
	private Map<String, Float> floatAttributes;
	private Map<String, String> stringAttributes;
	private Map<String, List<GraphObject>> listObjectAttributes;
	private Map<String, GraphObject> singleObjectAttributes;
	
	public void addAttribute(String key, String value) {
		if (value.charAt(0) == '"') {
			if (stringAttributes == null) {
				stringAttributes = new HashMap<String, String>();
			}
			stringAttributes.put(key, value.substring(1, value.length() - 1));
		} else if (value.contains(".")) {
			if (floatAttributes == null) {
				floatAttributes = new HashMap<String, Float>();
			}
			floatAttributes.put(key, new Float(value));
		} else {
			if (integerAttributes == null) {
				integerAttributes = new HashMap<String, Integer>();
			}
			integerAttributes.put(key, new Integer(value));
		}
	}

	public void addAttribute(String key, GraphObject value) {
		// L'objet est-il déjà dans listObject ?
		if (listObjectAttributes != null && listObjectAttributes.containsKey(key)) {
			// Si oui, on l'ajoute à la liste
			List<GraphObject> list = listObjectAttributes.get(key);
			list.add(value);
		}
		// Si non, l'objet est-il déjà dans singleObject ?
		else if (singleObjectAttributes != null && singleObjectAttributes.containsKey(key)) {
			// Si oui, on le retire et on crée une liste dans listObject
			List<GraphObject> list = new ArrayList<GraphObject>();
			list.add(singleObjectAttributes.remove(key));
			list.add(value);
			if (listObjectAttributes == null) {
				listObjectAttributes = new HashMap<String, List<GraphObject>>();
			}
			listObjectAttributes.put(key, list);
		}
		// Si non, on ajoute l'objet dans singleObject car c'est pour l'instant la seule instance
		else {
			if (singleObjectAttributes == null) {
				singleObjectAttributes = new HashMap<String, GraphObject>();
			}
			singleObjectAttributes.put(key, value);
		}
	}
	
	public Integer getInt(String key) {
		if (integerAttributes == null) {
			return null;
		}
		return integerAttributes.get(key);
	}
	
	public Float getFloat(String key) {
		if (floatAttributes == null) {
			return null;
		}
		return floatAttributes.get(key);
	}
	
	public Float getFloat(String key, float defaultValue) {
		if (floatAttributes == null) {
			return defaultValue;
		}
		Float value = floatAttributes.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}
	
	public String getString(String key) {
		if (stringAttributes == null) {
			return null;
		}
		return stringAttributes.get(key);
	}
	
	public GraphObject getSingleObject(String key) {
		if (singleObjectAttributes == null) {
			return null;
		}
		return singleObjectAttributes.get(key);
	}
	
	public List<GraphObject> getListObject(String key) {
		if (listObjectAttributes == null) {
			return null;
		}
		return listObjectAttributes.get(key);
	}
}
