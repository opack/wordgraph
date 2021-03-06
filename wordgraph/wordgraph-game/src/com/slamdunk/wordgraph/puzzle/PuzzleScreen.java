package com.slamdunk.wordgraph.puzzle;

import static com.slamdunk.wordgraph.puzzle.LetterStates.NORMAL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.utils.PropertiesManager;
import com.slamdunk.utils.ui.ButtonClickListener;
import com.slamdunk.utils.ui.MessageBoxUtils;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.Options;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.blackmarket.BlackMarketItem;
import com.slamdunk.wordgraph.effect.HighlightClueEffect;
import com.slamdunk.wordgraph.effect.VisualEffect;
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
	private List<VisualEffect> effects;
	private List<VisualEffect> renderingEffects;

	// Composants de l'interface qui seront r�utilis�s
	private Stage stage;
	private Label suggestionLabel;
	private TextButton validateButton;
	private Label scoreMultiplierLabel;
	private TextButton backspaceButton;
	private TextButton jokerButton;
	private Table gridTable;
	private LinkDrawer linkDrawer;
	private Image finishedImage;
	
	// On utilise Label plut�t qu'un simple String pour pouvoir d�terminer
	// la position vers laquelle envoyer les lettres s�lectionner, car il
	// nous faut un TextBounds pour d�terminer la taille du mot sugg�r�.
	// On ne peut pas s'appuyer sur le suggestionLabel car la lettre qui
	// est "envoy�e" vers ce label n'appara�t pas encore dans le texte
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
		effects = new ArrayList<VisualEffect>();
		renderingEffects = new ArrayList<VisualEffect>();
		
		stage = new Stage();
	}
	
	public void addBonus(BlackMarketItem bonus) {
		activeBonus.add(bonus);
	}
	

	public Set<BlackMarketItem> getBonuses() {
		return activeBonus;
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
		
		// R�cup�ration de la skin � appliquer
		Skin skin = puzzleAttributes.getSkin();
		if (skin == null) {
			skin = Assets.defaultPuzzleSkin;
		}
		puzzleAttributes.setSkin(skin);
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
		scoreMultiplierLabel = (Label)creator.getActor("score-multiplier");
		backspaceButton = (TextButton)creator.getActor("backspace");
		finishedImage = (Image)creator.getActor("finished");
		jokerButton = (TextButton)creator.getActor("joker");
		
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
			for (String line : puzzleAttributes.getRiddleSentenceLines()) {
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
		
		// Panneau des bonus
		initBonusPanel(creator);
		
		// Grille de boutons
		TextButtonStyle letterStyle = PuzzleButtonDecorator.getInstance().getLetterStyle(NORMAL);
		gridTable = (Table)creator.getActor("grid");
		PuzzleLayout layout = puzzleAttributes.getLayout();
		float letterSize = Math.min(
			gridTable.getWidth() / layout.getWidth() - 10,
			gridTable.getHeight() / layout.getHeight() - 10);
		
		grid = new Grid(layout.getHeight(), layout.getWidth());
		for (int curLine = 0; curLine < layout.getHeight(); curLine ++) {
			for (int curColumn = 0; curColumn < layout.getWidth(); curColumn ++) {
				// Cr�ation du bouton
				final String letter = layout.getLetter(curLine, curColumn);
				final TextButton button = new TextButton(letter, letterStyle);
				// On ne veut pas que le libell� r�cup�re les hits
				button.getLabel().setTouchable(Touchable.disabled);
				if ("_".equals(letter)) {
					button.setVisible(false);
				}
				
				// Cr�ation de la cellule de grille
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
				
				// Ajout du bouton � la grille
				gridTable.add(button).size(letterSize).pad(5);
			}
			// Nouvelle ligne
			gridTable.row();
		}
		
		// Dessinateur de liens
		linkDrawer = (LinkDrawer)creator.getActor("linkdrawer");
		linkDrawer.setCells(selectedCells);
		
		// Au d�but, les boutons de validation et d'annulation sont d�sactiv�s
		// car il n'y a pas de saisie
		validateButton.setDisabled(true);
		backspaceButton.setDisabled(true);
      
		// Chargement de l'image de fin de puzzle
		finishedImage.setScaling(Scaling.none);
		finishedImage.setDrawable(new TextureRegionDrawable(Assets.puzzleDone));
		
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
		
		// Cr�ation du libell� de suggestion courante
		suggestionTextBounds = new Label("", suggestionLabel.getStyle());
		
		// Si le puzzle est fini, on l'affiche
		if (puzzlePreferences.isFinished()) {
        	displayFinishImage();
		}
	}
	

	private void initBonusPanel(SvgUICreator creator) {
		// R�cup�ration des objets
		final Table panel = (Table)creator.getActor("bonus-panel");
		final Button displayBonusPanel = (Button)creator.getActor("display-bonus-panel");
		TextButton bonusRemoveLetterObstacle = (TextButton)creator.getActor("remove_letter_obstacle");
		TextButton bonusRemoveClueObstacle = (TextButton)creator.getActor("remove_clue_obstacle");
		TextButton bonusCrystalBall = (TextButton)creator.getActor("crystal_ball");
		TextButton bonusItem4 = (TextButton)creator.getActor("item4");
		TextButton bonusChrono = (TextButton)creator.getActor("chrono");
		TextButton bonusTwoTimes = (TextButton)creator.getActor("two_times");
		final Table bonusInfoTable = (Table)creator.getActor("bonus-info");
		final Label nameLabel = (Label)creator.getActor("bonus-name");
		final Label descriptionLabel = (Label)creator.getActor("bonus-description");
		final Label unavailableInfoLabel = (Label)creator.getActor("bonus-unavailable-info");
		
		// Place tous les boutons dans un groupe pour qu'un seul soit actif � la fois
		final ButtonGroup bonusButtons = new ButtonGroup();
		
		// Affectation des listeners
		final ButtonClickListener displayPanelListener = new ButtonClickListener() {
			private boolean displayed;
			
			@Override
			public void clicked(Button button) {
				if (displayed) {
					// Masque le panneau d'infos
					bonusInfoTable.setVisible(false);
					
					// Fait remonter le panneau de bonus
					panel.addAction(Actions.moveTo(panel.getX(), stage.getHeight() - displayBonusPanel.getHeight() - 5, 0.3f, Interpolation.fade));
				} else {
					// On s'assure qu'aucun bouton n'arrive s�lectionn�
					bonusButtons.uncheckAll();
					
					// Affichage du panneau (au premier plan)
					panel.setZIndex(stage.getActors().size + 1);
					panel.addAction(Actions.moveTo(panel.getX(), stage.getHeight() - panel.getHeight() + 20, 0.5f, Interpolation.bounceOut));
				}
				displayed = !displayed;
			}
        };
        displayBonusPanel.addListener(displayPanelListener);
		ButtonClickListener bonusListener = new ButtonClickListener() {
			BlackMarketItem lastSelectedItem;
			@Override
			public void clicked(Button button) {
				// Choisit le joker s�lectionn�
				BlackMarketItem selectedItem = BlackMarketItem.valueOf(button.getName());
				if (lastSelectedItem == selectedItem) {
					// Si le bonus ne peut pas �tre appliqu�, on ne fait rien
					if (!selectedItem.isAvailable(PuzzleScreen.this)) {
						return;
					}
					
					// Masque le panneau d'infos
					bonusInfoTable.setVisible(false);
					
					// D�clenche l'effet
					selectedItem.use(PuzzleScreen.this);
					
					// Cache le panneau des bonus
					displayPanelListener.clicked(null);
				} else {
					lastSelectedItem = selectedItem;
					
					// Change les libell�s
					String nameKey = selectedItem.name() + ".name." + Options.langCode;
					String nameText = PropertiesManager.getString("blackmarket", nameKey, "");
					nameLabel.setText(nameText);
					
					String descriptionKey = selectedItem.name() + ".description." + Options.langCode;
					String descriptionText = PropertiesManager.getString("blackmarket", descriptionKey, "");
					descriptionLabel.setText(descriptionText);
					
					String unavailableInfoText = "";
					if (!selectedItem.isAvailable(PuzzleScreen.this)) {
						String unavailableInfoKey = selectedItem.name() + ".unavailableInfo." + Options.langCode;
						unavailableInfoText = PropertiesManager.getString("blackmarket", unavailableInfoKey, "");
					}
					unavailableInfoLabel.setText(unavailableInfoText);
					
//					String priceKey = selectedItem.name() + ".price";
//					String priceText = PropertiesManager.getString("blackmarket", priceKey, "");
//					priceLabel.setText(priceText);
					
					// Affiche la table en face du bonus
					bonusInfoTable.pack();
					bonusInfoTable.setPosition(
						panel.getX() - 5 - bonusInfoTable.getWidth(),
						panel.getY() + button.getY() + button.getHeight() - bonusInfoTable.getHeight());
					bonusInfoTable.setVisible(true);
					bonusInfoTable.setZIndex(stage.getActors().size + 1);
				}
			}
		};
		
		// Activation des boutons et affectation du listener
		for (BlackMarketItem item : BlackMarketItem.values()) {
			TextButton button = (TextButton)creator.getActor(item.name());
			button.addListener(bonusListener);
			bonusButtons.add(button);
		}
		
		// Ajouts dans la table des boutons de bonus
		panel.add().expand().row();
		panel.add(bonusRemoveLetterObstacle).size(56).pad(5).row();
		panel.add(bonusRemoveClueObstacle).size(56).pad(5).row();
		panel.add(bonusCrystalBall).size(56).pad(5).row();
		panel.add(bonusItem4).size(56).pad(5).row();
		panel.add(bonusChrono).size(56).pad(5).row();
		panel.add(bonusTwoTimes).size(56).pad(5).row();
		panel.add(displayBonusPanel).center().padTop(10).size(48);
		panel.pack();
		panel.setPosition(
			stage.getWidth() - 5 - panel.getWidth(),
			stage.getHeight() - displayBonusPanel.getHeight() - 5);
		
		// Ajouts dans la table des infos bonus
		bonusInfoTable.add(nameLabel).width(nameLabel.getWidth()).row();
		bonusInfoTable.add(descriptionLabel).width(descriptionLabel.getWidth()).row();
		bonusInfoTable.add(unavailableInfoLabel).width(unavailableInfoLabel.getWidth()).row();
	}

	/**
	 * Charge un puzzle et cr�e la grille
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
			grid = null;
		} else {
			// D�marrage du chrono
			chrono.start();
		}
	}
	
	private void selectLetter(GridCell cell) {
		// V�rifie si la lettre s�lectionn�e n'est pas d�j� s�lectionn�e
		if (cell.isSelected()
		// V�rifie si la cellule peut �tre atteinte depuis la cellule pr�c�dente
		|| (!selectedCells.isEmpty() && !isReachable(selectedCells.getLast(), cell))) {
			return;
		}
		// S�lectionne la lettre
		cell.select();
		selectedCells.add(cell);
		String letter = cell.getLetter();
		notifyLetterSelected(letter);
		
		// Ajoute la lettre au mot courant
		suggestionTextBounds.setText(suggestionTextBounds.getText() + letter);
		
		// Lance une jolie animation qui "envoie" la lettre affich�e vers le libell� de suggestion
		animateLetterFly(cell.getButton());
	}
	
	/**
	 * Indique si les 2 cellules peuvent �tre atteintes l'une � partir de l'autre.
	 * C'est le cas si eles sont voisines ou que l'une au moins est isol�e.
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
	 * Valide le mot s�lectionn�
	 */
	private void validateWord() {
		// S'il reste une lettre en train d'arriver ou qu'il n'y a pas de lettres
		// s�lectionn�es, on ne peut pas faire de validation		
		if (!pendingLetters.isEmpty() || selectedCells.isEmpty()) {
			return;
		}
		
		// V�rification de la validit� du mot s�lectionn�
		final String suggestion = suggestionTextBounds.getText().toString();
		Riddle riddle = puzzleAttributes.getRiddle(suggestion);
		
		// Si le mot est valide, c'est cool !
		if (riddle != null && !riddle.isFound()) {
			int riddleId = riddle.getId();
			// L'enigme est trouv�e !
			puzzlePreferences.setSolutionFound(riddleId, true);
			riddle.setFound(true);
			
			// Mise � jour des libell�s de solution
			Label label = (Label)stage.getRoot().findActor("solution" + riddleId);
			if (puzzleAttributes.getInfos().getType() == PuzzleTypes.SENTENCE) {
				// Pour les puzzles de type SENTENCE, le libell� de solution est le m�me
				// que celui d'indice
				label = (Label)stage.getRoot().findActor("riddle" + riddleId);
			}
			final Button bullet = (Button)stage.getRoot().findActor("bullet" + riddleId);
			if (label != null) {
				// Envoie la suggestion vers le label de solution
				animateSuggestionFly(suggestion, bullet, label);
			}
			
			// Notifie les listeners qu'un mot a �t� valid�
			notifyWordValidated(suggestion);
			
			// Ajout des points
			scoreBoard.addRightSuggestionSeries();
			scoreBoard.updateScore(riddle);			
		} else {
			// Retrait de points
			scoreBoard.badSuggestion();
			
			// Notifie les listeners qu'un mot a �t� refus�
			notifyWordRejected(suggestion);
		}
		
		// Que le mot ait �t� accept� ou non, on remet le multiplicateur � 1
		setScoreMultiplier(1);
		
		// Application des obstacles sur le graphe
		obstacleManager.applyEffect();
		
		// R�initialisation de la suggestion et des cellules s�lectionn�es
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
					suggestionLabel.getX() + suggestionTextBounds.getTextBounds().width,
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
	 * Affiche l'image indiquant que le puzzle est termin�, et cache les
	 * composants permettant de saisir une suggestion
	 */
	private void displayFinishImage() {
		suggestionLabel.setVisible(false);
		validateButton.setVisible(false);
		backspaceButton.setVisible(false);
		finishedImage.setVisible(true);
		jokerButton.setVisible(false);
		gridTable.setVisible(false);
		linkDrawer.setVisible(false);
		stage.getRoot().findActor("bonus-panel").setVisible(false);
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
			gridTable.setVisible(false);
			linkDrawer.setVisible(false);
			
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
						gridTable.setVisible(true);
						linkDrawer.setVisible(true);
					}
				}
			);
		}
	}

	/**
	 * M�thode appel�e quand le joueur clique sur Joker
	 */
	private void onJoker() {
		game.showJokerScreen();
	}
	
	/**
	 * M�thode appel�e quand le joueur clique sur BlackMarket
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
		// S'il reste une lettre en train d'arriver ou qu'il n'y a pas de suggestion,
		// on ne peut pas faire de backspace
		if (!pendingLetters.isEmpty() || selectedCells.isEmpty()) {
			return;
		}
		
		// Supprime la derni�re lettre s�lectionn�e
		GridCell removed = selectedCells.removeLast();
		notifyLetterUnselected(removed.getLetter());
		
		// D�s�lectionne cette lettre
		removed.unselect();
		
		// Met � jour la suggestion
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
		// Raz du mot s�lectionn�
		suggestionLabel.setText("");
		suggestionTextBounds.setText("");
		pendingLetters.clear();
		
		// D�sactive les boutons
		validateButton.setDisabled(true);
		backspaceButton.setDisabled(true);
		
		// D�s�lectionne les lettres actuellements s�lectionn�es
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
        
        // Dessin des effets visuels
        renderingEffects.clear();
        renderingEffects.addAll(effects);
        SpriteBatch batch = stage.getSpriteBatch();
        batch.begin();
        for (VisualEffect effect : renderingEffects) {
        	if (!effect.update(batch, delta)) {
        		// Si l'effet est termin�, on le retire
        		effects.remove(effect);
        	}
        }
        batch.end();

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
			notifyPuzzleLoaded();
			obstacleManager.applyEffect();
			cancelWord();
		}
		// Dans tous les cas, l'�cran actuel doit r�cup�rer les input
		InputProcessor proc = new InputAdapter() {
			private Vector2 screenCoords = new Vector2();
			private Vector2 stageCoords;
			
			private float quarterWidth;
			private float quarterHeight;
			private Rectangle centerArea = new Rectangle();
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				// Calcul des coordonn�es pour le stage
				screenCoords.set(screenX, screenY);
				stageCoords = stage.screenToStageCoordinates(screenCoords);
				
				// R�cup�ration de l'�ventuel bouton
				Actor hit = stage.hit(stageCoords.x, stageCoords.y, true);
				if (hit != null
				&& (hit instanceof TextButton)
				// On s'int�resse � ce drag s'il est au centre du bouton
				&& isInCenterAreaOf(stageCoords, hit)) {
					// R�cup�re la cellule associ�e et s�lectionne la lettre
					GridCell cell = grid.getCell((Button)hit);
					if (cell != null) {
						selectLetter(cell);
						return true;
					}
				}
				return false;
			}

			private boolean isInCenterAreaOf(Vector2 coords, Actor hit) {
				// Le centre est une zone de la moiti� de la taille du 
				// bouton au centre de celui-ci.
				quarterWidth = hit.getWidth() / 4;
				quarterHeight = hit.getHeight() / 4;
				
				centerArea.x = hit.getX() + quarterWidth;
				centerArea.y = hit.getY() + quarterHeight;
				centerArea.width = quarterWidth * 2;
				centerArea.height = quarterHeight * 2;
				
				return centerArea.contains(coords);
			}
		};
		Gdx.input.setInputProcessor(new InputMultiplexer(proc, stage));
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

	public Grid getGrid() {
		return grid;
	}

	public Actor getActor(String name) {
		return stage.getRoot().findActor(name);
	}

	public void addEffect(VisualEffect effect) {
		effects.add(effect);
	}

	public LinkedList<GridCell> getSelectedCells() {
		return selectedCells;
	}

	public void highlightCell(GridCell cell) {
		HighlightClueEffect effect = new HighlightClueEffect();
		effect.setHighlightActor(cell.getButton());
		effect.init(this);
		
		addEffect(effect);
	}

	public void highlightRiddle(int riddleId) {
		Label label = (Label)getActor("riddle" + riddleId);
		
		HighlightClueEffect effect = new HighlightClueEffect();
		effect.setHighlightActor(label);
		effect.init(this);
		
		addEffect(effect);
	}
}
