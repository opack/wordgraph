package com.slamdunk.wordgraph.puzzle.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.slamdunk.wordgraph.TextButtonDecorator;
import com.slamdunk.wordgraph.puzzle.parsing.GMLParser;
import com.slamdunk.wordgraph.puzzle.parsing.GraphObject;

/**
 * Graphe complet, contenant les noeuds et les liens
 */
public class Graph extends Group {
	private ShapeRenderer shapeRenderer;
	private List<GraphNode> nodes;
	private List<GraphEdge> edges;
	private boolean centerGraph;
	private TextButtonDecorator buttonDecorator;
	
	public Graph() {
		shapeRenderer = new ShapeRenderer();
		nodes = new ArrayList<GraphNode>();
		edges = new ArrayList<GraphEdge>();
	}
	
	/**
	 * Charge le GML du puzzle et crée le graphe correspondant
	 * @param file
	 */
	public void load(String file, TextButtonDecorator decorator) {
		buttonDecorator = decorator;
		
		// Nettoie le graphe, au cas où
		nodes.clear();
		edges.clear();
		clearChildren();
		
		// Ouverture et parsing du fichier puzzle
		GraphObject root = new GMLParser().parse(file);
		GraphObject graph = root.getSingleObject("graph");
		
		Rectangle boundingBox = computeBoundingBox(graph);
		float oldWidth = getWidth();
		float oldHeight = getHeight();
		setWidth(boundingBox.width);
		setHeight(boundingBox.height);
		Vector2 offset = new Vector2(boundingBox.x, -boundingBox.y);
		
		// Création des noeuds
		Map<Integer, GraphNode> nodesById = createGraphNodes(graph, offset);
		
		// Création des liens
        createGraphEdges(graph, offset, nodesById);
        
        // Centre le graph dans le conteneur
        if (centerGraph) {
        	translate((oldWidth - getWidth()) / 2, (oldHeight - getHeight()) / 2);
        }
	}
	
	public boolean isCenterGraph() {
		return centerGraph;
	}

	public void setCenterGraph(boolean centerGraph) {
		this.centerGraph = centerGraph;
	}

	private Rectangle computeBoundingBox(GraphObject graph) {
		// Détermine la boundingBox en coordonnées GML (0;0 en haut)
		float minX = 0;
		float minY = 0;
		float maxX = 0;
		float maxY = 0;
		float curX = 0;
		float curY = 0;
		float curW = 0;
		float curH = 0;
		boolean isFirstNode = true;
		for (GraphObject graphNode : graph.getListObject("node")) {
			GraphObject graphics = graphNode.getSingleObject("graphics");
			
			curW = graphics.getFloat("w");
			curH = graphics.getFloat("h");
			// On retire une demie-longueur et largeur car le GML indique le centre de l'objet
			curX = graphics.getFloat("x") - curW / 2;
			curY = graphics.getFloat("y") - curH / 2;
			
			if (isFirstNode) {
				minX = curX;
				minY = curY;
				maxX = curX;
				maxY = curY;
				isFirstNode = false;
			} else {
				if (curX < minX) {
					minX = curX;
				}
				if (curX + curW > maxX) {
					maxX = curX + curW;
				}
				if (curY < minY) {
					minY = curY;
				}
				if (curY + curH > maxY) {
					maxY = curY + curH;
				}
			}
		}
		
		for (GraphObject graphEdge : graph.getListObject("edge")) {
			GraphObject graphics = graphEdge.getSingleObject("graphics");
			GraphObject line = graphics.getSingleObject("Line");
			if (line == null) {
				// S'il n'y a pas de ligne définie, alors c'est que le lien est
				// entre le centre de deux noeuds. Dans ce cas, le lien est
				// forcément à l'intérieur de la boundingBox actuelle.
				// Inutile de calculer.
				continue;
			}
			List<GraphObject> points = line.getListObject("point");
			if (points != null && !points.isEmpty()) {
				for (GraphObject point : points) {
					curX = point.getFloat("x");
					curY = point.getFloat("y");
					
					if (curX < minX) {
						minX = curX;
					}
					if (curX > maxX) {
						maxX = curX;
					}
					if (curY < minY) {
						minY = curY;
					}
					if (curY > maxY) {
						maxY = curY;
					}
				}
			}
		}
		
		Rectangle boundingBox = new Rectangle();
		boundingBox.width = maxX - minX;
		boundingBox.height = maxY - minY;
		boundingBox.x = minX;
		boundingBox.y = minY;
		return boundingBox;
	}

