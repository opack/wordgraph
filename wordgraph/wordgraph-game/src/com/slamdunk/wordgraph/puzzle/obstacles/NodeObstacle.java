package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.graph.Graph;
import com.slamdunk.wordgraph.puzzle.graph.GraphLink;
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
		// Récupération du noeud ciblé
		node = graph.getNode(getTarget());
		// Activation de l'obstacle s'il y a bien un noeud et que l'obstacle n'est pas
		// marqué comme inactif dans les préférences
		setActive(node != null && readPreferenceObstacleActive());
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



	@Override
	public void linkUsed(GraphLink link) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nodeHidden(GraphNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void wordValidated(String word) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyEffect(Graph graph) {
		// TODO Auto-generated method stub

	}

}
