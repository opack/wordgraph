package com.slamdunk.wordgraph.puzzle;

import static com.slamdunk.wordgraph.puzzle.LetterStates.JOKER;
import static com.slamdunk.wordgraph.puzzle.LetterStates.NORMAL;
import static com.slamdunk.wordgraph.puzzle.LetterStates.SELECTED;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.slamdunk.utils.DoubleEntryArray;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;
import com.slamdunk.wordgraph.puzzle.obstacles.Obstacle;
import com.slamdunk.wordgraph.puzzle.obstacles.ObstaclesTypes;


public class PuzzleButtonDecorator {
	private static PuzzleButtonDecorator instance;

	private DoubleEntryArray<LetterStates, ObstaclesTypes, TextButtonStyle> styles;
	
	public static void init(Skin skin) {
		instance = new PuzzleButtonDecorator(skin);
	}
	
	public static PuzzleButtonDecorator getInstance() {
		return instance;
	}
	
	private PuzzleButtonDecorator(final Skin skin) {
		styles = new DoubleEntryArray<LetterStates, ObstaclesTypes, TextButtonStyle>();
		registerStyles(null, skin, "puzzle-letter", "puzzle-letter-selected", "puzzle-letter-highlighted");
		registerStyles(ObstaclesTypes.FOG, skin, "obstacle-fog-normal", "obstacle-fog-selected", "obstacle-fog-joker");
		registerStyles(ObstaclesTypes.STONE, skin, "obstacle-stone-normal", "obstacle-stone-normal", "obstacle-stone-joker");
		registerStyles(ObstaclesTypes.INTRUDER, skin, "obstacle-intruder-normal", "obstacle-intruder-normal", "obstacle-intruder-normal");
		registerStyles(ObstaclesTypes.ISLE, skin, "obstacle-isle-normal", "obstacle-isle-selected", "obstacle-isle-joker");
	}
	
	public void registerStyles(ObstaclesTypes type, Skin skin, String normalStyleName, String selectedStyleName, String jokerStyleName) {
		styles.put(NORMAL, type, skin.get(normalStyleName, TextButtonStyle.class));
		styles.put(SELECTED, type, skin.get(selectedStyleName, TextButtonStyle.class));
		styles.put(JOKER, type, skin.get(jokerStyleName, TextButtonStyle.class));
	}

	/**
	 * Remet le style normal sur tous les boutons de lettre
	 * @param highlighted
	 */
	public void setNormalStyleOnAllNodes(PuzzleGraph graph) {
		for (PuzzleNode node : graph.getNodes()) {
			TextButton button = node.getButton();
			setStyle(node, LetterStates.NORMAL);
			button.setDisabled(false);			
		}
	}
	
	/**
	 * Applique le style correspondant � l'�tat de la lettre
	 * indiqu�. Le style choisi d�pend �galement des obstacles
	 * actifs sur le noeud : le premier obstacle actif donne son
	 * style au bouton.
	 * @param button
	 * @param contains
	 */
	public void setStyle(PuzzleNode node, LetterStates state) {
		TextButton button = node.getButton();
		List<Obstacle> obstacles = node.getObstacles();
		TextButtonStyle style = styles.get(state, null);
		if (obstacles != null) {
			for (Obstacle obstacle : node.getObstacles()) {
				if (obstacle.isObstacleDrawn()) {
					TextButtonStyle obstacleStyle = styles.get(state, obstacle.getType());
					if (obstacleStyle != null) {
						style = obstacleStyle;
						break;
					}
				}
			}
		}
		button.setStyle(style);
	}
}