	/**
	 * Crée les noeuds du graphe en boutons
	 * @param graph
	 */
	private Map<Integer, GraphNode> createGraphNodes(GraphObject graph, Vector2 offset) {
		// Création des noeuds
		Map<Integer, GraphNode> nodesById = new HashMap<Integer, GraphNode>();
		final float graphHeight = getHeight();
		for (GraphObject graphNode : graph.getListObject("node")) {
			String label = graphNode.getString("label");
			// On n'ajoute pas le node comportant les dimensions
			if ("bounds".equals(label)) {
				continue;
			}
			GraphObject graphics = graphNode.getSingleObject("graphics");
			
			GraphNode btnNode = new GraphNode(label, buttonDecorator.getDefaultStyle());
			buttonDecorator.decorate(label, btnNode);
			
			btnNode.setWidth(graphics.getFloat("w"));
			btnNode.setHeight(graphics.getFloat("h"));
			// On retire/ajoute une demie-largeur/heuteur car le GML indique le centre du noeud 
			btnNode.setX(graphics.getFloat("x") - btnNode.getWidth() / 2 - offset.x);
			btnNode.setY(graphHeight - graphics.getFloat("y") - btnNode.getHeight() / 2 - offset.y);
			
			// Ajout du noeud au stage et au graphe
			addActor(btnNode);
			nodes.add(btnNode);
			
			// On garde les noeuds associés à leur id de côté pour les liens
			nodesById.put(graphNode.getInt("id"), btnNode);
		}
		
		return nodesById;
	}
	
	/**
	 * Crée les liens du graphe
	 * @param root
	 */
	private void createGraphEdges(GraphObject graph, Vector2 offset, Map<Integer, GraphNode> nodesById) {
		for (GraphObject graphEdge : graph.getListObject("edge")) {
			// Récupération des deux noeuds
			GraphNode sourceNode = nodesById.get(graphEdge.getInt("source"));
			GraphNode targetNode = nodesById.get(graphEdge.getInt("target"));
			
			// Création du lien entre les noeuds
			GraphEdge edge = new GraphEdge(graphEdge.getString("label"), sourceNode.getText().toString(), targetNode.getText().toString());
			sourceNode.addEdge(edge);
			targetNode.addEdge(edge);
					
			// Récupération des points de la ligne
			GraphObject graphics = graphEdge.getSingleObject("graphics");
			GraphObject line = graphics.getSingleObject("Line");
			List<GraphObject> points = null;
			if (line != null) {
				points = line.getListObject("point");
			}
			if (points == null || points.isEmpty()) {
				// S'il n'y a aucun point, on relie les 2 centres
				edge.addPoint(getCenterOf(sourceNode));
				edge.addPoint(getCenterOf(targetNode));
			} else {
				final float graphHeight = getHeight();
				for (GraphObject point : points) {
					edge.addPoint(point.getFloat("x") - offset.x, graphHeight - point.getFloat("y") - offset.y);
				}
			}
			
			// Si la ligne comporte des points, la dessine
			if (edge.length() > 1) {
				// Ajustement des extrémités sur les ancres
				GraphObject anchor = graphEdge.getSingleObject("edgeAnchor");
				if (anchor != null) {
					// Décale les extrémités sur les ancres
					Vector2 source = edge.getPoint(0);
					source.x += anchor.getFloat("xSource", 0) * (sourceNode.getWidth() / 2);
					source.y -= anchor.getFloat("ySource", 0) * (sourceNode.getHeight() / 2);
					Vector2 target = edge.getPoint(edge.length() - 1);
					target.x += anchor.getFloat("xTarget", 0) * (targetNode.getWidth() / 2);
					target.y -= anchor.getFloat("yTarget", 0) * (targetNode.getHeight() / 2);
				}
				
				addActor(edge);
				edges.add(edge);
			}
		}
	}

	/**
	 * Méthode utilitaire qui retourne le centre d'un acteur
	 * @param actor
	 * @return
	 */
	private Vector2 getCenterOf(Actor actor) {
		return new Vector2(actor.getX() + actor.getWidth() / 2, actor.getY() + actor.getHeight() / 2);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		// Met en pause le batch pour dessiner les edges en premier
		batch.end();
		
		// Dessine les liens
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        drawEdges();
        
        // Reprend le batch et dessine les noeuds
        batch.begin();
		super.draw(batch, parentAlpha);
	}
	
	/**
	 * Dessine les lignes.
	 */
	public void drawEdges() {
		// Dessin des lignes
		//DBGGdx.gl.glEnable(GL10.GL_LINE_SMOOTH);
        for (GraphEdge edge : edges) {
        	edge.draw(shapeRenderer);
        }
	}
	
	public List<GraphNode> getNodes() {
		return nodes;
	}

	public List<GraphEdge> getEdges() {
		return edges;
	}

