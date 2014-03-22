package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

/**
 * Remplace un indice par une cat�gorie
 */
public class CategoryObstacle extends ClueObstacle{
	private String category;
	
	public CategoryObstacle(String target, String category) {
		super(ObstaclesTypes.CATEGORY, target);
		this.category = category;
	}

	@Override
	public void applyEffect(Graph graph) {
		Label label = getLabel();
		if (label != null) {
			if (isActive()) {
				label.setText("Cat�gorie : " + category);
			} else {
				label.setText(getRiddle().getClue());
			}
		}
	}
	
	@Override
	public void wordValidated(String word) {
		// Si le mot valid� est celui de cet obstacle, alors l'obstacle dispara�t
		if (word.equals(getRiddle().getSolution())) {
			setActive(false);
			applyEffect(null);
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
