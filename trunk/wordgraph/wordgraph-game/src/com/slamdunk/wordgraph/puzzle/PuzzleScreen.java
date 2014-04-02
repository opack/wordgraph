package com.slamdunk.wordgraph.puzzle;

import static com.slamdunk.wordgraph.puzzle.LetterStates.NORMAL;
import static com.slamdunk.wordgraph.puzzle.graph.LayoutFactory.GRID_HEIGHT;
import static com.slamdunk.wordgraph.puzzle.graph.LayoutFactory.GRID_WIDTH;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.utils.ui.ButtonClickListener;
import com.slamdunk.utils.ui.MessageBoxUtils;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.pack.PuzzleInfos;
import com.slamdunk.wordgraph.puzzle.graph.LinkDrawer;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLink;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstacleManager;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes;
import com.slamdunk.wordgraph.puzzle.parsing.PuzzleAttributesReader;

public class PuzzleScreen implements Screen {
	private WordGraphGame game;
	private String puzzlePack;
	private String puzzleName;
	private boolean reloadPuzzle;
	
	private PuzzlePreferencesHelper puzzlePreferences;
	private PuzzleAttributes puzzleAttributes;
	private PuzzleGraph graph;
	private ObstacleManager obstacleManager;
	private LinkDrawer linkDrawer;
	
	private ScoreBoard scoreBoard;
	private Chronometer chrono;

	private Stage stage;
	private Label suggestionLabel;
	private TextButton validateButton;
	private TextButton backspaceButton;
	private TextButton jokerButton;
	private Image finishedImage;
	
	// On utilise Label plutôt qu'un simple String pour pouvoir déterminer
	// la position vers laquelle envoyer les lettres sélectionner, car il
	// nous faut un TextBounds pour déterminer la taille du mot suggéré
	private Label currentSuggestion;
	private LinkedList<String> pendingLetters;
	private LinkedList<PuzzleLink> selectedLinks;
	private LinkedList<PuzzleNode> selectedNodes;
	private LinkedList<String> selectedLetters;
	
	private List<PuzzleListener> listeners;
	
	private FPSLogger fpsLogger = new FPSLogger();
	
	public PuzzleScreen(WordGraphGame game) {
		this.game = game;
		pendingLetters = new LinkedList<String>();
		selectedLinks = new LinkedList<PuzzleLink>();
		selectedLetters = new LinkedList<String>();
		selectedNodes = new LinkedList<PuzzleNode>();
		
		stage = new Stage();
		graph = new PuzzleGraph();
	}
	
