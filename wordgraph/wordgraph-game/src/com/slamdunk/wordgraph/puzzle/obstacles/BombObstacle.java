package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.grid.Grid;

/**
 * Place une bombe sur la lettre qui va exploser et casser tous les liens
 * au bout d'un certain nombre de touches
 */
public class BombObstacle extends CellObstacle{
	private int countDown;
	private Label label;
	
	@Override
	public void puzzleLoaded(Grid grid, PuzzleAttributes puzzleAttributes, Stage stage, PuzzlePreferencesHelper puzzlePreferences) {
		super.puzzleLoaded(grid, puzzleAttributes, stage, puzzlePreferences);
		// Initialise le compte
		int prefsCountDown = puzzlePreferences.getBombCountDown(getTarget());
		if (prefsCountDown != -1) {
			countDown = prefsCountDown;
		}
		applyEffect(grid);
	}

	@Override
	public void applyEffect(Grid grid) {
		super.applyEffect(grid);
		// Si l'obstacle est actif, on masque la lettre
		if (isActive()) {
			// Place une image de bombe et un libellé avec le décompte
			if (getImage() == null) {
				createImage("obstacle-bomb");
				label = new Label(String.valueOf(countDown), Assets.defaultPuzzleSkin.get("text", LabelStyle.class));
				TextButton button = getCell().getButton();
				button.addActor(label);
				label.setWidth(button.getWidth());
				label.setAlignment(Align.center);
				label.setZIndex(0);
			}
			// Met à jour le libellé de décompte et, si nécessaire, fait exploser la bombe
			updateAndDetonate();
		}
		// Si, après le updateAndDetonate, la bombe n'est plus active, alors
		// on retire la bombe
		if (!isActive()) {
			// Sinon on supprime l'image et le libellé
			if (getImage() != null) {
				getImage().remove();
				setImage(null);
				label.remove();
				label = null;
			}
		}
	}
	
	/**
	 * Met à jour le libellé de décompte et, si nécessaire, fait exploser la bombe
	 */
	private void updateAndDetonate() {
		label.setText(String.valueOf(countDown));
		if (countDown == 0) {
			ObstacleManager manager = getManager();
			// Suppression de l'obstacle Bomb
			setActive(false);
			// On ne désactive pas l'obstacle car il sera transformé en île
			// lors du prochain affichage
			//writePreferenceObstacleActive(false);
			// Création d'un obstacle Isle
			IsleObstacle isle = new IsleObstacle();
			isle.setLine(getLine());
			isle.setColumn(getColumn());
			isle.init(manager.getGrid(), manager.getPuzzleAttributes(), manager.getStage(), getPuzzlePreferences());
			manager.add(isle);
		}
	}

	@Override
	public void letterSelected(String letter) {
		super.letterSelected(letter);
		// A chaque lettre sélectionnée, le compte-à-rebours baisse
		countDown--;
		applyEffect(getManager().getGrid());
		// Sauvegarde le compte
		getPuzzlePreferences().setBombCountDown(getTarget(), countDown);
	}
	
	@Override
	public void initFromProperties(PropertiesEx properties, String key) {
		super.initFromProperties(properties, key);
		
		// Lit le compte-à-rebours initial
		countDown = properties.getIntegerProperty(key + ".countDown", 10);
	}
}
