package com.slamdunk.wordgraph.pack.parsing;

import java.io.IOException;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.slamdunk.wordgraph.pack.PackAttributes;
import com.slamdunk.wordgraph.pack.PuzzleInfos;

public class PackAttributesReader {
	private Properties loadedProperties;
	private boolean loaded;
	private String packPath;

	/**
	 * Lit le fichier de propriétés spécifié et crée un objet PuzzleAttributes
	 * @param file
	 * @return
	 */
	public PackAttributes read(String path, String packFile) {
		// Ouverture du fichier
		loadFile(path + "/" + packFile);
		packPath = path;
		
		PackAttributes packAttributes = new PackAttributes();
		
		// Charge les infos du pack
		loadPackInfos(packAttributes);
		
		// Charge les graphismes du pack (images de fond, images des boutons...)
		// ...
		
		// Charge les infos puzzle
		loadPuzzleInfos(packAttributes);
		
		return packAttributes;
	}
	
	public void loadFile(String file) {
		loadedProperties = new Properties();
		FileHandle handle = null;
		try {
			handle = Gdx.files.internal(file);
			loadedProperties.load(handle.reader());
			loaded = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Properties loadPuzzleProperties(String path, String puzzlePropsFile) {
		Properties properties = new Properties();
		FileHandle handle = null;
		try {
			handle = Gdx.files.internal(path + "/" + puzzlePropsFile);
			properties.load(handle.reader());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}
	
	public void loadPackInfos(PackAttributes packAttributes) {
		if (!loaded) {
			throw new IllegalStateException("No properties file loaded !");
		}
		String name = loadedProperties.getProperty("name");
		String label = loadedProperties.getProperty("label", "");
		int difficulty = Integer.parseInt(loadedProperties.getProperty("difficulty", "1"));
		boolean available = Boolean.parseBoolean(loadedProperties.getProperty("available", "false"));
		
		packAttributes.setName(name);
		packAttributes.setLabel(label);
		packAttributes.setDifficulty(difficulty);
		packAttributes.setAvailable(available);
	}

	public void loadPuzzleInfos(PackAttributes packAttributes) {
		if (!loaded) {
			throw new IllegalStateException("No properties file loaded !");
		}
		int count = 0;
		String name = null;
		String label = null;
		String description = null;
		int difficulty = 0;
		boolean available = false;
		float goldTime = 0;
		float silverTime = 0;
		float bronzeTime = 0;
		while ((name = loadedProperties.getProperty("puzzle." + count + ".name")) != null) {
			available = Boolean.parseBoolean(loadedProperties.getProperty("puzzle." + count + ".available", "false"));
			
			// Ouverture et lecture du fichier de propriétés du puzzle
			Properties puzzleProperties = loadPuzzleProperties(packPath, name + ".properties");
			label = puzzleProperties.getProperty("label", "");
			description = puzzleProperties.getProperty("description", "");
			difficulty = Integer.parseInt(puzzleProperties.getProperty("difficulty", "1"));
			
			goldTime = Float.parseFloat(puzzleProperties.getProperty("time.gold", "0"));
			silverTime = Float.parseFloat(puzzleProperties.getProperty("time.silver", "0"));
			bronzeTime = Float.parseFloat(puzzleProperties.getProperty("time.bronze", "0"));
			
			// Création du bean d'infos
			PuzzleInfos infos = new PuzzleInfos();
			infos.setName(name);
			infos.setLabel(label);
			infos.setDescription(description);
			infos.setDifficulty(difficulty);
			infos.setAvailable(available);
			
			infos.setGoldTime(goldTime);
			infos.setSilverTime(silverTime);
			infos.setBronzeTime(bronzeTime);
			
			packAttributes.setPuzzleInfos(name, infos);
			
			count++;
		}
	}
}
