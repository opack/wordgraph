package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphNode;

public abstract class NodeObstacle extends Obstacle {
	private GraphNode node;
	private Image image;

	public NodeObstacle(ObstaclesTypes type, String target) {
		super(type, target);
	}
	
	@Override
	public void graphLoaded(Graph graph, PuzzlePreferencesHelper puzzlePreferences) {
		super.graphLoaded(graph, puzzlePreferences);
		// R�cup�ration du noeud cibl�
		node = graph.getNode(getTarget());
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqu� comme inactif dans les pr�f�rences
		setActive(node != null && readPreferenceObstacleActive());
		if (isActive()) {
			node.addObstacle(this);
		}
		// Application de l'effet de l'obstacle
		applyEffect(graph);
	}
	
	public GraphNode getNode() {
		return node;
	}

	public Image getImage() {
		return image;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}

	public void createImage(String regionName) {
		image = new Image(Assets.defaultPuzzleSkin.getDrawable(regionName));
		node.addActor(image);
		image.setZIndex(0);
		image.setPosition((node.getWidth() - image.getWidth()) / 2, (node.getHeight() - image.getHeight()) / 2);
	}
}
