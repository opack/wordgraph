package com.slamdunk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Gestionnaire de fichiers de propriétés, capable de stocker plusieurs
 * fichiers
 */
public class PropertiesManager {
	
	private static Map<String, PropertiesEx> propertiesMap;
	
	static {
		propertiesMap = new HashMap<String, PropertiesEx>();
	}
	
	public static void init(String propertiesKey) {
		PropertiesEx properties = new PropertiesEx();
		FileHandle fh = Gdx.files.internal("properties/" + propertiesKey + ".properties");
		InputStream inStream = fh.read();
		try {
			properties.load(inStream);
			inStream.close();
			propertiesMap.put(propertiesKey, properties);
		} catch (IOException e) {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException ex) {
				}
			}
		}
	}
	
	public static boolean getBoolean(String propertiesKey, String name, boolean fallback) {
		PropertiesEx properties = propertiesMap.get(propertiesKey);
		if (properties == null) {
			return fallback;
		}
		return properties.getBooleanProperty(name, fallback);
	}

	public static float getFloat(String propertiesKey, String name, float fallback) {
		PropertiesEx properties = propertiesMap.get(propertiesKey);
		if (properties == null) {
			return fallback;
		}
		return properties.getFloatProperty(name, fallback);
	}

	public static int getInteger(String propertiesKey, String name, int fallback) {
		PropertiesEx properties = propertiesMap.get(propertiesKey);
		if (properties == null) {
			return fallback;
		}
		return properties.getIntegerProperty(name, fallback);
	}

	public static String getString(String propertiesKey, String name, String fallback) {
		PropertiesEx properties = propertiesMap.get(propertiesKey);
		if (properties == null) {
			return fallback;
		}
		return properties.getStringProperty(name, fallback);
	}

	private PropertiesManager () {
	}
}
