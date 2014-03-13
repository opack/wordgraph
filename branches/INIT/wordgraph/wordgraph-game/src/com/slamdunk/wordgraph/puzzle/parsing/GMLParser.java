package com.slamdunk.wordgraph.puzzle.parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Lit un fichier GML et charge les noeuds et liens mentionnés
 * L'algorithme utilisé est le suivant :
 * 		1. Prendre le prochain (éviter les espaces) mot : c'est la clé
 * 		2. Prendre le prochain mot : c'est la valeur
 * 		3a. Si la valeur est un [, utiliser le reader correspondant à la clé
 * 		3b.	Sinon, attribuer la valeur à l'élément adéquat
 */
public class GMLParser {
	private final static Pattern PATTERN = Pattern.compile("([^\\s]+)\\s+");
	
	public GraphObject parse(String filename) {
		// Ouverture du fichier
		FileHandle file = Gdx.files.internal(filename);
		
		// Récupération des parties du GML
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
			
			// Si la clé est une fin d'objet, on retourne le résultat
			if ("]".equals(key)) {
				return object;
			}
			
			value = partsIterator.next();
			// Si la valeur est un début d'objet, on lit ce qui suit
			// dans un nouvel objet
			if ("[".equals(value)) {
				object.addAttribute(key, parseGraphObject(partsIterator));
			} else {
				// Sinon, on est en présence d'attributs
				object.addAttribute(key, value);
			}
		}
		return object;
	}
}
