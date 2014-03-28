package com.slamdunk.wordgraph;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;

/**
 * Permet de manipuler facilement les préférences liées à un puzzle d'un pack en particulier
 *
 */
public class PuzzlePreferencesHelper {
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

	public void setLinksSize(List<PuzzleLink> links) {
		for (PuzzleLink link : links) {
			prefs.putInteger("link." + link.getName() + ".size", link.getSize());
		}
		prefs.flush();
	}
	
	/**
	 * Indique si le lien mentionné est trouvé ou non
	 * @param puzzlePack
	 * @param puzzleName
	 * @param name
	 * @return
	 */
	public int getLinkSize(String name) {
		return prefs.getInteger("link." + name + ".size", -1);
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

	public boolean isObstacleActive(String type, String target) {
		return prefs.getBoolean("isObstacleActive." + type + "." + target, true);
	}

	public void setObstacleActive(String type, String target, boolean isActive) {
		prefs.putBoolean("isObstacleActive." + type + "." + target, isActive);
		prefs.flush();
	}
	
	public int getMorphCurrentLetterIndex(String type, String target) {
		return prefs.getInteger("morphCurrentLetterIndex." + type + "." + target, 0);
	}

	public void setMorphCurrentLetterIndex(String type, String target, int index) {
		prefs.putInteger("morphCurrentLetterIndex." + type + "." + target, index);
		prefs.flush();
	}

	public int getBombCountDown(String target) {
		return prefs.getInteger("bombCountDown." + target, -1);
	}
	
	public void setBombCountDown(String target, int countDown) {
		prefs.putInteger("bombCountDown." + target, countDown);
		prefs.flush();
	}
}
