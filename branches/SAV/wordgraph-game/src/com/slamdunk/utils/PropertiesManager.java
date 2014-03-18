package com.slamdunk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class PropertiesManager {
	
	private static Map<String, Properties> propertiesMap;
	
	static {
		propertiesMap = new HashMap<String, Properties>();
	}
	
	public static void init(String propertiesKey) {
		Properties properties = new Properties();
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
	
	public static boolean asBoolean(String propertiesKey, String name, boolean fallback) {
		Properties properties = propertiesMap.get(propertiesKey);
		if (properties == null) {
			return fallback;
		}
		String v = properties.getProperty(name);
		if (v == null) return fallback;
		return Boolean.parseBoolean(v);
	}

	public static float asFloat(String propertiesKey, String name, float fallback) {
		Properties properties = propertiesMap.get(propertiesKey);
		if (properties == null) {
			return fallback;
		}
		String v = properties.getProperty(name);
		if (v == null) return fallback;
		return Float.parseFloat(v);
	}

	public static int asInt(String propertiesKey, String name, int fallback) {
		Properties properties = propertiesMap.get(propertiesKey);
		if (properties == null) {
			return fallback;
		}
		String v = properties.getProperty(name);
		if (v == null) return fallback;
		return Integer.parseInt(v);
	}

	public static String asString(String propertiesKey, String name, String fallback) {
		Properties properties = propertiesMap.get(propertiesKey);
		if (properties == null) {
			return fallback;
		}
		String v = properties.getProperty(name);
		if (v == null) return fallback;
		return v;
	}

	private PropertiesManager () {
	}
}
