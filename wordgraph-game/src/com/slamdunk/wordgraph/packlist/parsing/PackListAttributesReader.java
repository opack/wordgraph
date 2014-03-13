package com.slamdunk.wordgraph.packlist.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class PackListAttributesReader {

private final static Pattern PATTERN = Pattern.compile("([^\\s]+)\\s+");
	
	public List<String> read(String filename) {
		// Ouverture du fichier
		FileHandle file = Gdx.files.internal(filename);
		
		// Récupération des packs
		return extractPacks(file.readString());
	}

	private List<String> extractPacks(String fileContent) {
		List<String> parts = new ArrayList<String>();
		Matcher matcher = PATTERN.matcher(fileContent);
		while (matcher.find()) {
			parts.add(matcher.group(1));
		}
		return parts;
	}
}
