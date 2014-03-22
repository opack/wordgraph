package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

/**
 * Masque une lettre
 */
public class MorphObstacle extends NodeObstacle{
	/**
	 * Liste des lettres qui seront affectées à la cible
	 */
	private String changingLetters;
	
	/**
	 * L'index de la lettre actuellement affichée
	 */
	private int currentLetterIndex;
	
	/**
	 * La lettre actuellement affichée
	 */
	private String currentLetter;
	
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
	
	public MorphObstacle(String target, float interval, String changingLetters) {
		super(ObstaclesTypes.MORPH, target);
		this.interval = interval;
		this.changingLetters = changingLetters;
	}

	@Override
	public void applyEffect(Graph graph) {
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
		GraphNode node = getNode();
		
		// Change les liens
		String oldLetter = node.getName();
		for (GraphLink link : node.getLinks()) {
			if (link.getSourceLetter().equals(oldLetter)) {
				link.setSourceLetter(letter);
			}
			if (link.getTargetLetter().equals(oldLetter)) {
				link.setTargetLetter(letter);
			}
		}
		
		// Change la valeur de la lettre
		node.setText(letter);
		node.setName(letter);
	}
	
	@Override
	public void puzzleLoaded(Graph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
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
		if (letter.equals(currentLetter)) {
			paused = true;
		}
	}
	
	@Override
	public void letterUnselected(String letter) {
		super.letterUnselected(letter);
		// Si on désélectionne cette lettre, alors on remet l'obstacle en marche
		if (letter.equals(currentLetter)) {
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
		applyEffect(getManager().getGraph());
	}

	/**
	 * Choisis la prochaine lettre et l'affecte au noeud, puis enregistre
	 * ce changement dans les préférences
	 */
	private void morphLetter() {
		// Récupère la prochaine lettre
		currentLetterIndex++;
		if (currentLetterIndex >= changingLetters.length()) {
			currentLetterIndex = 0;
		}
		currentLetter = String.valueOf(changingLetters.charAt(currentLetterIndex));
		
		// Quelle que soit la lettre sélectionnée, on change la valeur
		// de la lettre morphée
		setNodeLetter(currentLetter);
		
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
		String changingLetters = parameters[2];
		return new MorphObstacle(target, interval, changingLetters);
	}
}