	private void addListener(PuzzleListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<PuzzleListener>();
		}
		listeners.add(listener);
	}
	
	private void notifyPuzzleLoaded(PuzzleGraph graph, PuzzlePreferencesHelper puzzlePreferences) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.puzzleLoaded(graph, puzzleAttributes, stage, puzzlePreferences);
			}
		}
	}
	
	private void notifyGraphLoaded(PuzzleGraph graph) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.graphLoaded(graph);
			}
		}
	}
	
	private void notifyLinkUsed(PuzzleLink link) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.linkUsed(link);
			}
		}
	}
	
	private void notifyNodeHidden(PuzzleNode node) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.nodeHidden(node);
			}
		}
	}
	
	private void notifyLetterSelected(String letter) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.letterSelected(letter);
			}
		}
	}
	
	private void notifyLetterUnselected(String letter) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.letterUnselected(letter);
			}
		}
	}
	
	private void notifyWordValidated(String word) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.wordValidated(word);
			}
		}
	}
	
	private void notifyWordRejected(String word) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.wordRejected(word);
			}
		}
	}
	
	private void notifyTimeElapsed(float delta) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.timeElapsed(delta);
			}
		}
	}
	
	public void setPuzzleToPlay(String pack, String puzzle) {
		this.puzzlePack = pack;
		this.puzzleName = puzzle;
		reloadPuzzle = true;
	}

	private void createUI() {
		// Nettoyage du puzzle actuel
		pendingLetters.clear();
		stage.clear();
		
		// Récupération de la skin à appliquer
		Skin skin = puzzleAttributes.getSkin();
		if (skin == null) {
			skin = Assets.defaultPuzzleSkin;
		}
		PuzzleButtonDecorator.init(skin);
		
		// Création des composants depuis le layout définit dans le SVG
		SvgUICreator creator = new SvgUICreator(skin);
		creator.load("layouts/puzzle-common.ui.svg");
		if (puzzleAttributes.getInfos().getType() == PuzzleTypes.WORDS) {
			creator.load("layouts/puzzle-words.ui.svg");
		} else {
			creator.load("layouts/puzzle-sentence.ui.svg");
		}
		suggestionLabel = (Label)creator.getActor("suggestion");
		validateButton = (TextButton)creator.getActor("validate");
		backspaceButton = (TextButton)creator.getActor("backspace");
		finishedImage = (Image)creator.getActor("finished");
		jokerButton = (TextButton)creator.getActor("joker");
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setVisible(background.getDrawable() != null);
		
		// Titre du puzzle
		Label title = (Label)creator.getActor("title");
		title.setText(puzzleAttributes.getInfos().getLabel());
		
		// Chargement des indices
		List<Riddle> riddles = puzzleAttributes.getRiddles();
		if (puzzleAttributes.getInfos().getType() == PuzzleTypes.WORDS) {
			// On a un tableau d'indices
			for (Riddle riddle : riddles) {
				int riddleId = riddle.getId();
				Label clue = (Label)creator.getActor("riddle" + riddleId);
				clue.setText(riddle.getClue());
				Label solution = (Label)creator.getActor("solution" + riddleId);
				Button bullet = (Button)creator.getActor("bullet" + riddleId);
				bullet.setVisible(true);
				bullet.setDisabled(true);
				
				// Si la solution a été trouvée précédemment, on met à jour l'interface
				if (puzzlePreferences.getSolutionFound(riddleId)) {
					solution.setText(riddle.getSolution());
					riddle.setFound(true);
					bullet.setChecked(true);
				}
			}
		} else {
			// Récupération des modèles
			Label textModel = (Label)creator.getActor("riddle");
			Label solutionModel = (Label)creator.getActor("solution");
			
			// Création des lignes
			int idx = 0;
			for (String line : puzzleAttributes.getLines()) {
				// Récupération de la Table dans laquelle on va écrire
				Table table = (Table)creator.getActor("line" + idx);
				
				// Recherche le prochain place holder
				int start = 0;
				int prevPlaceholderEnd = 0;
				while ((start = line.indexOf("[", prevPlaceholderEnd)) != -1) {
					// Crée un label pour mettre tout le texte précédant le placeholder
					if (start > prevPlaceholderEnd) {
						String blabla = line.substring(prevPlaceholderEnd, start);
						Label blablaLabel = new Label(blabla, textModel.getStyle());
						table.add(blablaLabel);
					}
					
					// Récupère le numéro de l'énigme, et l'énigme elle-même
					int riddleId = Integer.parseInt(line.substring(start + 1, start + 2));
					Riddle riddle = puzzleAttributes.getRiddle(riddleId);
					
					// Crée le Label et le place dans la table
					Label solutionLabel = new Label(riddle.getClue(), solutionModel.getStyle());
					solutionLabel.setName("riddle" + riddleId);
					table.add(solutionLabel);
					if (puzzlePreferences.getSolutionFound(riddleId)) {
						solutionLabel.setText(riddle.getSolution());
						riddle.setFound(true);
					}
					
					// Continue la recherche à partir de la fin de ce placeholder
					prevPlaceholderEnd = start + 3;
				}
				// Crée un label pour mettre tout le texte restant sur
				// la ligne après le dernier placeholder
				if (prevPlaceholderEnd < line.length()) {
					String blabla = line.substring(prevPlaceholderEnd);
					Label blablaLabel = new Label(blabla, textModel.getStyle());
					table.add(blablaLabel);
				}
				
				table.layout();
				
				idx++;
			}
		}
		
		// Label de temps et de score
		chrono.setLabel((Label)creator.getActor("timer"), 1f);
		scoreBoard.setLabel((Label)creator.getActor("score"));
		
		// Au début, les boutons de validation et d'annulation sont désactivés
		// car il n'y a pas de saisie
		validateButton.setDisabled(true);
		backspaceButton.setDisabled(true);
      
		// Chargement de l'image de fin de puzzle
		finishedImage.setScaling(Scaling.none);
		finishedImage.setDrawable(new TextureRegionDrawable(Assets.puzzleDone));
		if (puzzlePreferences.isFinished()) {
        	displayFinishImage();
		}
		
		// Initialisation du dessinateur de liens
		linkDrawer = (LinkDrawer)creator.getActor("linkdrawer");
		linkDrawer.setGraph(graph);
		
		// Affectation des listeners
		ButtonClickListener	letterSelectListener = new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				selectLetter((TextButton)button);
			}
		};
		TextButton letterButton;
		for (int curLine = 0; curLine < GRID_HEIGHT; curLine++) {
			for (int curColumn = 0; curColumn < GRID_WIDTH; curColumn++) {
				letterButton = (TextButton)creator.getActor("letter" + curLine + curColumn);
				letterButton.addListener(letterSelectListener);
			}
		}
		creator.getActor("back").addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				onBack();
			}
        });
		creator.getActor("joker").addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				onJoker();
			}
        });
		validateButton.addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				validateWord();
			}
        });
		backspaceButton.addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				backspace();
			}
        });
        // Ajout du listener du bouton back
        stage.addListener(new InputListener(){
        	@Override
     		public boolean keyUp(InputEvent event, int keycode) {
     			onBack();
				return true;
     		};
     	});
        
		// Ajout des composants au stage
		creator.populate(stage);
		
		// Chargement du graph
		loadGraph(graph);
		reloadPuzzle = false;
		
		// Création du libellé de suggestion courante
		currentSuggestion = new Label("", suggestionLabel.getStyle());
	}
	
	/**
	 * Crée le graphe
	 * @param graph
	 */
	private void loadGraph(PuzzleGraph graph) {
		// Vidage du graph existant
		graph.clear();
		
		// Création du graphe
		List<String> solutions = new ArrayList<String>();
		for (Riddle riddle : puzzleAttributes.getRiddles()) {
			solutions.add(riddle.getSolution());
		}
		graph.load(solutions);
		notifyGraphLoaded(graph);
		
		// Arrangement du graphe et affectation des lettres aux boutons
		final Group stageRoot = stage.getRoot();
		final TextButton[][] letterButtons = new TextButton[GRID_HEIGHT][GRID_WIDTH];
		for (int curLine = 0; curLine < GRID_HEIGHT; curLine++) {
			for (int curColumn = 0; curColumn < GRID_WIDTH; curColumn++) {
				letterButtons[curLine][curColumn] = (TextButton)stageRoot.findActor("letter" + curLine + curColumn);
			}
		}
		String[] prefsLayout = puzzlePreferences.getLayout();
		String[] attributesLayout = puzzleAttributes.getLayout();
		if (prefsLayout != null) {
			graph.layout(prefsLayout, letterButtons);
		} else if (attributesLayout != null) {
			graph.layout(attributesLayout, letterButtons);
		} else {
			graph.layout(letterButtons);
		}
		
		// Enregistrement du layout actuel dans les préférences pour recharger
		// la disposition à l'identique lors du prochaine affichage
		puzzlePreferences.setLayout(graph.getLayout());
		
		// Récupère la nombre de liens disponibles entre chaque lettre
		// suite aux éventuelles précédentes parties
		for (String letter : graph.getLetters()) {
			for (PuzzleLink link : graph.getLinks(letter)) {
				int size = puzzlePreferences.getLinkSize(link.getName());
				if (size > -1) {
					link.setSize(size);
				}
			}
		}
		
		// Cache les noeuds qui n'ont plus de lien disponible
		hideIsolatedNodes();
	}

	/**
	 * Charge un puzzle et crée le graphe
	 * @param puzzleName
	 */
	public void loadPuzzle() {
		// Chargement des préférences liées à ce puzzle
		puzzlePreferences = new PuzzlePreferencesHelper(puzzlePack, puzzleName);
		
		// Chargement des attributs du puzzle
		PuzzleAttributesReader puzzleAttributesReader = new PuzzleAttributesReader();
		puzzleAttributes = puzzleAttributesReader.read("puzzles/" + puzzlePack + "/" + puzzleName + ".properties");
		
		// Récupération des gestionnaires d'obstacles
		obstacleManager = puzzleAttributes.getObstacleManager();
		if (obstacleManager == null) {
			obstacleManager = new ObstacleManager();
		}
		if (listeners != null) {
			listeners.clear();
		}
		addListener(obstacleManager);
		
		// Récupération de la skin du puzzle
		Skin puzzleSkin = puzzleAttributes.getSkin();
		if (puzzleSkin == null) {
			puzzleSkin = Assets.defaultPuzzleSkin;
		}
		
		// Création du tableau de score
		scoreBoard = new ScoreBoard(puzzlePreferences);
		
		// Création du chrono
		chrono = new Chronometer(puzzlePreferences.getElapsedTime());
		
		if (puzzlePreferences.isFinished()) {
			graph = null;
		} else {
			// Démarrage du chrono
			chrono.start();
		}
	}
	
	public void selectLetter(TextButton button) {
		// Récupère la lettre sélectionnée et la dernière lettre actuelle
		String currentWord = currentSuggestion.getText().toString();
		String last = currentWord.isEmpty() ? "" : currentWord.substring(currentWord.length() - 1);
		String selected = button.getName().toString();
		PuzzleNode node = graph.getNode(selected);
		
		// Récupère le lien entre les lettres
		selectedLetters.add(selected);
		if (!last.isEmpty()) {
			PuzzleLink link = graph.getLink(last, selected);
			// S'il n'y a pas de lien entre ces lettres, alors on interdit la sélection
			if (link == null) {
				if (!currentWord.contains(selected)) {
					PuzzleButtonDecorator.getInstance().setStyle(node, NORMAL);
					button.setDisabled(false);
				}
				return;
			}
			// Met en évidence le lien
			link.select();
			selectedLinks.add(link);
		}
		// Sélectionne le bouton de cette lettre
		selectedNodes.add(node);
		notifyLetterSelected(selected);
		
		// Ajoute la lettre au mot courant
		String newSuggestion = currentWord + selected;
		currentSuggestion.setText(newSuggestion);
		linkDrawer.setWord(newSuggestion);
		
		// Lance une jolie animation qui "envoie" la lettre affichée vers le libellé de suggestion
		animateLetterFly(button);
		
		// Met en retrait et désactive les lettres qui ne sont pas connectées.
		fadeOutUnreachableLetters(selected);
	}
	
	/**
	 * Cache un peu les lettres qui ne sont pas atteignables
	 * @param sourceLetter La lettre à partir de laquelle on souhaite voir
	 * si les autres lettres sont atteignables
	 */
	private void fadeOutUnreachableLetters(String sourceLetter) {
		for (PuzzleNode node : graph.getNodes()) {
			PuzzleLink link = node.getLink(sourceLetter);
			// Ce noeud peut être atteint s'il y a un lien non utilisé
			boolean hasAvailableLink = (link != null && link.isAvailable());
			// Si la lettre est dans la pierre, elle apparaît comme inaccessible
			boolean isStoned = node.isTargeted(ObstaclesTypes.STONE);
			// La lettre peut être atteinte si elle a un lien ou si elle est isolée,
			// mais pas si elle est dans la pierre
			boolean isReachable = hasAvailableLink && !isStoned;
			// Au final, le bouton est désactivé si la letter n'est pas joignable
			node.getButton().setDisabled(!isReachable);
		}
	}

	/**
	 * Valide le mot sélectionné
	 */
	private void validateWord() {
		final String suggestion = currentSuggestion.getText().toString();
		// S'il reste une lettre en train d'arriver ou qu'il n'y a pas de suggestion,
		// on ne peut pas faire de validation		
		if (!pendingLetters.isEmpty() || suggestion.isEmpty()) {
			return;
		}
		
		// Vérification de la validité du mot sélectionné
		Riddle riddle = puzzleAttributes.getRiddle(suggestion);
		
		// Si le mot est valide, c'est cool !
		if (riddle != null && !riddle.isFound()) {
			int riddleId = riddle.getId();
			// L'enigme est trouvée !
			puzzlePreferences.setSolutionFound(riddleId, true);
			riddle.setFound(true);
			
			// Mise à jour des libellés de solution
			final Label label = (Label)stage.getRoot().findActor("solution" + riddleId);
			final Button bullet = (Button)stage.getRoot().findActor("bullet" + riddleId);
			if (label != null) {
				// Envoie la suggestion vers le label de solution
				animateSuggestionFly(suggestion, bullet, label);
			}
			
			// Suppression des liens utilisés
			List<PuzzleLink> usedLinks = new ArrayList<PuzzleLink>();
			for (String letter : graph.getLetters()) {
				for (PuzzleLink link : graph.getLinks(letter)) {
					if (link.isSelected()) {
						link.unselect();
						link.setSize(link.getSize() - 1);
						usedLinks.add(link);
						notifyLinkUsed(link);
					}
				}
			}
			
			// Mise à jour des préférences avec la nouvelle taille des liens
			puzzlePreferences.setLinksSize(usedLinks);
			
			// Cache les lettres isolées
			hideIsolatedNodes();
			
			// Désactivation des lettres mises en évidence
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(graph);
			
			// Notifie les listeners qu'un mot a été validé
			notifyWordValidated(suggestion);
			
			// Ajout des points
			scoreBoard.addRightSuggestionSeries();
			scoreBoard.updateScore(riddle);			
		} else {
			scoreBoard.badSuggestion();
			
			// Notifie les listeners qu'un mot a été refusé
			notifyWordRejected(suggestion);
		}
		
		// Application des obstacles sur le graphe
		obstacleManager.applyEffect();
		
		// Réinitialisation de la suggestion
		cancelWord();
		
		// S'il ne reste aucune enigme, fin du jeu !
		if (!puzzleAttributes.remainsRiddles()) {
			// Enregistre le temps actuel
			puzzlePreferences.setElapsedTime(chrono.getTime());
			// Attribue des pièces
			earnCoins();
			// Indique que le puzzle est terminé
			scoreBoard.setFinished(true);
			// Stoppe le chrono
			chrono.stop();
			// Cache la ligne de suggestion et remplace le graphe par l'image de fin de puzzle
			displayFinishImage();
		}
	}
	
	/**
	 * Anime "l'envoi" de la lettre indiquée vers le label de suggestion
	 * @param suggestion
	 * @param destination
	 */
	private void animateLetterFly(TextButton button) {
		// On travaille bien avec la lettre "affichée" et non "réelle", car en cas d'obstacle
		// brouillard la lettre affichée sera "?".
		String text = button.getText().toString();
		pendingLetters.add(text);
		
		// Pendant l'animation, on ne peut pas faire de validation ni de backspace
		validateButton.setDisabled(true);
		backspaceButton.setDisabled(true);
		
		final TextButton copy = new TextButton(text, button.getStyle());
		copy.setName("copy" + button.getName());
		copy.setBounds(
			button.getX(),
			button.getY(),
			button.getWidth(),
			button.getHeight());
		copy.setColor(1,1,1,0.5f);
		stage.addActor(copy);
		copy.addAction(Actions.sequence(
				Actions.moveTo(
					suggestionLabel.getX() + currentSuggestion.getTextBounds().width,
					suggestionLabel.getY() + suggestionLabel.getHeight() / 2 - copy.getHeight() / 2,
					0.6f,
					Interpolation.circleOut),
				new Action() {
					@Override
					public boolean act(float delta) {
						if (!copy.hasParent()) {
							// On ne gère l'action que si le bouton est toujours dans le graphe
							return true;
						}
						// Suppression de la lettre copiée
						copy.setVisible(false);
						copy.remove();
						
						// Ajoute la prochaine lettre en attente d'ajout
						if (!pendingLetters.isEmpty()) {
							suggestionLabel.setText(suggestionLabel.getText() + pendingLetters.pop());
							
							// Permet de valider ou d'annuler la saisie
							validateButton.setDisabled(false);
							backspaceButton.setDisabled(false);
						}
						return true;
					}
				}
			)
		);
	}
	
	/**
	 * Anime "l'envoi" de la suggestion indiquée vers le label indiqué
	 * @param suggestion
	 * @param destination
	 */
	private void animateSuggestionFly(final String suggestion, final Button bullet, final Label destination) {
		LabelStyle style = new LabelStyle(suggestionLabel.getStyle());
		style.background = null;
		final Label copy = new Label(suggestion, style);
		copy.setPosition(suggestionLabel.getX(), suggestionLabel.getY());
		stage.addActor(copy);
		copy.addAction(Actions.sequence(
				Actions.moveTo(
					destination.getX() + destination.getParent().getX(),
					destination.getY() + destination.getParent().getY(),
					0.6f, Interpolation.circleOut),
				new Action() {
					@Override
					public boolean act(float delta) {
						if (!copy.hasParent()) {
							// On ne gère l'action que si le label est toujours dans le graphe
							return true;
						}
						// Suppression du label copié
						copy.setVisible(false);
						copy.remove();
						
						// Ajoute la solution
						destination.setText(suggestion);
						
						// Modifie la puce
						if (bullet != null) {
							bullet.setChecked(true);
						}
						return true;
					}
				}
			)
		);
	}
	
	/**
	 * Cache les noeuds qui n'ont plus aucun lien avec d'autres noeuds
	 */
	private void hideIsolatedNodes() {
		for (PuzzleNode node : graph.getNodes()) {
			if (!node.isReachable()) {
				TextButton button = node.getButton();
				if (button != null) {
					button.setVisible(false);
				}
				notifyNodeHidden(node);
			}
		}
	}
	
	/**
	 * Affiche l'image indiquant que le puzzle est terminé, et cache les
	 * composants permettant de saisir une suggestion
	 */
	private void displayFinishImage() {
		suggestionLabel.setVisible(false);
		validateButton.setVisible(false);
		backspaceButton.setVisible(false);
		finishedImage.setVisible(true);
		jokerButton.setVisible(false);
	}

	/**
	 * Fait gagner des pièces au joueur en fonction du temps passé sur le puzzle
	 * @param time
	 */
	private void earnCoins() {
		int earnedCoins = 0;
		
		// Pièces gagné en fonction du temps de résolution
		PuzzleInfos infos = puzzleAttributes.getInfos();
		float time = chrono.getTime();
		if (time <= infos.getGoldTime()) {
			earnedCoins += 3;
		} else if (time <= infos.getSilverTime()) {
			earnedCoins += 2;
		} else if (time <= infos.getBronzeTime()) {
			earnedCoins += 1;
		}
		
		// Aucune suggestion erronée = 1 pièce
		if (!scoreBoard.hasMadeWrongSuggestions()) {
			earnedCoins++;
		}
		
		// Ajout du nombre de pièces
		// ...
	}

	/**
	 * Méthode appelée quand le joueur appuie sur la touche Back
	 */
	private void onBack() {
		if (puzzlePreferences.isFinished()) {
			backToPackScreen();
		} else {
			// On arrête le compteur et masque le graphe pendant l'affichage de la boîte de dialoug
			chrono.stop();
			showLetters(true);
			
			MessageBoxUtils.showConfirm(
				"Quitter le puzzle ?",
				stage,
				new ButtonClickListener() {
					@Override
					public void clicked(Button button) {
						// Enregistre le temps actuel
						puzzlePreferences.setElapsedTime(chrono.getTime());
						
						// Retourne à l'écran de pack
						backToPackScreen();
					}
				},
				new ButtonClickListener() {
					@Override
					public void clicked(Button button) {
						// Redémarre le compteur et raffiche le graphe
						chrono.start();
						showLetters(true);
					}
				});
		}
	}
	
	private void showLetters(boolean show) {
		for (PuzzleNode node : graph.getNodes()) {
			TextButton button = node.getButton();
			if (button != null) {
				button.setVisible(show);
			}
		}
	}

	/**
	 * Méthode appelée quand le joueur clique sur Joker
	 */
	private void onJoker() {
		game.showJokerScreen(puzzleAttributes, graph);
	}
	
	/**
	 * Retourne à l'écran de pack
	 */
	private void backToPackScreen() {
		// Décharge la skin
		if (puzzleAttributes.getSkin() != null && !"default-puzzle".equals(puzzleAttributes.getSkinName())) {
			Assets.disposeSkin(puzzleAttributes.getSkinName());
		}
		// Retour à l'écran du pack
		game.showPackScreen(puzzlePack);
	}
	
	/**
	 * Annule le mot actuellement sélectionné
	 */
	private void backspace() {
		String curText = suggestionLabel.getText().toString();
		String curSuggestion = currentSuggestion.getText().toString();
		// S'il reste une lettre en train d'arriver ou qu'il n'y a pas de suggestion,
		// on ne peut pas faire de backspace		
		if (!pendingLetters.isEmpty() || curText.isEmpty()) {
			return;
		}
		
		// Supprime la dernière lettre sélectionnée
		String newText = curText.substring(0, curText.length() - 1);
		suggestionLabel.setText(newText);
		String newSuggestion = curSuggestion.substring(0, curText.length() - 1);
		currentSuggestion.setText(newSuggestion);
		String removedLetter = curSuggestion.substring(curText.length() - 1);
		notifyLetterUnselected(removedLetter);
		linkDrawer.setWord(newSuggestion);
		
		// Activation des boutons
		boolean isTextEmpty = newText.isEmpty();
		validateButton.setDisabled(isTextEmpty);
		backspaceButton.setDisabled(isTextEmpty);
		
		// Désélection des liens qui ne sont plus sélectionnés
		selectedLetters.removeLast();
		if (!selectedLinks.isEmpty()) {
			PuzzleLink lastLink = selectedLinks.removeLast();
			lastLink.unselect();
		}

		if (!selectedNodes.isEmpty()) {
			// Suppression du dernier node sélectionné
			PuzzleNode lastNode = selectedNodes.removeLast();
		
			// Désélection de ce node si la lettre n'est plus dans le mot
			if (newSuggestion.indexOf(removedLetter) == -1) {
				PuzzleButtonDecorator.getInstance().setStyle(lastNode, NORMAL);
				lastNode.getButton().setDisabled(false);
			}
		}
		
		// Mise en évidence des lettres accessibles
		if (!newSuggestion.isEmpty()) {
			String lastLetter = newSuggestion.substring(newSuggestion.length() - 1);
			fadeOutUnreachableLetters(lastLetter);
		} else {
			// Il n'y a plus de lettres dans la suggestion : toutes les lettres sont donc accessibles
			for (PuzzleNode node : graph.getNodes()) {
				TextButton button = node.getButton();
				if (button != null) {
					button.setDisabled(false);
				}
			}
		}
	}
	
	private void cancelWord() {
		// Raz du mot sélectionné
		suggestionLabel.setText("");
		currentSuggestion.setText("");
		pendingLetters.clear();
		validateButton.setDisabled(true);
		backspaceButton.setDisabled(true);
		selectedLinks.clear();
		selectedLetters.clear();
		
		// Raz des liens sélectionnés
		for (String letter : graph.getLetters()) {
			for (PuzzleLink link : graph.getLinks(letter)) {
				link.setSelected(0);
			}
		}
		
		// Raz des lettres sélectionnées
		// DBGgraph.selectAllNodes(false);
		
		// Raz des lettres non joignables
		PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(graph);
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render(float delta) {
		
		// Mise à jour des acteurs
        stage.act(Gdx.graphics.getDeltaTime());
        
        // Mise à jour du compteur de temps
        chrono.update(delta);
        
        // Mise à jour du temps pour les obstacles intéressés
        notifyTimeElapsed(delta);
        
        // Dessin des autres composants du stage (label, boutons, noeuds...)
        Gdx.gl.glClearColor(245 / 255f, 237 / 255f, 217 / 255f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.draw();

        fpsLogger.log();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(480, 800, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
		// On ne recrée le stage, le puzzle et tout que si on le doit
		if (reloadPuzzle) {
	        loadPuzzle();
	        createUI();
			notifyPuzzleLoaded(graph, puzzlePreferences);
		}
		// Dans tous les cas, l'écran actuel doit récupérer les input
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}
}
