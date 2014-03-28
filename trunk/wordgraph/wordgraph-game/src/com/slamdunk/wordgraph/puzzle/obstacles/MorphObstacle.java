package com.slamdunk.wordgraph.puzzle.obstacles;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

/**
 * Masque une lettre
 */
public class MorphObstacle extends NodeObstacle{
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
	
	public MorphObstacle(String target, float interval, List<String> changingLetters) {
		super(ObstaclesTypes.MORPH, target);
		this.interval = interval;
		this.changingLetters = changingLetters;
	}

	@Override
	public void applyEffect(PuzzleGraph graph) {
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
				morphLetter();
				elapsed = 0;
			}
		} else {
			// Sinon on remet les infos d'origine dans le noeud...
			setNodeLetter(getTarget());
			
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
	private void setNodeLetter(String letter) {
		PuzzleNode node = getNode();
		String oldLetter = node.getLetter();
		if (letter.equals(oldLetter)) {
			return;
		}
		
		// Change la valeur de la lettre dans le graphe...
		node.setLetter(letter);
		PuzzleGraph graph = getManager().getPuzzleGraph();
		graph.updateNodeLetter(oldLetter, letter);
		// ... et sur le bouton
		TextButton button = node.getButton();
		button.setText(letter);
		button.setName(letter);
		
		// Change les liens
		for (String linkedLetter : node.getLinks().keySet()) {
			PuzzleNode linkedNode = graph.getNode(linkedLetter);
			linkedNode.updateLink(oldLetter, letter);
		}
	}
	
	@Override
	public void puzzleLoaded(PuzzleGraph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(graph, puzzleAttributes, stage, puzzlePreferences);
		
		// Initialise la valeur du noeud avec la dernière valeur en cours
		// la dernière fois que le puzzle a été joué
		currentLetterIndex = readPreferenceMorphCurrentLetterIndex() - 1;
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
	public void wordValidated(String word) {
		super.wordValidated(word);
		// Une fois qu'un mot a été suggéré, que la lettre soit ou non dans ce mot,
		// l'obstacle redémarre car la suggestion est vidée et la lettre gênée n'est
		// forcément plus dans le mot.
		paused = false;
	}
	
	@Override
	public void wordRejected(String word) {
		super.wordRejected(word);
		// Une fois qu'un mot a été suggéré, que la lettre soit ou non dans ce mot,
		// l'obstacle redémarre car la suggestion est vidée et la lettre gênée n'est
		// forcément plus dans le mot.
		paused = false;
	}
	
	@Override
	public void timeElapsed(float delta) {
		if (paused) {
			return;
		}
		elapsed += delta;
		applyEffect(getManager().getPuzzleGraph());
	}

	/**
	 * Choisis la prochaine lettre et l'affecte au noeud, puis enregistre
	 * ce changement dans les préférences
	 */
	private void morphLetter() {
		// Récupère la prochaine lettre
		currentLetterIndex++;
		if (currentLetterIndex >= changingLetters.size()) {
			currentLetterIndex = 0;
		}
		// Quelle que soit la lettre sélectionnée, on change la valeur
		// de la lettre morphée
		setNodeLetter(changingLetters.get(currentLetterIndex));
		
		// On enregistre la lettre actuellement affichée dans les préférences
		writePreferenceMorphCurrentLetterIndex(currentLetterIndex);
	}
	
	/**
	 * Crée un MorphObstacle initialisé avec les données lues dans le fichier
	 * properties décrivant le puzzle. Ces données ont la forme suivante :
	 * [L]|[s]|[XXXX] avec :
	 *   [L] : lettre du graphe sur laquelle est placé l'obstacle
	 *   [s] : temps en secondes provoquant un changement de lettre
	 *   [XXXX] : différentes valeurs que prendra le noeud. La lettre [L] doit faire
	 *   partie de la liste.
	 * Exemple :
	 * 	- "C|5|MHCJ" : obstacle sur la lettre C, change toutes les 5 secondes et prend
	 * les valeurs M, H, C et J (dans cet ordre). 
	 *  - "U|2|UZX" : obstacle sur la lettre U, change toutes les 2 secondes et prend
	 *  les valeurs U, Z et X (dans cet ordre).
	 * @param propertiesDescription
	 * @return
	 */
	public static MorphObstacle createFromProperties(String propertiesDescription) {
		String[] parameters = propertiesDescription.split("\\|");
		if (parameters.length != 3) {
			throw new IllegalArgumentException("MorphObstacle : Failure to split '" + propertiesDescription + "' in the 3 required parts.");
		}
		String target = parameters[0];
		float interval = Float.valueOf(parameters[1]);
		String letters = parameters[2];
		List<String> changingLetters = new ArrayList<String>();
		for (int curChar = 0; curChar < letters.length(); curChar++) {
			changingLetters.add(String.valueOf(letters.charAt(curChar)));
		}
		return new MorphObstacle(target, interval, changingLetters);
	}
}
