package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

/**
 * Remplace un indice par une catégorie
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
				label.setText("Catégorie : " + category);
			} else {
				label.setText(getRiddle().getClue());
			}
		}
	}
	
	@Override
	public void wordValidated(String word) {
		// Si le mot validé est celui de cet obstacle, alors l'obstacle disparaît
		if (word.equals(getRiddle().getSolution())) {
			setActive(false);
			applyEffect(null);
		}
	}
	
	/**
	 * Crée un CategoryObstacle initialisé avec les données lues dans le fichier
	 * properties décrivant le puzzle. Ces données ont la forme suivante :
	 * [idx]|[category] avec :
	 *   [idx] : indice de l'enigme visée
	 *   [category] : catégorie à afficher à la place de l'indice
	 * Exemple :
	 * 	- "1|métier" : obstacle sur l'énigme n°1 (donc la 2è), remplaçant l'indice par "métier"
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
