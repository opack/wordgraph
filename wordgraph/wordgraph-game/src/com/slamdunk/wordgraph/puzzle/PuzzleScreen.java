package com.slamdunk.wordgraph.puzzle;

import static com.slamdunk.wordgraph.puzzle.LetterStates.NORMAL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.utils.ui.ButtonClickListener;
import com.slamdunk.utils.ui.MessageBoxUtils;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.blackmarket.BlackMarketItem;
import com.slamdunk.wordgraph.pack.PuzzleInfos;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleLayout;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;
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
	private Grid grid;
	private ObstacleManager obstacleManager;
	
	private ScoreBoard scoreBoard;
	private Chronometer chrono;
	
	private Set<BlackMarketItem> activeBonus;

	// Composants de l'interface qui seront réutilisés
	private Stage stage;
	private Label suggestionLabel;
	private TextButton validateButton;
	private Label scoreMultiplierLabel;
	private TextButton backspaceButton;
	private TextButton jokerButton;
	private TextButton blackMarketButton;
	private Table gridTable;
	private LinkDrawer linkDrawer;
	private Image finishedImage;
	
	// On utilise Label plutôt qu'un simple String pour pouvoir déterminer
	// la position vers laquelle envoyer les lettres sélectionner, car il
	// nous faut un TextBounds pour déterminer la taille du mot suggéré.
	// On ne peut pas s'appuyer sur le suggestionLabel car la lettre qui
	// est "envoyée" vers ce label n'apparaît pas encore dans le texte
	// et la taille n'est donc pas correcte.
	private Label suggestionTextBounds;
	private LinkedList<String> pendingLetters;
	private LinkedList<GridCell> selectedCells;
	
	private List<PuzzleListener> listeners;
	
	private FPSLogger fpsLogger = new FPSLogger();
	
	public PuzzleScreen(WordGraphGame game) {
		this.game = game;
		pendingLetters = new LinkedList<String>();
		selectedCells = new LinkedList<GridCell>();
		activeBonus = new HashSet<BlackMarketItem>();
		
		stage = new Stage();
	}
	
	public void addBonus(BlackMarketItem bonus) {
		activeBonus.add(bonus);
	}
	
	private void addListener(PuzzleListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<PuzzleListener>();
		}
		listeners.add(listener);
	}
	
	private void notifyPuzzleLoaded() {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.puzzleLoaded(grid, puzzleAttributes, stage, puzzlePreferences);
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
				listener.wordValidated(word, selectedCells);
			}
		}
	}
	
	private void notifyWordRejected(String word) {
		if (listeners != null) {
			for (PuzzleListener listener : listeners) {
				listener.wordRejected(word, selectedCells);
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
		scoreMultiplierLabel = (Label)creator.getActor("score-multiplier");
		backspaceButton = (TextButton)creator.getActor("backspace");
		finishedImage = (Image)creator.getActor("finished");
		jokerButton = (TextButton)creator.getActor("joker");
		blackMarketButton = (TextButton)creator.getActor("black-market");
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setVisible(background.getDrawable() != null);
		
		// Titre du puzzle
		Label title = (Label)creator.getActor("title");
		title.setText(puzzleAttributes.getInfos().getLabel());
		
		// Label de temps et de score
		chrono.setFormatStrings("Temps : %02d:%02d", "Pause : %04.1f");
		chrono.setLabel((Label)creator.getActor("timer"), 1f);
		chrono.updateLabel();
		scoreBoard.setLabel((Label)creator.getActor("score"));
		
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
			for (String line : puzzleAttributes.getRiddleSentenceLines()) {
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
		
		// Grille de boutons
		TextButtonStyle letterStyle = PuzzleButtonDecorator.getInstance().getLetterStyle(NORMAL);
		gridTable = (Table)creator.getActor("grid");
		PuzzleLayout layout = puzzleAttributes.getLayout();
		grid = new Grid(layout.getHeight(), layout.getWidth());
		for (int curLine = 0; curLine < layout.getHeight(); curLine ++) {
			for (int curColumn = 0; curColumn < layout.getWidth(); curColumn ++) {
				// Création du bouton
				final String letter = layout.getLetter(curLine, curColumn);
				final TextButton button = new TextButton(letter, letterStyle);
				if ("_".equals(letter)) {
					button.setVisible(false);
				}
				
				// Création de la cellule de grille
				final GridCell cell = new GridCell(curLine, curColumn, letter);
				cell.setButton(button);
				grid.putCell(cell);
				
				// Affectation du listener
				button.addListener(new ButtonClickListener() {
					@Override
					public void clicked(Button button) {
						selectLetter(cell);
					}
				});
				
				// Ajout du bouton à la grille
				gridTable.add(button).expand().fill().pad(5);
			}
			// Nouvelle ligne
			gridTable.row();
		}
		
		// Dessinateur de liens
		linkDrawer = (LinkDrawer)creator.getActor("linkdrawer");
		linkDrawer.setCells(selectedCells);
		
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
		
		// Affectation des listeners
		creator.getActor("back").addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				onBack();
			}
        });
		jokerButton.addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				onJoker();
			}
        });
		blackMarketButton.addListener(new ButtonClickListener() {
			@Override
			public void clicked(Button button) {
				onBlackMarket();
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
		reloadPuzzle = false;
		
		// Création du libellé de suggestion courante
		suggestionTextBounds = new Label("", suggestionLabel.getStyle());
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
			grid = null;
		} else {
			// Démarrage du chrono
			chrono.start();
		}
	}
	
	private void selectLetter(GridCell cell) {
		// Vérifie si la lettre sélectionnée n'est pas déjà sélectionnée
		if (cell.isSelected()
		// Vérifie si la cellule peut être atteinte depuis la cellule précédente
		|| (!selectedCells.isEmpty() && !isReachable(selectedCells.getLast(), cell))) {
			return;
		}
		// Sélectionne la lettre
		cell.select();
		selectedCells.add(cell);
		String letter = cell.getLetter();
		notifyLetterSelected(letter);
		
		// Ajoute la lettre au mot courant
		suggestionTextBounds.setText(suggestionTextBounds.getText() + letter);
		
		// Lance une jolie animation qui "envoie" la lettre affichée vers le libellé de suggestion
		animateLetterFly(cell.getButton());
	}
	
	/**
	 * Indique si les 2 cellules peuvent être atteintes l'une à partir de l'autre.
	 * C'est le cas si eles sont voisines ou que l'une au moins est isolée.
	 * @param cell1
	 * @param cell2
	 * @return
	 */
	private boolean isReachable(GridCell cell1, GridCell cell2) {
		return cell1.isAround(cell2)
		|| cell1.isTargeted(ObstaclesTypes.ISLE)
		|| cell2.isTargeted(ObstaclesTypes.ISLE);
	}

	/**
	 * Valide le mot sélectionné
	 */
	private void validateWord() {
		// S'il reste une lettre en train d'arriver ou qu'il n'y a pas de lettres
		// sélectionnées, on ne peut pas faire de validation		
		if (!pendingLetters.isEmpty() || selectedCells.isEmpty()) {
			return;
		}
		
		// Vérification de la validité du mot sélectionné
		final String suggestion = suggestionTextBounds.getText().toString();
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
			
			// Notifie les listeners qu'un mot a été validé
			notifyWordValidated(suggestion);
			
			// Ajout des points
			scoreBoard.addRightSuggestionSeries();
			scoreBoard.updateScore(riddle);			
		} else {
			// Retrait de points
			scoreBoard.badSuggestion();
			
			// Notifie les listeners qu'un mot a été refusé
			notifyWordRejected(suggestion);
		}
		
		// Que le mot ait été accepté ou non, on remet le multiplicateur à 1
		setScoreMultiplier(1);
		
		// Application des obstacles sur le graphe
		obstacleManager.applyEffect();
		
		// Réinitialisation de la suggestion et des cellules sélectionnées
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
					suggestionLabel.getX() + suggestionTextBounds.getTextBounds().width,
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
	 * Affiche l'image indiquant que le puzzle est terminé, et cache les
	 * composants permettant de saisir une suggestion
	 */
	private void displayFinishImage() {
		suggestionLabel.setVisible(false);
		validateButton.setVisible(false);
		backspaceButton.setVisible(false);
		finishedImage.setVisible(true);
		jokerButton.setVisible(false);
		blackMarketButton.setVisible(false);
		gridTable.setVisible(false);
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
			gridTable.setVisible(false);
			
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
						gridTable.setVisible(true);
					}
				});
		}
	}

	/**
	 * Méthode appelée quand le joueur clique sur Joker
	 */
	private void onJoker() {
		game.showJokerScreen(puzzleAttributes, grid);
	}
	
	/**
	 * Méthode appelée quand le joueur clique sur BlackMarket
	 */
	private void onBlackMarket() {
		game.showBlackMarketScreen(this);
	}
	
	public PuzzleAttributes getPuzzleAttributes() {
		return puzzleAttributes;
	}

	public Chronometer getChrono() {
		return chrono;
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
		// S'il reste une lettre en train d'arriver ou qu'il n'y a pas de suggestion,
		// on ne peut pas faire de backspace
		if (!pendingLetters.isEmpty() || selectedCells.isEmpty()) {
			return;
		}
		
		// Supprime la dernière lettre sélectionnée
		GridCell removed = selectedCells.removeLast();
		notifyLetterUnselected(removed.getLetter());
		
		// Désélectionne cette lettre
		removed.unselect();
		
		// Met à jour la suggestion
		CharSequence cur = suggestionLabel.getText();
		suggestionLabel.setText(cur.subSequence(0, cur.length() - 1));
		cur = suggestionTextBounds.getText();
		suggestionTextBounds.setText(cur.subSequence(0, cur.length() - 1));
		
		// Activation des boutons
		boolean canBackspace = selectedCells.isEmpty();
		validateButton.setDisabled(canBackspace);
		backspaceButton.setDisabled(canBackspace);
	}

	private void cancelWord() {
		// Raz du mot sélectionné
		suggestionLabel.setText("");
		suggestionTextBounds.setText("");
		pendingLetters.clear();
		
		// Désactive les boutons
		validateButton.setDisabled(true);
		backspaceButton.setDisabled(true);
		
		// Désélectionne les lettres actuellements sélectionnées
		for (GridCell cell : selectedCells) {
			cell.unselect();
		}
		selectedCells.clear();
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
			notifyPuzzleLoaded();
			obstacleManager.applyEffect();
		}
		// Dans tous les cas, l'écran actuel doit récupérer les input
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	public void setScoreMultiplier(int multiplier) {
		scoreMultiplierLabel.setVisible(multiplier > 1);
		scoreMultiplierLabel.setText("x" + multiplier);
		scoreBoard.setScoreMultiplier(multiplier);
	}

	public ObstacleManager getObstacleManager() {
		return obstacleManager;
	}

	public String getCurrentWord() {
		return suggestionTextBounds.getText().toString();
	}

	public Grid getGrid() {
		return grid;
	}
}
