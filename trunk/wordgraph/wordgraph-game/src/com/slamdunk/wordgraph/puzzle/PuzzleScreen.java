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
	
	// On utilise Label plut�t qu'un simple String pour pouvoir d�terminer
	// la position vers laquelle envoyer les lettres s�lectionner, car il
	// nous faut un TextBounds pour d�terminer la taille du mot sugg�r�
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
		
		// R�cup�ration de la skin � appliquer
		Skin skin = puzzleAttributes.getSkin();
		if (skin == null) {
			skin = Assets.defaultPuzzleSkin;
		}
		PuzzleButtonDecorator.init(skin);
		
		// Cr�ation des composants depuis le layout d�finit dans le SVG
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
				
				// Si la solution a �t� trouv�e pr�c�demment, on met � jour l'interface
				if (puzzlePreferences.getSolutionFound(riddleId)) {
					solution.setText(riddle.getSolution());
					riddle.setFound(true);
					bullet.setChecked(true);
				}
			}
		} else {
			// R�cup�ration des mod�les
			Label textModel = (Label)creator.getActor("riddle");
			Label solutionModel = (Label)creator.getActor("solution");
			
			// Cr�ation des lignes
			int idx = 0;
			for (String line : puzzleAttributes.getLines()) {
				// R�cup�ration de la Table dans laquelle on va �crire
				Table table = (Table)creator.getActor("line" + idx);
				
				// Recherche le prochain place holder
				int start = 0;
				int prevPlaceholderEnd = 0;
				while ((start = line.indexOf("[", prevPlaceholderEnd)) != -1) {
					// Cr�e un label pour mettre tout le texte pr�c�dant le placeholder
					if (start > prevPlaceholderEnd) {
						String blabla = line.substring(prevPlaceholderEnd, start);
						Label blablaLabel = new Label(blabla, textModel.getStyle());
						table.add(blablaLabel);
					}
					
					// R�cup�re le num�ro de l'�nigme, et l'�nigme elle-m�me
					int riddleId = Integer.parseInt(line.substring(start + 1, start + 2));
					Riddle riddle = puzzleAttributes.getRiddle(riddleId);
					
					// Cr�e le Label et le place dans la table
					Label solutionLabel = new Label(riddle.getClue(), solutionModel.getStyle());
					solutionLabel.setName("riddle" + riddleId);
					table.add(solutionLabel);
					if (puzzlePreferences.getSolutionFound(riddleId)) {
						solutionLabel.setText(riddle.getSolution());
						riddle.setFound(true);
					}
					
					// Continue la recherche � partir de la fin de ce placeholder
					prevPlaceholderEnd = start + 3;
				}
				// Cr�e un label pour mettre tout le texte restant sur
				// la ligne apr�s le dernier placeholder
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
		
		// Au d�but, les boutons de validation et d'annulation sont d�sactiv�s
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
		
		// Cr�ation du libell� de suggestion courante
		currentSuggestion = new Label("", suggestionLabel.getStyle());
	}
	
	/**
	 * Cr�e le graphe
	 * @param graph
	 */
	private void loadGraph(PuzzleGraph graph) {
		// Vidage du graph existant
		graph.clear();
		
		// Cr�ation du graphe
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
		
		// Enregistrement du layout actuel dans les pr�f�rences pour recharger
		// la disposition � l'identique lors du prochaine affichage
		puzzlePreferences.setLayout(graph.getLayout());
		
		// R�cup�re la nombre de liens disponibles entre chaque lettre
		// suite aux �ventuelles pr�c�dentes parties
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
	 * Charge un puzzle et cr�e le graphe
	 * @param puzzleName
	 */
	public void loadPuzzle() {
		// Chargement des pr�f�rences li�es � ce puzzle
		puzzlePreferences = new PuzzlePreferencesHelper(puzzlePack, puzzleName);
		
		// Chargement des attributs du puzzle
		PuzzleAttributesReader puzzleAttributesReader = new PuzzleAttributesReader();
		puzzleAttributes = puzzleAttributesReader.read("puzzles/" + puzzlePack + "/" + puzzleName + ".properties");
		
		// R�cup�ration des gestionnaires d'obstacles
		obstacleManager = puzzleAttributes.getObstacleManager();
		if (obstacleManager == null) {
			obstacleManager = new ObstacleManager();
		}
		if (listeners != null) {
			listeners.clear();
		}
		addListener(obstacleManager);
		
		// R�cup�ration de la skin du puzzle
		Skin puzzleSkin = puzzleAttributes.getSkin();
		if (puzzleSkin == null) {
			puzzleSkin = Assets.defaultPuzzleSkin;
		}
		
		// Cr�ation du tableau de score
		scoreBoard = new ScoreBoard(puzzlePreferences);
		
		// Cr�ation du chrono
		chrono = new Chronometer(puzzlePreferences.getElapsedTime());
		
		if (puzzlePreferences.isFinished()) {
			graph = null;
		} else {
			// D�marrage du chrono
			chrono.start();
		}
	}
	
	public void selectLetter(TextButton button) {
		// R�cup�re la lettre s�lectionn�e et la derni�re lettre actuelle
		String currentWord = currentSuggestion.getText().toString();
		String last = currentWord.isEmpty() ? "" : currentWord.substring(currentWord.length() - 1);
		String selected = button.getName().toString();
		PuzzleNode node = graph.getNode(selected);
		
		// R�cup�re le lien entre les lettres
		selectedLetters.add(selected);
		if (!last.isEmpty()) {
			PuzzleLink link = graph.getLink(last, selected);
			// S'il n'y a pas de lien entre ces lettres, alors on interdit la s�lection
			if (link == null) {
				if (!currentWord.contains(selected)) {
					PuzzleButtonDecorator.getInstance().setStyle(node, NORMAL);
					button.setDisabled(false);
				}
				return;
			}
			// Met en �vidence le lien
			link.select();
			selectedLinks.add(link);
		}
		// S�lectionne le bouton de cette lettre
		selectedNodes.add(node);
		notifyLetterSelected(selected);
		
		// Ajoute la lettre au mot courant
		String newSuggestion = currentWord + selected;
		currentSuggestion.setText(newSuggestion);
		linkDrawer.setWord(newSuggestion);
		
		// Lance une jolie animation qui "envoie" la lettre affich�e vers le libell� de suggestion
		animateLetterFly(button);
		
		// Met en retrait et d�sactive les lettres qui ne sont pas connect�es.
		fadeOutUnreachableLetters(selected);
	}
	
	/**
	 * Cache un peu les lettres qui ne sont pas atteignables
	 * @param sourceLetter La lettre � partir de laquelle on souhaite voir
	 * si les autres lettres sont atteignables
	 */
	private void fadeOutUnreachableLetters(String sourceLetter) {
		for (PuzzleNode node : graph.getNodes()) {
			PuzzleLink link = node.getLink(sourceLetter);
			// Ce noeud peut �tre atteint s'il y a un lien non utilis�
			boolean hasAvailableLink = (link != null && link.isAvailable());
			// Si la lettre est dans la pierre, elle appara�t comme inaccessible
			boolean isStoned = node.isTargeted(ObstaclesTypes.STONE);
			// La lettre peut �tre atteinte si elle a un lien ou si elle est isol�e,
			// mais pas si elle est dans la pierre
			boolean isReachable = hasAvailableLink && !isStoned;
			// Au final, le bouton est d�sactiv� si la letter n'est pas joignable
			node.getButton().setDisabled(!isReachable);
		}
	}

	/**
	 * Valide le mot s�lectionn�
	 */
	private void validateWord() {
		final String suggestion = currentSuggestion.getText().toString();
		// S'il reste une lettre en train d'arriver ou qu'il n'y a pas de suggestion,
		// on ne peut pas faire de validation		
		if (!pendingLetters.isEmpty() || suggestion.isEmpty()) {
			return;
		}
		
		// V�rification de la validit� du mot s�lectionn�
		Riddle riddle = puzzleAttributes.getRiddle(suggestion);
		
		// Si le mot est valide, c'est cool !
		if (riddle != null && !riddle.isFound()) {
			int riddleId = riddle.getId();
			// L'enigme est trouv�e !
			puzzlePreferences.setSolutionFound(riddleId, true);
			riddle.setFound(true);
			
			// Mise � jour des libell�s de solution
			final Label label = (Label)stage.getRoot().findActor("solution" + riddleId);
			final Button bullet = (Button)stage.getRoot().findActor("bullet" + riddleId);
			if (label != null) {
				// Envoie la suggestion vers le label de solution
				animateSuggestionFly(suggestion, bullet, label);
			}
			
			// Suppression des liens utilis�s
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
			
			// Mise � jour des pr�f�rences avec la nouvelle taille des liens
			puzzlePreferences.setLinksSize(usedLinks);
			
			// Cache les lettres isol�es
			hideIsolatedNodes();
			
			// D�sactivation des lettres mises en �vidence
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(graph);
			
			// Notifie les listeners qu'un mot a �t� valid�
			notifyWordValidated(suggestion);
			
			// Ajout des points
			scoreBoard.addRightSuggestionSeries();
			scoreBoard.updateScore(riddle);			
		} else {
			scoreBoard.badSuggestion();
			
			// Notifie les listeners qu'un mot a �t� refus�
			notifyWordRejected(suggestion);
		}
		
		// Application des obstacles sur le graphe
		obstacleManager.applyEffect();
		
		// R�initialisation de la suggestion
		cancelWord();
		
		// S'il ne reste aucune enigme, fin du jeu !
		if (!puzzleAttributes.remainsRiddles()) {
			// Enregistre le temps actuel
			puzzlePreferences.setElapsedTime(chrono.getTime());
			// Attribue des pi�ces
			earnCoins();
			// Indique que le puzzle est termin�
			scoreBoard.setFinished(true);
			// Stoppe le chrono
			chrono.stop();
			// Cache la ligne de suggestion et remplace le graphe par l'image de fin de puzzle
			displayFinishImage();
		}
	}
	
	/**
	 * Anime "l'envoi" de la lettre indiqu�e vers le label de suggestion
	 * @param suggestion
	 * @param destination
	 */
	private void animateLetterFly(TextButton button) {
		// On travaille bien avec la lettre "affich�e" et non "r�elle", car en cas d'obstacle
		// brouillard la lettre affich�e sera "?".
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
							// On ne g�re l'action que si le bouton est toujours dans le graphe
							return true;
						}
						// Suppression de la lettre copi�e
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
	 * Anime "l'envoi" de la suggestion indiqu�e vers le label indiqu�
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
							// On ne g�re l'action que si le label est toujours dans le graphe
							return true;
						}
						// Suppression du label copi�
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
	 * Affiche l'image indiquant que le puzzle est termin�, et cache les
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
	 * Fait gagner des pi�ces au joueur en fonction du temps pass� sur le puzzle
	 * @param time
	 */
	private void earnCoins() {
		int earnedCoins = 0;
		
		// Pi�ces gagn� en fonction du temps de r�solution
		PuzzleInfos infos = puzzleAttributes.getInfos();
		float time = chrono.getTime();
		if (time <= infos.getGoldTime()) {
			earnedCoins += 3;
		} else if (time <= infos.getSilverTime()) {
			earnedCoins += 2;
		} else if (time <= infos.getBronzeTime()) {
			earnedCoins += 1;
		}
		
		// Aucune suggestion erron�e = 1 pi�ce
		if (!scoreBoard.hasMadeWrongSuggestions()) {
			earnedCoins++;
		}
		
		// Ajout du nombre de pi�ces
		// ...
	}

	/**
	 * M�thode appel�e quand le joueur appuie sur la touche Back
	 */
	private void onBack() {
		if (puzzlePreferences.isFinished()) {
			backToPackScreen();
		} else {
			// On arr�te le compteur et masque le graphe pendant l'affichage de la bo�te de dialoug
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
						
						// Retourne � l'�cran de pack
						backToPackScreen();
					}
				},
				new ButtonClickListener() {
					@Override
					public void clicked(Button button) {
						// Red�marre le compteur et raffiche le graphe
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
	 * M�thode appel�e quand le joueur clique sur Joker
	 */
	private void onJoker() {
		game.showJokerScreen(puzzleAttributes, graph);
	}
	
	/**
	 * Retourne � l'�cran de pack
	 */
	private void backToPackScreen() {
		// D�charge la skin
		if (puzzleAttributes.getSkin() != null && !"default-puzzle".equals(puzzleAttributes.getSkinName())) {
			Assets.disposeSkin(puzzleAttributes.getSkinName());
		}
		// Retour � l'�cran du pack
		game.showPackScreen(puzzlePack);
	}
	
	/**
	 * Annule le mot actuellement s�lectionn�
	 */
	private void backspace() {
		String curText = suggestionLabel.getText().toString();
		String curSuggestion = currentSuggestion.getText().toString();
		// S'il reste une lettre en train d'arriver ou qu'il n'y a pas de suggestion,
		// on ne peut pas faire de backspace		
		if (!pendingLetters.isEmpty() || curText.isEmpty()) {
			return;
		}
		
		// Supprime la derni�re lettre s�lectionn�e
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
		
		// D�s�lection des liens qui ne sont plus s�lectionn�s
		selectedLetters.removeLast();
		if (!selectedLinks.isEmpty()) {
			PuzzleLink lastLink = selectedLinks.removeLast();
			lastLink.unselect();
		}

		if (!selectedNodes.isEmpty()) {
			// Suppression du dernier node s�lectionn�
			PuzzleNode lastNode = selectedNodes.removeLast();
		
			// D�s�lection de ce node si la lettre n'est plus dans le mot
			if (newSuggestion.indexOf(removedLetter) == -1) {
				PuzzleButtonDecorator.getInstance().setStyle(lastNode, NORMAL);
				lastNode.getButton().setDisabled(false);
			}
		}
		
		// Mise en �vidence des lettres accessibles
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
		// Raz du mot s�lectionn�
		suggestionLabel.setText("");
		currentSuggestion.setText("");
		pendingLetters.clear();
		validateButton.setDisabled(true);
		backspaceButton.setDisabled(true);
		selectedLinks.clear();
		selectedLetters.clear();
		
		// Raz des liens s�lectionn�s
		for (String letter : graph.getLetters()) {
			for (PuzzleLink link : graph.getLinks(letter)) {
				link.setSelected(0);
			}
		}
		
		// Raz des lettres s�lectionn�es
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
		
		// Mise � jour des acteurs
        stage.act(Gdx.graphics.getDeltaTime());
        
        // Mise � jour du compteur de temps
        chrono.update(delta);
        
        // Mise � jour du temps pour les obstacles int�ress�s
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
		// On ne recr�e le stage, le puzzle et tout que si on le doit
		if (reloadPuzzle) {
	        loadPuzzle();
	        createUI();
			notifyPuzzleLoaded(graph, puzzlePreferences);
		}
		// Dans tous les cas, l'�cran actuel doit r�cup�rer les input
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}
}
