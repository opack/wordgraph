package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleNode;

public abstract class NodeObstacle extends Obstacle {
	private PuzzleNode node;
	private Image image;

	public NodeObstacle(ObstaclesTypes type, String target) {
		super(type, target);
	}
	
	@Override
	public void puzzleLoaded(PuzzleGraph graph, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(graph, puzzleAttributes, stage, puzzlePreferences);
		// Récupération du noeud ciblé
		node = graph.getNode(getTarget());
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqué comme inactif dans les préférences
		setActive(node != null && isActive());
		if (isActive()) {
			node.addObstacle(this);
		}
		// Application de l'effet de l'obstacle
		applyEffect(graph);
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
