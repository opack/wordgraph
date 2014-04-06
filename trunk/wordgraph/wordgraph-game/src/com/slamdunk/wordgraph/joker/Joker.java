package com.slamdunk.wordgraph.joker;

import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.wordgraph.puzzle.PuzzleScreen;
import com.slamdunk.wordgraph.puzzle.Riddle;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

public enum Joker {
	bishop {
		@Override
		public void decorate(Riddle riddle, PuzzleScreen puzzleScreen) {
			// Cherche le mot dans la grille
			List<GridCell> selectedCells = puzzleScreen.getGrid().findWord(riddle.getSolution());
			
			// Choisit une cellule au hasard
			int index = MathUtils.random(0, selectedCells.size() - 1);
			GridCell jokerCell = selectedCells.get(index);
			
			// Met la lettre en valeur
			puzzleScreen.highlightCell(jokerCell);
			
			// Met l'indice en valeur
			puzzleScreen.highlightRiddle(riddle.getId());
		}
	},
	rook {
		@Override
		public void decorate(Riddle riddle, PuzzleScreen puzzleScreen) {
			// Cherche le mot dans la grille
			List<GridCell> selectedCells = puzzleScreen.getGrid().findWord(riddle.getSolution());
			
			// Choisit la première cellule
			GridCell jokerCell = selectedCells.get(0);
			
			// Met la lettre en valeur
			puzzleScreen.highlightCell(jokerCell);

			// Met l'indice en valeur
			puzzleScreen.highlightRiddle(riddle.getId());
		}
	},
	knight {
		@Override
		public void decorate(Riddle riddle, PuzzleScreen puzzleScreen) {
			// Cherche le mot dans la grille
			List<GridCell> selectedCells = puzzleScreen.getGrid().findWord(riddle.getSolution());
			
			// Choisit la première et la dernière cellule
			GridCell firstJokerCell = selectedCells.get(0);
			GridCell lastJokerCell = selectedCells.get(selectedCells.size() - 1);
			
			// Met les lettres en valeur
			puzzleScreen.highlightCell(firstJokerCell);
			puzzleScreen.highlightCell(lastJokerCell);
			
			// Met l'indice en valeur
			puzzleScreen.highlightRiddle(riddle.getId());
		}
	},
	queen {
		@Override
		public void decorate(Riddle riddle, PuzzleScreen puzzleScreen) {
			// Cherche le mot dans la grille
			List<GridCell> selectedCells = puzzleScreen.getGrid().findWord(riddle.getSolution());
			
			// Met les cellules en valeur
			for (GridCell jokerCell : selectedCells) {
				puzzleScreen.highlightCell(jokerCell);
			}

			// Met l'indice en valeur
			puzzleScreen.highlightRiddle(riddle.getId());
		}
	};
	
	public abstract void decorate(Riddle riddle, PuzzleScreen puzzleScreen);
}
