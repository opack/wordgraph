package com.slamdunk.wordgraph.puzzle.graph;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class GraphEdge extends Actor {
	private static final int THICKNESS_HIGHLIGHTED = 5;
	private static final Color COLOR_HIGHLIGHTED = new Color(255 / 255f, 204 / 255f, 0 / 255f, 1 / 255f);
	private static final int THICKNESS_NORMAL = 3;
	private static final Color COLOR_NORMAL = new Color(118 / 255f, 113 / 255f, 100 / 255f, 1 / 255f);
	
	private String sourceLetter;
	private String targetLetter;
	
	private List<Vector2> points;
	
	private boolean highlighted;
	private boolean visible;
	
	public GraphEdge(String id, String sourceLetter, String targetLetter) {
		// Les liens ne peuvent pas être touchés
		setTouchable(Touchable.disabled);
		
		this.sourceLetter = sourceLetter;
		this.targetLetter = targetLetter;
		if (id == null || id.isEmpty()) {
			setName(sourceLetter + targetLetter);
		} else {
			setName(id);
		}
		
		visible = true;
	}
	
	public boolean isBetween(String letter1, String letter2) {
		if (letter1 == null || letter2 == null) {
			return false;
		}
		return (letter1.equals(sourceLetter) && letter2.equals(targetLetter))
			|| (letter1.equals(targetLetter) && letter2.equals(sourceLetter));
	}
	
	public void addPoint(float x, float y) {
		addPoint(new Vector2(x, y));
	}
	

	public void addPoint(Vector2 point) {
		if (points == null) {
			points = new ArrayList<Vector2>();
		}
		points.add(point);
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		if (points == null
		|| points.size() < 2
		|| !visible) {
			return;
		}
//DBG		if (!highlighted) {
//			return;
//		}
		shapeRenderer.begin(ShapeType.Line);
		if (highlighted) {
			Gdx.gl10.glLineWidth(THICKNESS_HIGHLIGHTED);
			shapeRenderer.setColor(COLOR_HIGHLIGHTED);
		} else {
			Gdx.gl10.glLineWidth(THICKNESS_NORMAL);
			shapeRenderer.setColor(COLOR_NORMAL);
		}
		Vector2 point1 = points.get(0);
		Vector2 point2 = null;
		for (int index = 1; index < points.size(); index ++) {
			point2 = points.get(index);
			shapeRenderer.line(point1.x, point1.y, point2.x, point2.y);
			point1 = point2;
		}
		shapeRenderer.end();
	}

	/**
	 * Retourne le nombre de points de la ligne
	 * @return
	 */
	public int length() {
		if (points == null) {
			return 0;
		}
		return points.size();
	}

	public Vector2 getPoint(int index) {
		if (points == null) {
			return null;
		}
		return points.get(index);
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Modifie les coordonnées des points de la ligne pour qu'ils prennent tous
	 * en compte les coordonnées de la boîte dans laquelle ils sont définis.
	 * Sans cette méthode, toutes les coordonnées sont relatives à XY 0;0.
	 * Cette méthode ajuste également la taille de l'Actor.
	 * @param x
	 * @param y
	 */
	@Override
	public void translate(float offsetX, float offsetY) {
		Vector2 min = new Vector2(points.get(0));
		Vector2 max = new Vector2(points.get(0));
		for (Vector2 point : points) {
			// Ajuste la position du point
			point.x += offsetX;
			point.y += offsetY;
			
			// Ajuste les bounds
			if (point.x < min.x) {
				min.x = point.x;
			}
			if (point.y < min.y) {
				min.y = point.y;
			}
			if (point.x > max.x) {
				max.x = point.x;
			}
			if (point.y > max.y) {
				max.y = point.y;
			}
		}
		setBounds(min.x, min.y, max.x - min.x, max.y - min.y);
	}
	
	/**
	 * Flip verticalement les points autour de l'ordonnée indiquée
	 * @param y
	 */
	public void flipVertical(float ordinate) {
		for (Vector2 point : points) {
			point.y = 2 * ordinate - point.y;
		}
	}

	public List<Vector2> getPoints() {
		return points;
	}
}
