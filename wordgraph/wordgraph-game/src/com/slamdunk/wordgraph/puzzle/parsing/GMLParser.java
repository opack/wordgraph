package com.slamdunk.wordgraph.puzzle.parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Lit un fichier GML et charge les noeuds et liens mentionn�s
 * L'algorithme utilis� est le suivant :
 * 		1. Prendre le prochain (�viter les espaces) mot : c'est la cl�
 * 		2. Prendre le prochain mot : c'est la valeur
 * 		3a. Si la valeur est un [, utiliser le reader correspondant � la cl�
 * 		3b.	Sinon, attribuer la valeur � l'�l�ment ad�quat
 */
public class GMLParser {
	private final static Pattern PATTERN = Pattern.compile("([^\\s]+)\\s+");
	
	public GraphObject parse(String filename) {
		// Ouverture du fichier
		FileHandle file = Gdx.files.internal(filename);
		
		// R�cup�ration des parties du GML
		List<String> parts = extractParts(file.readString());
		
		// Analyse des mots lus
		Iterator<String> partsIterator = parts.iterator();
		return parseGraphObject(partsIterator);
	}

	private List<String> extractParts(String fileContent) {
		List<String> parts = new ArrayList<String>();
		Matcher matcher = PATTERN.matcher(fileContent);
		while (matcher.find()) {
			parts.add(matcher.group(1));
		}
		return parts;
	}

	private GraphObject parseGraphObject(Iterator<String> partsIterator) {
		GraphObject object = new GraphObject();
		String key = null;
		String value = null;
		while (partsIterator.hasNext()) {
			key = partsIterator.next();
			
			// Si la cl� est une fin d'objet, on retourne le r�sultat
			if ("]".equals(key)) {
				return object;
			}
			
			value = partsIterator.next();
			// Si la valeur est un d�but d'objet, on lit ce qui suit
			// dans un nouvel objet
			if ("[".equals(value)) {
				object.addAttribute(key, parseGraphObject(partsIterator));
			} else {
				// Sinon, on est en pr�sence d'attributs
				object.addAttribute(key, value);
			}
		}
		return object;
	}
}
