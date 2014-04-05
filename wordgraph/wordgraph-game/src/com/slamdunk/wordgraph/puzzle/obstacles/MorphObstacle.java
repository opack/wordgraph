package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

/**
 * Masque une lettre
 */
public class MorphObstacle extends CellObstacle{
	/**
	 * Lettre d'origine de la cellule, avant que l'obstacle ne soit appliqué
	 */
	private String originalLetter;
	/**
	 * Liste des lettres qui seront affectées à la cible
	 */
	private List<String> changingLetters;
	
	/**
	 * L'index de la lettre actuellement affichée
	 */
	private int currentLetterIndex;
	
	/**
	 * Interval (en secondes) entre 2 changements
	 */
	private float interval;
	
	/**
	 * Temps écoulé (en secondes) depuis le dernier changement
	 */
	private float elapsed;
	
	/**
	 * Indique si l'obstacle est en pause, typiquement quand une lettre
	 * est sélectionnée.
	 */
	private boolean paused;
	
	public MorphObstacle() {
		changingLetters = new ArrayList<String>();
		setType(ObstaclesTypes.MORPH);
	}
	
	@Override
	public void applyEffect(Grid grid) {
		super.applyEffect(grid);
		// Si l'obstacle est actif, on affiche une image sur la lettre
		// pour montrer qu'un obstacle morph est présent
		if (isActive()) {
			// Place une image de brouillard sur la lettre isolée
			if (getImage() == null) {
				createImage("obstacle-morph");
			}
			// Si le temps est écoulé depuis le dernier changement,
			// on passe à la lettre suivante
			if (elapsed >= interval) {
				currentLetterIndex++;
				morphLetter();
				elapsed = 0;
			}
		} else {
			// Sinon on remet les infos d'origine dans le noeud...
			setCellLetter(originalLetter);
			
			// ... et on supprime l'image
			if (getImage() != null) {
				getImage().remove();
				setImage(null);
			}
		}
	}

	/**
	 * Change la lettre du noeud (texte + name)
	 * @param target
	 */
	private void setCellLetter(String letter) {
		// Conserve la lettre source lors du premier changement
		GridCell cell = getCell();
		String oldLetter = cell.getLetter();
		
		// Si on veut appliquer la même lettre, on n'a rien à faire
		if (letter.equals(oldLetter)) {
			return;
		}
		
		// Change la valeur de la lettre dans la grille
		cell.setLetter(letter);
		cell.getButton().setText(letter);
	}
	
	@Override
	public void puzzleLoaded(Grid grid, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(grid, puzzleAttributes, stage, puzzlePreferences);
		
		// Enregistre la lettre originelle
		originalLetter = getCell().getLetter();
		
		// Met à jour la lettre actuellement affichée
		morphLetter();
	}

	@Override
	public void letterSelected(String letter) {
		super.letterSelected(letter);
		// Si on sélectionne cette lettre, alors on met l'obstacle en pause
		if (letter.equals(changingLetters.get(currentLetterIndex))) {
			paused = true;
		}
	}
	
	@Override
	public void letterUnselected(String letter) {
		super.letterUnselected(letter);
		// Si on désélectionne cette lettre, alors on remet l'obstacle en marche
		if (letter.equals(changingLetters.get(currentLetterIndex))) {
			paused = false;
		}
	}
	
	@Override
	public void wordValidated(String word, List<GridCell> cells) {
		super.wordValidated(word, cells);
		// Une fois qu'un mot a été suggéré, que la lettre soit ou non dans ce mot,
		// l'obstacle redémarre car la suggestion est vidée et la lettre gênée n'est
		// forcément plus dans le mot.
		paused = false;
	}
	
	@Override
	public void wordRejected(String word, List<GridCell> cells) {
		super.wordRejected(word, cells);
		// Une fois qu'un mot a été suggéré, que la lettre soit ou non dans ce mot,
		// l'obstacle redémarre car la suggestion est vidée et la lettre gênée n'est
		// forcément plus dans le mot.
		paused = false;
	}
	
	@Override
	public void timeElapsed(float delta) {
		super.timeElapsed(delta);
		if (paused) {
			return;
		}
		elapsed += delta;
		applyEffect(getManager().getGrid());
	}

	/**
	 * Choisis la prochaine lettre et l'affecte au noeud, puis enregistre
	 * ce changement dans les préférences
	 */
	private void morphLetter() {
		// Récupère la prochaine lettre
		if (currentLetterIndex >= changingLetters.size()) {
			currentLetterIndex = 0;
		}
		// Quelle que soit la lettre sélectionnée, on change la valeur
		// de la lettre morphée
		setCellLetter(changingLetters.get(currentLetterIndex));
		
		// On enregistre la lettre actuellement affichée dans les préférences
		saveToPreferences();
	}
	
	@Override
	public void initFromProperties(PropertiesEx properties, String key) {
		super.initFromProperties(properties, key);
		
		// Charge la liste des lettres tournantes
		String letters = properties.getStringProperty(key + ".letters", "");
		changingLetters.clear();
		for (int curChar = 0; curChar < letters.length(); curChar++) {
			changingLetters.add(String.valueOf(letters.charAt(curChar)));
		}
		
		// Lit l'intervalle de changement
		interval = properties.getIntegerProperty(key + ".interval", 3);
	}
	
	/**
	 * Lit les informations de l'instance de l'obstacle depuis les préférences
	 */
	public void loadFromPreferences() {
		super.loadFromPreferences();
		PuzzlePreferencesHelper prefs = getPuzzlePreferences();
		
		currentLetterIndex = prefs.getInteger(getPreferencesKey() + ".index", 0);
	}
	
	@Override
	public void saveToPreferences() {
		PuzzlePreferencesHelper prefs = getPuzzlePreferences();
		
		prefs.putInteger(getPreferencesKey() + ".index", currentLetterIndex);
		
		// Faire le super en dernier car il contient le flush()
		super.saveToPreferences();
	}
}
