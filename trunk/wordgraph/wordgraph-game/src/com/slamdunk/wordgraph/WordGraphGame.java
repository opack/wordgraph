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
		// On indique � libGDX qu'on va g�rer nous-m�me la touche back
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
		// Si l'application est redimensionn�e, on
		// redimensionne tous les �crans
		main.resize(480, 800);
		packList.resize(480, 800);
		pack.resize(480, 800);
		puzzle.resize(480, 800);
		joker.resize(480, 800);
	}
	
	/**
	 * Affiche l'�cran principal
	 */
	public void showMainScreen() {
		setScreen(main);
	}
	
	/**
	 * Affiche l'�cran de s�lection de pack de puzzles
	 */
	public void showPackListScreen() {
		setScreen(packList);
	}
	
	/**
	 * Affiche l'�cran de s�lection de puzzle
	 * @param pack
	 */
	public void showPackScreen(String name) {
		pack.setPackName(name);
		setScreen(pack);
	}
	
	/**
	 * Affiche l'�cran puzzle pour jouer au puzzle indiqu�
	 * @param puzzleToPlay
	 */
	public void showPuzzleScreen(String pack, String puzzleName) {
		puzzle.setPuzzleToPlay(pack, puzzleName);
		setScreen(puzzle);
	}
	
	/**
	 * Affiche l'�cran puzzle dans son dernier �tat
	 */
	public void showPuzzleScreen() {
		setScreen(puzzle);
	}

	/**
	 * Affiche l'�cran de s�lection de joker
	 */
	public void showJokerScreen(PuzzleAttributes puzzleAttributes, PuzzleGraph graph) {
		joker.setGraph(graph);
		joker.setPuzzleAttributes(puzzleAttributes);
		setScreen(joker);
	}
}
