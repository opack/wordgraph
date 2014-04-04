package com.slamdunk.wordgraph.joker;

import static com.slamdunk.wordgraph.puzzle.LetterStates.JOKER;

import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.wordgraph.puzzle.PuzzleButtonDecorator;
import com.slamdunk.wordgraph.puzzle.grid.Grid;
import com.slamdunk.wordgraph.puzzle.grid.GridCell;

public enum Joker {
	bishop {
		@Override
		public void decorate(String word, Grid grid) {
			// Cherche le mot dans la grille
			List<GridCell> selectedCells = grid.findWord(word);
			
			// Choisit une cellule au hasard
			int index = MathUtils.random(0, selectedCells.size() - 1);
			GridCell jokerCell = selectedCells.get(index);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(grid);
			PuzzleButtonDecorator.getInstance().setStyle(jokerCell, JOKER);
		}
	},
	rook {
		@Override
		public void decorate(String word, Grid grid) {
			// Cherche le mot dans la grille
			List<GridCell> selectedCells = grid.findWord(word);
			
			// Choisit la première cellule
			GridCell jokerCell = selectedCells.get(0);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(grid);
			PuzzleButtonDecorator.getInstance().setStyle(jokerCell, JOKER);
		}
	},
	knight {
		@Override
		public void decorate(String word, Grid grid) {
			// Cherche le mot dans la grille
			List<GridCell> selectedCells = grid.findWord(word);
			
			// Choisit la première et la dernière cellule
			GridCell firstJokerCell = selectedCells.get(0);
			GridCell lastJokerCell = selectedCells.get(selectedCells.size() - 1);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(grid);
			PuzzleButtonDecorator.getInstance().setStyle(firstJokerCell, JOKER);
			PuzzleButtonDecorator.getInstance().setStyle(lastJokerCell, JOKER);
		}
	},
	queen {
		@Override
		public void decorate(String word, Grid grid) {
			// Cherche le mot dans la grille
			List<GridCell> selectedCells = grid.findWord(word);
			
			// Met les cellules en valeur
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(grid);
			for (GridCell jokerCell : selectedCells) {
				PuzzleButtonDecorator.getInstance().setStyle(jokerCell, JOKER);
			}
		}
	};
	
	public abstract void decorate(String word, Grid grid);
}
