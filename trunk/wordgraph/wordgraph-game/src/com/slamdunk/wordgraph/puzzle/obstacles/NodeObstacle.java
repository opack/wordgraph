package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.LetterStates;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleButtonDecorator;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

public abstract class NodeObstacle extends Obstacle {
	private PuzzleNode node;
	private Image image;

	public NodeObstacle(ObstaclesTypes type, String target) {
		super(type, target);
	}
	
	@Override
	public void applyEffect(PuzzleGraph graph) {
		// Lorsque l'effet doit être appliqué, on change le style
		// du bouton pour que son image reflète l'obstacle
		PuzzleButtonDecorator.getInstance().setStyle(node, LetterStates.NORMAL);
	}
	
	@Override
	public void puzzleLoaded(PuzzleGraph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(graph, puzzleAttributes, stage, puzzlePreferences);
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqué comme inactif dans les préférences
		setActive(node != null && isActive());
		if (isActive()) {
			node.addObstacle(this);
		}
		// Application de l'effet de l'obstacle
		applyEffect(graph);
	}
	
	@Override
	public void graphLoaded(PuzzleGraph graph) {
		super.graphLoaded(graph);
		// Récupération du noeud ciblé
		node = graph.getNode(getTarget());
	}
	
	public PuzzleNode getNode() {
		return node;
	}

	public Image getImage() {
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}

	public void createImage(String regionName) {
		TextButton button = node.getButton();
		if (button == null) {
			return;
		}
		image = new Image(Assets.defaultPuzzleSkin.getDrawable(regionName));
		button.addActor(image);
		image.setZIndex(0);
		image.setPosition(
			(button.getWidth() - image.getWidth()) / 2,
			(button.getHeight() - image.getHeight()) / 2);
	}
}