	/**
	 * Définit la propriété highlighted de tous les liens
	 * @param b
	 */
	public void highlightAllEdges(boolean isHighlighted) {
		for (GraphEdge edge : edges) {
			edge.setHighlighted(isHighlighted);
		}
	}
	
	/**
	 * Définit la propriété highlighted de tous les noeuds
	 * @param highlighted
	 */
	public void highlightAllNodes(boolean highlighted) {
		TextButtonStyle style = buttonDecorator.getDefaultStyle();
		if (highlighted) {
			style = buttonDecorator.getHighlightedStyle();
		}
		for (GraphNode node : nodes) {
			node.setStyle(style);
			node.setChecked(highlighted);
		}
	}
	
// DBG
//	/**
//	 * Définit la propriété highlighted de tous les liens
//	 * @param selected
//	 */
//	public void selectAllNodes(boolean selected) {
//		for (GraphNode node : nodes) {
//			node.setChecked(selected);
//		}
//	}
	
	/**
	 * Retourne le premier (éventuellement non sélectionné) lien entre 2 lettres.
	 * Cette méthode ne regarde pas si le lien est visible ou sélectionné
	 * @see #getEdge(String, String, boolean)
	 * @param lettre1
	 * @param lettre2
	 * @return
	 */
	public GraphEdge getEdge(String letter1, String letter2) {
		for (GraphEdge edge : edges) {
			if (edge.isBetween(letter1, letter2)) {
				return edge;
			}
		}
		return null;
	}
	
	/**
	 * Retourne le premier lien entre 2 lettres qui est visible et sélectionné ou non
	 * @param lettre1
	 * @param lettre2
	 * @param hightlighted
	 * @return
	 */
	public GraphEdge getEdge(String letter1, String letter2, boolean hightlighted) {
		for (GraphEdge edge : edges) {
			if (edge.isVisible() && edge.isBetween(letter1, letter2) && edge.isHighlighted() == hightlighted) {
				return edge;
			}
		}
		return null;
	}
	
	@Override
	public void setBounds(float x, float y, float width, float height) {
		float offsetX = x - getX();
		float offsetY = y - getY();
		super.setBounds(x, y, width, height);
		translateEdges(offsetX, offsetY);
	}
	
	@Override
	public void setPosition(float x, float y) {
		float offsetX = x - getX();
		float offsetY = y - getY();
		super.setPosition(x, y);
		translateEdges(offsetX, offsetY);
	}
	
	@Override
	public void setX(float x) {
		float offsetX = x - getX();
		super.setX(x);
		translateEdges(offsetX, 0);
	}
	
	@Override
	public void setY(float y) {
		float offsetY = y - getY();
		super.setY(y);
		translateEdges(0, offsetY);
	}
	
	@Override
	public void translate(float x, float y) {
		super.translate(x, y);
		translateEdges(x, y);
	}
	
//	@Override
//	public void setSize(float width, float height) {
//		// Les dimensions du graph sont modifiées. Dans ce cas, on s'assure
//		// qu'il reste au centre
//		float offsetX = (width - getWidth()) / 2;
//		float offsetY = (height - getHeight()) / 2;
//		super.setSize(width, height);
//		offsetNodesAndEdges(offsetX, offsetY);
//	}

	private void translateEdges(float offsetX, float offsetY) {
		for (GraphEdge edge : edges) {
			edge.translate(offsetX, offsetY);
		}
	}
	
	public void dispose() {
		shapeRenderer.dispose();
	}

	/**
	 * Décale les noeuds et liens du graph
	 * @param offsetX
	 * @param offsetY
	 */
	public void offsetNodesAndEdges(float offsetX, float offsetY) {
		for (GraphNode node : nodes) {
			node.translate(offsetX, offsetY);
		}
		for (GraphEdge edge : edges) {
			edge.translate(offsetX, offsetY);
		}
	}
	
	/**
	 * Cache les noeuds qui n'ont plus aucun lien avec d'autres noeuds
	 */
	public void hideIsolatedNodes() {
		for (GraphNode node : nodes) {
			if (node.isIsolated()) {
				node.setVisible(false);
			}
		}
	}

	/**
	 * Retourne le noeud contenant la lettre indiquée
	 * @return
	 */
	public GraphNode getNode(String letter) {
		if (letter != null) {
			for (GraphNode node : nodes) {
				if (letter.equals(node.getName())) {
					return node;
				}
			}
		}
		return null;
	}

	public void highlightNode(String letter) {
		GraphNode node = getNode(letter);
		if (node != null) {
			node.setStyle(buttonDecorator.getHighlightedStyle());
		}
	}
}
