package com.slamdunk.wordgraph.puzzle;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;

public class ScoreBoard {
	private int score;
	private int rightSuggestionSeries;
	private boolean hasMadeWrongSuggestions;
	private Label label;
	private String formatString;
	
	private PuzzlePreferencesHelper puzzlePreferences;
	
	public ScoreBoard(PuzzlePreferencesHelper puzzlePreferences) {
		this.puzzlePreferences = puzzlePreferences;
		score = puzzlePreferences.getScore();
		rightSuggestionSeries = puzzlePreferences.getRightSuggestionSeries();
		hasMadeWrongSuggestions = puzzlePreferences.getHasMadeWrongSuggestions();
	}

	/**
	 * Met à jour le score en ajoutant le nombre de points indiqué
	 * @param value
	 */
	public void updateScore(int value) {
		score += value;
		puzzlePreferences.setScore(score);
		updateLabel();
	}

	public int getScore() {
		return score;
	}
	
	public String getFormatString() {
		return formatString;
	}

	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	/**
	 * Indique si le puzzle est terminé
	 */
	public void setFinished(boolean finished) {
		puzzlePreferences.setFinished(finished);
	}

	/**
	 * Met à jour le score en tenant compte de l'enigme trouvée
	 * @param riddle
	 */
	public void updateScore(Riddle resolvedRiddle) {
		String solution = resolvedRiddle.getSolution();
		int solutionLength = solution.length();
		
		int lengthBonus = 0;
		if (solutionLength >= 12) {
			lengthBonus = 3;
		} else if (solutionLength >= 9) {
			lengthBonus = 2;
		} else if (solutionLength >= 6) {
			lengthBonus = 1;
		}

		int rightSuggestionSeriesBonus = rightSuggestionSeries / 2;
		
		updateScore(resolvedRiddle.getDifficulty()
				+ solutionLength
				+ lengthBonus
				+ rightSuggestionSeriesBonus);
	}

	private void updateLabel() {
		if (label == null) {
			return;
		}
		label.setText(String.format(formatString, score));
	}

	public void setLabel(Label label) {
		this.label = label;
		formatString = label.getText().toString();
		updateLabel();
	}
	
	public void addRightSuggestionSeries() {
		rightSuggestionSeries++;
		puzzlePreferences.setRightSuggestionSeries(rightSuggestionSeries);
	}
	
	public void resetRightSuggestionSeries() {
		rightSuggestionSeries = 0;
		puzzlePreferences.setRightSuggestionSeries(rightSuggestionSeries);
	}
	
	public int getRightSuggestionSeries() {
		return rightSuggestionSeries;
	}

	public void badSuggestion() {
		updateScore(-1);
		resetRightSuggestionSeries();
		hasMadeWrongSuggestions = true;
		puzzlePreferences.setHasMadeWrongSuggestions(true);
	}

	public boolean hasMadeWrongSuggestions() {
		return hasMadeWrongSuggestions;
	}
}
