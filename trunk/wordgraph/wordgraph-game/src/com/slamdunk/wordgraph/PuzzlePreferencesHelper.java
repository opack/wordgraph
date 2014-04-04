package com.slamdunk.wordgraph;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Permet de manipuler facilement les préférences liées à un puzzle d'un pack en particulier
 *
 */
public class PuzzlePreferencesHelper implements Preferences {
	private Preferences prefs;
	
	public PuzzlePreferencesHelper(String puzzlePack, String puzzleName) {
		prefs = Gdx.app.getPreferences(getPreferencesKey(puzzlePack, puzzleName));
	}

	/**
	 * Définit le score du puzzle
	 * @param puzzlePack
	 * @param puzzleName
	 * @param score
	 * @param finished
	 */
	public void setScore(int score) {
		prefs.putInteger("score", score);
		prefs.flush();
	}
	
	/**
	 * Définit si puzzle est fini ou non
	 * @param puzzlePack
	 * @param puzzleName
	 * @param score
	 * @param finished
	 */
	public void setFinished(boolean finished) {
		prefs.putBoolean("finished", finished);
		prefs.flush();
	}

	/**
	 * Récupère le score du puzzle indiqué
	 * @param puzzlePack
	 * @param puzzleName
	 * @return
	 */
	public int getScore() {
		return prefs.getInteger("score", 0);
	}

	public String getPreferencesKey(String puzzlePack, String puzzleName) {
		return puzzlePack + "_" + puzzleName;
	}

	/**
	 * Indique si la solution de l'enigme d'id mentionné est trouvée ou non
	 * @param puzzlePack
	 * @param puzzleName
	 * @param solution
	 * @return
	 */
	public boolean getSolutionFound(int riddleId) {
		return prefs.getBoolean("solution." + riddleId + ".found", false);
	}
	
	/**
	 * Indique si la solution de l'enigme d'id mentionné est trouvée ou non
	 * @param puzzlePack
	 * @param puzzleName
	 * @param solution
	 * @return
	 */
	public void setSolutionFound(int riddleId, boolean found) {
		prefs.putBoolean("solution." + riddleId + ".found", found);
		prefs.flush();
	}

	public boolean isFinished() {
		return prefs.getBoolean("finished", false);
	}

	public int getRightSuggestionSeries() {
		return prefs.getInteger("rightSuggestionSeries", 0);
	}

	public void setRightSuggestionSeries(int value) {
		prefs.putInteger("rightSuggestionSeries", value);
		prefs.flush();
	}

	public void setElapsedTime(float time) {
		prefs.putFloat("elapsedTime", time);
		prefs.flush();
	}
	
	public float getElapsedTime() {
		return prefs.getFloat("elapsedTime", 0);
	}

	public boolean getHasMadeWrongSuggestions() {
		return prefs.getBoolean("hasMadeWrongSuggestions", false);
	}

	public void setHasMadeWrongSuggestions(boolean hasMadeWrongSuggestions) {
		prefs.putBoolean("hasMadeWrongSuggestions", hasMadeWrongSuggestions);
		prefs.flush();
	}

	public void setLayout(String[] layout) {
		prefs.putInteger("layout.size", layout.length);
		for (int curLine = 0; curLine < layout.length; curLine++) {
			prefs.putString("layout." + curLine, layout[curLine]);
		}
		prefs.flush();
	}
	
	public String[] getLayout() {
		int height = prefs.getInteger("layout.size", -1);
		if (height == -1) {
			return null;
		}
		String[] layout = new String[height];
		for (int curLine = 0; curLine < height; curLine++) {
			layout[curLine] = prefs.getString("layout." + curLine);
		}
		return layout;
	}

	public void putBoolean(String key, boolean val) {
		prefs.putBoolean(key, val);
	}

	public void putInteger(String key, int val) {
		prefs.putInteger(key, val);
	}

	public void putLong(String key, long val) {
		prefs.putLong(key, val);
	}

	public void putFloat(String key, float val) {
		prefs.putFloat(key, val);
	}

	public void putString(String key, String val) {
		prefs.putString(key, val);
	}

	public void put(Map<String, ?> vals) {
		prefs.put(vals);
	}

	public boolean getBoolean(String key) {
		return prefs.getBoolean(key);
	}

	public int getInteger(String key) {
		return prefs.getInteger(key);
	}

	public long getLong(String key) {
		return prefs.getLong(key);
	}

	public float getFloat(String key) {
		return prefs.getFloat(key);
	}

	public String getString(String key) {
		return prefs.getString(key);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	public int getInteger(String key, int defValue) {
		return prefs.getInteger(key, defValue);
	}

	public long getLong(String key, long defValue) {
		return prefs.getLong(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		return prefs.getFloat(key, defValue);
	}

	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	public Map<String, ?> get() {
		return prefs.get();
	}

	public boolean contains(String key) {
		return prefs.contains(key);
	}

	public void clear() {
		prefs.clear();
	}

	public void remove(String key) {
		prefs.remove(key);
	}

	public void flush() {
		prefs.flush();
	}
}
