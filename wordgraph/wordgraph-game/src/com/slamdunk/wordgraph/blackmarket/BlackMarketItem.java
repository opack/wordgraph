package com.slamdunk.wordgraph.blackmarket;

import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.wordgraph.puzzle.PuzzleAttributes;
import com.slamdunk.wordgraph.puzzle.PuzzleScreen;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;
import com.slamdunk.wordgraph.puzzle.obstacles.Obstacle;

public enum BlackMarketItem {
	remove_letter_obstacle {
		@Override
		public void use(PuzzleScreen puzzleScreen) {
			// Récupération de la liste des obstacles appliqués à des lettres
			List<Obstacle> obstacles = puzzleScreen.getObstacleManager().getLetterObstacles();
			
			// Choix d'un obstacle au hasard
			int index = MathUtils.random(0, obstacles.size() - 1);
			Obstacle obstacle = obstacles.get(index);
			
			// Désactivation de cet obstacle
			obstacle.deactivate(puzzleScreen.getGrid());
		}
		
		@Override
		public boolean isAvailable(PuzzleScreen puzzleScreen) {
			return puzzleScreen.getObstacleManager().hasLetterObstacles();
		}
	},
	remove_clue_obstacle {
		@Override
		public void use(PuzzleScreen puzzleScreen) {
			// Récupération de la liste des obstacles appliqués à des lettres
			List<Obstacle> obstacles = puzzleScreen.getObstacleManager().getClueObstacles();
			
			// Choix d'un obstacle au hasard
			int index = MathUtils.random(0, obstacles.size() - 1);
			Obstacle obstacle = obstacles.get(index);
			
			// Désactivation de cet obstacle
			obstacle.deactivate(puzzleScreen.getGrid());
		}

		@Override
		public boolean isAvailable(PuzzleScreen puzzleScreen) {
			return puzzleScreen.getObstacleManager().hasClueObstacles();
		}
	},
	crystal_ball {
		@Override
		public void use(PuzzleScreen puzzleScreen) {
			// Mise en évidence de la lettre concernée
			GridCell lastSelected = puzzleScreen.getSelectedCells().getLast();
			puzzleScreen.highlightCell(lastSelected);
			
			// Extraction de la dernière lettre
			String lastLetter = lastSelected.getLetter();
			
			// Mise en évidence des indices concernés
			PuzzleAttributes attributes = puzzleScreen.getPuzzleAttributes();
			for (Riddle riddle : attributes.getRiddles()) {
				if (riddle.getSolution().contains(lastLetter)) {
					puzzleScreen.highlightRiddle(riddle.getId());
				}
			}
		}
		
		@Override
		public boolean isAvailable(PuzzleScreen puzzleScreen) {
			return !puzzleScreen.getSelectedCells().isEmpty();
		}
	},
	item4 {
		@Override
		public void use(PuzzleScreen puzzleScreen) {
			System.out.println("Utilisation du bonus " + name());
		}
	},
	chrono {
		@Override
		public void use(PuzzleScreen puzzleScreen) {
			puzzleScreen.getChrono().pause(15);
			puzzleScreen.addBonus(this);
		}
	},
	two_times {
		@Override
		public void use(PuzzleScreen puzzleScreen) {
			puzzleScreen.setScoreMultiplier(2);
			puzzleScreen.addBonus(this);
		}
		
		@Override
		public boolean isAvailable(PuzzleScreen puzzleScreen) {
			return !puzzleScreen.getBonuses().contains(two_times);
		}
	};

	/**
	 * Applique l'effet de l'item
	 * @param puzzleScreen
	 */
	public abstract void use(PuzzleScreen puzzleScreen);

	/**
	 * Indique si l'article est disponible ou non en fonction
	 * de l'état du jeu.
	 * @param puzzleScreen
	 * @return
	 */
	public boolean isAvailable(PuzzleScreen puzzleScreen) {
		return true;
	}
}
