package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;

/**
 * Remplace un indice par une cat�gorie
 */
public class CategoryObstacle extends ClueObstacle{
	private LabelStyle originalStyle;
	private String category;
	
	public CategoryObstacle(String target, String category) {
		super(ObstaclesTypes.CATEGORY, target);
		this.category = category;
	}

	@Override
	public void applyEffect(PuzzleGraph graph) {
		Label label = getLabel();
		if (label != null) {
			if (isActive()) {
//				if (getImage() == null) {
//					createImage("obstacle-category", false);
//				}
				if (originalStyle == null) {
					originalStyle = label.getStyle();
				}
				LabelStyle newStyle = new LabelStyle(originalStyle);
				newStyle.background = Assets.defaultPuzzleSkin.getDrawable("obstacle-category");
				label.setStyle(newStyle);
				label.setText(category);
				label.setAlignment(Align.center);
			} else {
//				if (getImage() != null) {
//					getImage().remove();
//				}
				label.setStyle(originalStyle);
				label.setText(getRiddle().getClue());
				label.setAlignment(Align.left);
			}
		}
	}
	
	@Override
	public void wordValidated(String word) {
		// Si le mot valid� est celui de cet obstacle, alors l'obstacle dispara�t
		if (word.equals(getRiddle().getSolution())) {
			setActive(false);
			applyEffect(null);
			writePreferenceObstacleActive(false);
		}
	}
	
	/**
	 * Cr�e un CategoryObstacle initialis� avec les donn�es lues dans le fichier
	 * properties d�crivant le puzzle. Ces donn�es ont la forme suivante :
	 * [idx]|[category] avec :
	 *   [idx] : indice de l'enigme vis�e
	 *   [category] : cat�gorie � afficher � la place de l'indice
	 * Exemple :
	 * 	- "1|m�tier" : obstacle sur l'�nigme n�1 (donc la 2�), rempla�ant l'indice par "m�tier"
	 * @param propertiesDescription
	 * @return
	 */
	public static CategoryObstacle createFromProperties(String propertiesDescription) {
		String[] parameters = propertiesDescription.split("\\|");
		if (parameters.length != 2) {
			throw new IllegalArgumentException("CategoryObstacle : Failure to split '" + propertiesDescription + "' in the 2 required parts.");
		}
		return new CategoryObstacle(parameters[0], parameters[1]);
	}
}
