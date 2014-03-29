package com.slamdunk.wordgraph;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.slamdunk.utils.PropertiesManager;
import com.slamdunk.wordgraph.joker.JokerScreen;
import com.slamdunk.wordgraph.pack.PackScreen;
import com.slamdunk.wordgraph.packlist.PackListScreen;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleScreen;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;

public class WordGraphGame extends Game {

	private MainScreen main;
	private PackListScreen packList;
	private PackScreen pack;
	private PuzzleScreen puzzle;
	private JokerScreen joker;

	@Override
	public void create() {
		// On indique à libGDX qu'on va gérer nous-même la touche back
		Gdx.input.setCatchBackKey(true);
		
		// D'abord, lire la configuration
		PropertiesManager.init("config");
		// Ensuite, charger les assets
		Assets.load();
		
		main = new MainScreen(this);
		packList = new PackListScreen(this);
		pack = new PackScreen(this);
		puzzle = new PuzzleScreen(this);
		joker = new JokerScreen(this);
		
		showMainScreen();
	}
	
	@Override
	public void resize(int width, int height) {
		// Si l'application est redimensionnée, on
		// redimensionne tous les écrans
		main.resize(480, 800);
		packList.resize(480, 800);
		pack.resize(480, 800);
		puzzle.resize(480, 800);
		joker.resize(480, 800);
	}
	
	/**
	 * Affiche l'écran principal
	 */
	public void showMainScreen() {
		setScreen(main);
	}
	
	/**
	 * Affiche l'écran de sélection de pack de puzzles
	 */
	public void showPackListScreen() {
		setScreen(packList);
	}
	
	/**
	 * Affiche l'écran de sélection de puzzle
	 * @param pack
	 */
	public void showPackScreen(String name) {
		pack.setPackName(name);
		setScreen(pack);
	}
	
	/**
	 * Affiche l'écran puzzle pour jouer au puzzle indiqué
	 * @param puzzleToPlay
	 */
	public void showPuzzleScreen(String pack, String puzzleName) {
		puzzle.setPuzzleToPlay(pack, puzzleName);
		setScreen(puzzle);
	}
	
	/**
	 * Affiche l'écran puzzle dans son dernier état
	 */
	public void showPuzzleScreen() {
		setScreen(puzzle);
	}

	/**
	 * Affiche l'écran de sélection de joker
	 */
	public void showJokerScreen(PuzzleAttributes puzzleAttributes, PuzzleGraph graph) {
		joker.setGraph(graph);
		joker.setPuzzleAttributes(puzzleAttributes);
		setScreen(joker);
	}
}
