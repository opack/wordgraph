package com.slamdunk.wordgraph.blackmarket;

import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.wordgraph.puzzle.PuzzleScreen;
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
			System.out.println("Utilisation du bonus " + name());
		}
		
		@Override
		public boolean isAvailable(PuzzleScreen puzzleScreen) {
			return !puzzleScreen.getCurrentWord().isEmpty();
		}
	},
	spyglass {
		@Override
		public void use(PuzzleScreen puzzleScreen) {
			System.out.println("Utilisation du bonus " + name());
		}

		@Override
		public boolean isAvailable(PuzzleScreen puzzleScreen) {
			return !puzzleScreen.getCurrentWord().isEmpty();
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
	},
	map {
		@Override
		public void use(PuzzleScreen puzzleScreen) {
			System.out.println("Utilisation du bonus " + name());
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
