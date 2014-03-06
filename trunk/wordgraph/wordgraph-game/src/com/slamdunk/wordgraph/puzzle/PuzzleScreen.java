package com.slamdunk.wordgraph.puzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.slamdunk.utils.MessageBoxUtils;
import com.slamdunk.utils.ui.svg.SvgUICreator;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.WordGraphGame;
import com.slamdunk.wordgraph.pack.PuzzleInfos;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphEdge;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;
import com.slamdunk.wordgraph.puzzle.parsing.PuzzleAttributesReader;

public class PuzzleScreen implements Screen {
	private WordGraphGame game;
	private String puzzlePack;
	private String puzzleName;
	private boolean reloadPuzzle;
	
	private PuzzlePreferencesHelper puzzlePreferences;
	private PuzzleAttributes puzzleAttributes;
	private Graph graph;
	
	private ScoreBoard scoreBoard;
	private Chronometer chrono;

	private PuzzleButtonDecorator puzzleButtonDecorator;
	private Stage stage;
	private Map<String, Label> solutionLabels;
	private Label suggestionLabel;
	private TextButton validateButton;
	private TextButton cancelButton;
	private TextButton jokerButton;
	private Image finishedImage;
	
	// On utilise Label plutôt qu'un simple String pour pouvoir déterminer
	// la position vers laquelle envoyer les lettres sélectionner, car il
	// nous faut un TextBounds pour déterminer la taille du mot suggéré
	private Label currentSuggestion;
	private LinkedList<String> pendingLetters;
	
	private FPSLogger fpsLogger = new FPSLogger();
	
	public PuzzleScreen(WordGraphGame game) {
		this.game = game;
		solutionLabels = new HashMap<String, Label>();
		stage = new Stage();
		stage.addListener(new InputListener(){
     		public boolean keyUp(InputEvent event, int keycode) {
     			onBack();
				return true;
     		};
     	});
		pendingLetters = new LinkedList<String>();
	}
	
	public void setPuzzleToPlay(String pack, String puzzle) {
		this.puzzlePack = pack;
		this.puzzleName = puzzle;
		reloadPuzzle = true;
	}

