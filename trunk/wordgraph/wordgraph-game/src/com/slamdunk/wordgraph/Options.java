package com.slamdunk.wordgraph;

import com.slamdunk.utils.PropertiesManager;

public class Options {
	public static String langCode = PropertiesManager.asString("config", "language", "fr");
}
