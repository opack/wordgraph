package com.slamdunk.wordgraph.puzzle.graph;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class GraphEdge extends Group {
	private static final int THICKNESS_HIGHLIGHTED = 5;
//	private static final Color COLOR_HIGHLIGHTED = new Color(255 / 255f, 204 / 255f, 0 / 255f, 1 / 255f);
	private static final int THICKNESS_NORMAL = 3;
//	private static final Color COLOR_NORMAL = new Color(118 / 255f, 113 / 255f, 100 / 255f, 1 / 255f);
	
	private String sourceLetter;
	private String targetLetter;
	
	private List<Vector2> points;
	
	/**
	 * Indique si le lien est mis en surbrillance, typiquement
	 * lorsque les lettres qu'il connecte sont sélectionnées.
	 */
	private boolean highlighted;
	/**
	 * Indique si le lien est visible. Il peut être invisible
	 * s'il a déjà été utilisé dans un mot (@see #used) ou
	 * si un obstacle le masque.
	 */
	//private boolean visible;
	/**
	 * Indique si le lien a déjà été utilisé dans un mot.
	 */
	private boolean used;
	
	private Drawable normalDrawable;
	private Drawable highlightedDrawable;
	
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
		
		//visible = true;
	}
	
	public void setDrawables(Drawable normalDrawable, Drawable highlightedDrawable) {
		this.normalDrawable = normalDrawable;
		this.highlightedDrawable = highlightedDrawable;
		setHighlighted(highlighted);
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
//		if (points == null
//		|| points.size() < 2
//		|| !visible) {
//			return;
//		}
////DBG		if (!highlighted) {
////			return;
////		}
//		shapeRenderer.begin(ShapeType.Line);
//		if (highlighted) {
//			Gdx.gl10.glLineWidth(THICKNESS_HIGHLIGHTED);
//			shapeRenderer.setColor(COLOR_HIGHLIGHTED);
//		} else {
//			Gdx.gl10.glLineWidth(THICKNESS_NORMAL);
//			shapeRenderer.setColor(COLOR_NORMAL);
//		}
//		Vector2 point1 = points.get(0);
//		Vector2 point2 = null;
//		for (int index = 1; index < points.size(); index ++) {
//			point2 = points.get(index);
//			shapeRenderer.line(point1.x, point1.y, point2.x, point2.y);
//			point1 = point2;
//		}
//		shapeRenderer.end();
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
		
		// Modifie l'image associée aux traits
		final Drawable newDrawable = highlighted ? highlightedDrawable : normalDrawable;
		for (Actor child : getChildren()) {
			((Image)child).setDrawable(newDrawable);
		}
	}

//	public boolean isVisible() {
//		return visible;
//	}
//
//	public void setVisible(boolean visible) {
//		this.visible = visible;
//		for (Actor child : getChildren()) {
//			child.setVisible(visible);
//		}
//	}
	

// DBG Inutile de faire les translations si les liens sont des enfants
//	/**
//	 * Modifie les coordonnées des points de la ligne pour qu'ils prennent tous
//	 * en compte les coordonnées de la boîte dans laquelle ils sont définis.
//	 * Sans cette méthode, toutes les coordonnées sont relatives à XY 0;0.
//	 * Cette méthode ajuste également la taille de l'Actor.
//	 * @param x
//	 * @param y
//	 */
//	@Override
	public void translate(float offsetX, float offsetY) {
//		Vector2 min = new Vector2(points.get(0));
//		Vector2 max = new Vector2(points.get(0));
//		for (Vector2 point : points) {
//			// Ajuste la position du point
//			point.x += offsetX;
//			point.y += offsetY;
//			
//			// Ajuste les bounds
//			if (point.x < min.x) {
//				min.x = point.x;
//			}
//			if (point.y < min.y) {
//				min.y = point.y;
//			}
//			if (point.x > max.x) {
//				max.x = point.x;
//			}
//			if (point.y > max.y) {
//				max.y = point.y;
//			}
//		}
//		setBounds(min.x, min.y, max.x - min.x, max.y - min.y);
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

	public String getSourceLetter() {
		return sourceLetter;
	}

	public String getTargetLetter() {
		return targetLetter;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	/**
	 * Recalcule la position et l'angle de rotation des images de ce lien
	 */
	public void computeImages() {
		clearChildren();
		
		Vector2 min = null;
		Vector2 max = null;
		
		// L'épaisseur du trait est fixée
		final float thickness = highlighted ? THICKNESS_HIGHLIGHTED : THICKNESS_NORMAL;
		final float halfThickness = thickness / 2;
		final int nbPoints = points.size();
		Vector2 tmp;
		for (int curPoint = 1; curPoint < nbPoints; curPoint++) {
			Vector2 A = points.get(curPoint - 1);
			Vector2 B = points.get(curPoint);
			
			// Mise à jour des limites du lien
			if (min == null) {
				min = new Vector2(A);
				max = new Vector2(A);
			}
			if (A.x < min.x) {
				min.x = A.x;
			}
			if (B.x < min.x) {
				min.x = B.x;
			}
			if (A.y < min.y) {
				min.y = A.y;
			}
			if (B.y < min.y) {
				min.y = B.y;
			}
			if (A.x > max.x) {
				max.x = A.x;
			}
			if (B.x > max.x) {
				max.x = B.x;
			}
			if (A.y > max.y) {
				max.y = A.y;
			}
			if (B.y > max.y) {
				max.y = B.y;
			}
			
			// Cherche le point le plus proche de l'origine
			if (B.dst2(0, 0) < A.dst2(0, 0)) {
				tmp = A;
				A = B;
				B = tmp;
			}
			
			// Détermine la taille du trait
			float length = A.dst(B);
			
			// Détermine l'angle du trait
			// SOH-CAH-TOAH -> cos(a) = AC/AB, si A et B sont les 2 points et C
			// la projection de B sur l'axe des abscisses.
			// Or AC = xC - xA (les 2 sont sur les abscisses) = xB - xA (car C
			// est la projection de B sur x), et AB = width.
			float angleRad = (float)Math.acos((B.x - A.x) / length);
			float angleDeg = angleRad * MathUtils.radiansToDegrees;
			if (A.y > B.y) {
				// Si le premier point est au-dessus du second, alors l'angle est négatif
				angleDeg *= -1;
			}
			
			Image image = new Image(normalDrawable);
			image.setBounds(A.x - halfThickness, A.y - halfThickness, length, thickness);
			image.rotate(angleDeg);
			addActor(image);
		}
		// Réajuste la taille et la position de l'objet GraphEdge et des images contenues
		setBounds(min.x, min.y, max.x - min.x, max.y - min.y);
		for (Actor child : getChildren()) {
			child.translate(- min.x, - min.y);
		}
	}
}
