package com.slamdunk.wordgraph.puzzle.obstacles;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.slamdunk.utils.PropertiesEx;
import com.slamdunk.wordgraph.Assets;
import com.slamdunk.wordgraph.PuzzlePreferencesHelper;
import com.slamdunk.wordgraph.puzzle.grid.Grid;

/**
 * Place une bombe sur la lettre qui va exploser et casser tous les liens
 * au bout d'un certain nombre de touches
 */
public class BombObstacle extends CellObstacle{
	private int countDown;
	private Label label;
	
	public BombObstacle() {
		setType(ObstaclesTypes.BOMB);
	}
	
	@Override
	public void applyEffect(Grid grid) {
		super.applyEffect(grid);
		// Si l'obstacle est actif, on masque la lettre
		if (isActive()) {
			// Place une image de bombe et un libell� avec le d�compte
			if (getImage() == null) {
				createImage("obstacle-bomb");
				label = new Label("", Assets.defaultPuzzleSkin.get("text", LabelStyle.class));
				label.setText(String.valueOf(countDown));
				TextButton button = getCell().getButton();
				button.addActor(label);
				label.setWidth(button.getWidth());
				label.setAlignment(Align.center);
				label.setZIndex(0);
			}
			// Met � jour le libell� de d�compte et, si n�cessaire, fait exploser la bombe
			updateAndDetonate();
		}
		// Si, apr�s le updateAndDetonate, la bombe n'est plus active, alors
		// on retire la bombe
		if (!isActive()) {
			// Sinon on supprime l'image et le libell�
			if (getImage() != null) {
				getImage().remove();
				setImage(null);
				label.remove();
				label = null;
			}
		}
	}
	
	/**
	 * Met � jour le libell� de d�compte et, si n�cessaire, fait exploser la bombe
	 */
	private void updateAndDetonate() {
		if (countDown == 0) {
			ObstacleManager manager = getManager();
			Grid grid = manager.getGrid();
			// Suppression de l'obstacle Bomb
			setActive(false);
			// On ne d�sactive pas l'obstacle car il sera transform� en �le
			// lors du prochain affichage
			//writePreferenceObstacleActive(false);
			// Cr�ation d'un obstacle Isle
			IsleObstacle isle = new IsleObstacle();
			isle.setLine(getLine());
			isle.setColumn(getColumn());
			isle.puzzleLoaded(grid, manager.getPuzzleAttributes(), manager.getStage(), getPuzzlePreferences());
			isle.applyEffect(grid);
			manager.add(isle);
		} else {
			label.setText(String.valueOf(countDown));
		}
	}

	@Override
	public void letterSelected(String letter) {
		super.letterSelected(letter);
		// A chaque lettre s�lectionn�e, le compte-�-rebours baisse
		countDown--;
		applyEffect(getManager().getGrid());
		// Sauvegarde le compte
		saveToPreferences();
	}
	
	@Override
	public void initFromProperties(PropertiesEx properties, String key) {
		super.initFromProperties(properties, key);
		
		// Lit le compte-�-rebours initial
		countDown = properties.getIntegerProperty(key + ".countDown", 10);
	}
	
	/**
	 * Lit les informations de l'instance de l'obstacle depuis les pr�f�rences
	 */
	public void loadFromPreferences() {
		super.loadFromPreferences();
		PuzzlePreferencesHelper prefs = getPuzzlePreferences();
		
		int prefsCountDown = prefs.getInteger(getPreferencesKey() + ".countDown", -1);
		if (prefsCountDown != -1) {
			countDown = prefsCountDown;
		}
	}
	
	@Override
	public void saveToPreferences() {
		PuzzlePreferencesHelper prefs = getPuzzlePreferences();
		prefs.putInteger(getPreferencesKey() + ".countDown", countDown);
		
		// Faire le super en dernier car il contient le flush()
		super.saveToPreferences();
	}
}