	private void createUI() {
		pendingLetters.clear();
		stage.clear();
		// Récupération de la skin à appliquer
		Skin skin = puzzleAttributes.getSkin();
		if (skin == null) {
			skin = Assets.uiSkin;
		}
		
		// Création des composants depuis le layout définit dans le SVG
		SvgUICreator creator = new SvgUICreator(skin);
		creator.load("layouts/puzzle.ui.svg");
		suggestionLabel = (Label)creator.getActor("suggestion");
		validateButton = (TextButton)creator.getActor("validate");
		cancelButton = (TextButton)creator.getActor("cancel");
		finishedImage = (Image)creator.getActor("finished");
		jokerButton = (TextButton)creator.getActor("joker");
		
		// Image de fond
		Image background = (Image)creator.getActor("background");
		background.setZIndex(0);
		background.setVisible(background.getDrawable() != null);
		
		// Titre du puzzle
		Label title = (Label)creator.getActor("title");
		title.setText(puzzleAttributes.getInfos().getLabel());
		
		// Tableau des indices
		solutionLabels.clear();
		int idx = 0;
		for (Riddle riddle : puzzleAttributes.getRiddles()) {
			Label clue = (Label)creator.getActor("riddle" + idx);
			clue.setText(riddle.getClue());
			Label solution = (Label)creator.getActor("solution" + idx);
			solutionLabels.put(riddle.getSolution(), solution);

			// Si la solution a été trouvée précédemment, on met à jour l'interface
			if (puzzlePreferences.getSolutionFound(riddle.getSolution())) {
				solution.setText(riddle.getSolution());
				riddle.setFound(true);
			}
			idx++;
		}
		
		// Label de temps et de score
		chrono.setLabel((Label)creator.getActor("timer"), 1f);
		scoreBoard.setLabel((Label)creator.getActor("score"));
		
		// Par défaut, les boutons de validation et d'annulation sont désactivés
		// car il n'y a pas de saisie
		validateButton.setDisabled(true);
		cancelButton.setDisabled(true);
      
		// Chargement du graph
		puzzleButtonDecorator = new PuzzleButtonDecorator(this, skin);
		graph = (Graph)creator.getActor("graph");
		loadGraph(graph, puzzleButtonDecorator);
		
		// Chargement de l'image de fin de puzzle
		finishedImage.setScaling(Scaling.none);
		finishedImage.setDrawable(new TextureRegionDrawable(Assets.puzzleDone));
		if (puzzlePreferences.isFinished()) {
        	displayFinishImage();
		}
		
		// Affectation des listeners
		creator.getActor("back").addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		onBack();
			}
        });
		creator.getActor("joker").addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		onJoker();
			}
        });
		validateButton.addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
    			validateWord();
        	}
        });
		cancelButton.addListener(new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
    			cancelWord();
        	}
        });
		
		// Ajout des composants au stage
		creator.populate(stage);
		
		reloadPuzzle = false;
		
		// Création du libellé de suggestion courante
		currentSuggestion = new Label("", suggestionLabel.getStyle());
	}
	
	/**
	 * Crée le graphe
	 * @param graph
	 */
	private void loadGraph(Graph graph, PuzzleButtonDecorator decorator) {
		// Création du graphe
		graph.load("puzzles/" + puzzlePack + "/"  + puzzleName + ".gml", decorator);
		
		// Cache les liens et noeuds déjà cachés précédemment
		for (GraphEdge edge : graph.getEdges()) {
			edge.setVisible(puzzlePreferences.getEdgeVisible(edge.getName()));	
		}
		graph.hideIsolatedNodes();
	}

	/**
	 * Charge un puzzle et crée le graphe
	 * @param puzzleName
	 */
	public void loadPuzzle() {
		// Chargement des préférences liées à ce puzzle
		puzzlePreferences = new PuzzlePreferencesHelper(puzzlePack, puzzleName);
		
		// Chargement des attributs du puzzle
		puzzleAttributes = new PuzzleAttributesReader().read("puzzles/" + puzzlePack + "/" + puzzleName + ".properties");
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
	
	public void selectLetter(GraphNode button) {
		// Récupère la lettre sélectionnée et la dernière lettre actuelle
		String currentWord = currentSuggestion.getText().toString();
		String last = currentWord.isEmpty() ? "" : currentWord.substring(currentWord.length() - 1);
		String selected = button.getText().toString();

		// Récupère le lien entre les lettres
		if (!last.isEmpty()) {
			GraphEdge edge = graph.getEdge(last, selected, false);
			if (edge == null) {
				// Aucun lien n'existe entre les 2 lettres
				// Désélectionne le bouton si la lettre n'est pas déjà dans le mot
				//DBGbutton.setChecked(currentWord.contains(selected));
				setSelectedStyle(button, currentWord.contains(selected));
				return;
			}
			
			// Met en évidence le lien
			edge.setHighlighted(true);
		}
		// Sélectionne le bouton de cette lettre
		setSelectedStyle(button, true);
		
		// Ajoute la lettre au mot courant
		currentSuggestion.setText(currentWord + selected);
		
		// Lance une jolie animation qui "envoie" la lettre vers le libellé de suggestion
		pendingLetters.add(selected);
		final TextButton copy = new TextButton(button.getText().toString(), button.getStyle());
		copy.setBounds(
			button.getX() + graph.getX(),
			button.getY() + graph.getY(),
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
							cancelButton.setDisabled(false);
						}
						return true;
					}
				}
			)
		);
		
		// Fait un peu disparaître et désactive les lettres qui ne sont pas connectées
		for (GraphNode node : graph.getNodes()) {
			if (node != button && graph.getEdge(node.getText().toString(), selected, false) == null) {
				node.setColor(1, 1, 1, 0.35f);
				node.setDisabled(true);
			} else {
				node.setColor(1, 1, 1, 1f);
				node.setDisabled(false);
			}
		}
	}
	
	/**
	 * Applique le style selected ou normal au bouton indiqué
	 * @param button
	 * @param contains
	 */
	private void setSelectedStyle(GraphNode button, boolean selected) {
		if (selected) {
			button.setStyle(puzzleButtonDecorator.getSelectedStyle());
		} else {
			button.setStyle(puzzleButtonDecorator.getDefaultStyle());
		}
	}

	/**
	 * Valide le mot sélectionné
	 */
	private void validateWord() {
		// Vérification de la validité du mot sélectionné
		final String suggestion = currentSuggestion.getText().toString();
		Riddle riddle = puzzleAttributes.getRiddle(suggestion);
		
		// Si le mot est valide, c'est cool !
		if (riddle != null && !riddle.isFound()) {
			// L'enigme est trouvée !
			puzzlePreferences.setSolutionFound(suggestion, true);
			riddle.setFound(true);
			final Label label = solutionLabels.get(suggestion);
			if (label != null) {
				// Envoie la suggestion vers le label de solution
				LabelStyle style = new LabelStyle(suggestionLabel.getStyle());
				style.background = null;
				final Label copy = new Label(suggestion, style);
				copy.setPosition(suggestionLabel.getX(), suggestionLabel.getY());
				stage.addActor(copy);
				copy.addAction(Actions.sequence(
						Actions.moveTo(
							label.getX(),
							label.getY(),
							0.6f,
							Interpolation.circleOut),
						new Action() {
							@Override
							public boolean act(float delta) {
								if (!copy.hasParent()) {
									// On ne gère l'action que si le label est toujours dans le graphe
									return true;
								}
								// Suppression de la lettre copiée
								copy.setVisible(false);
								copy.remove();
								
								// Ajoute la solution
								label.setText(suggestion);
								return true;
							}
						}
					)
				);
			}
			
			// Suppression des liens utilisés
			List<String> hidden = new ArrayList<String>();
			for (GraphEdge edge : graph.getEdges()) {
				if (edge.isHighlighted()) {
					edge.setVisible(false);
					hidden.add(edge.getName());
				}
			}
			puzzlePreferences.setEdgeVisible(hidden, false);
			
			// Suppression des lettres isolées
			graph.hideIsolatedNodes();
			
			// Désactivation des lettres mises en évidence
			graph.highlightAllNodes(false);
			
			// Ajout des points
			scoreBoard.addRightSuggestionSeries();
			scoreBoard.updateScore(riddle);			
		} else {
			scoreBoard.badSuggestion();
		}
		
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
	 * Affiche l'image indiquant que le puzzle est terminé, et cache les
	 * composants permettant de saisir une suggestion
	 */
	private void displayFinishImage() {
		suggestionLabel.setVisible(false);
		validateButton.setVisible(false);
		cancelButton.setVisible(false);
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
			graph.setVisible(false);
			
			MessageBoxUtils.showConfirm(
				"Quitter le puzzle ?",
				stage,
				new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						// Enregistre le temps actuel
						puzzlePreferences.setElapsedTime(chrono.getTime());
						
						// Retourne à l'écran de pack
						backToPackScreen();
					}
				},
				new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						// Redémarre le compteur et raffiche le graphe
						chrono.start();
						graph.setVisible(true);
					}
				});
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
	private void cancelWord() {
		// Raz du mot sélectionné
		suggestionLabel.setText("");
		currentSuggestion.setText("");
		pendingLetters.clear();
		validateButton.setDisabled(true);
		cancelButton.setDisabled(true);
		
		// Raz des liens sélectionnés
		graph.highlightAllEdges(false);
		
		// Raz des lettres sélectionnées
		// DBGgraph.selectAllNodes(false);
		
		// Raz des lettres non joignables (alpha 0.5
		for (GraphNode node : graph.getNodes()) {
			node.setColor(1, 1, 1, 1f);
			node.setDisabled(false);
			setSelectedStyle(node, false);
		}
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		graph.dispose();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Mise à jour des acteurs
        stage.act(Gdx.graphics.getDeltaTime());
        
        // Mise à jour du compteur de temps
        chrono.update(delta);
        
        // Dessin des autres composants du stage (label, boutons, noeuds...)
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
		}
		// Dans tous les cas, l'écran actuel doit récupérer les input
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}
}
