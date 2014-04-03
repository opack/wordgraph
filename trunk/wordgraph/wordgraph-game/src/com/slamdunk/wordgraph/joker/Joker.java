package com.slamdunk.wordgraph.joker;

import static com.slamdunk.wordgraph.puzzle.LetterStates.JOKER;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.wordgraph.puzzle.PuzzleButtonDecorator;
import com.slamdunk.wordgraph.puzzle.graph.DELETE.PuzzleGraph;
import com.slamdunk.wordgraph.puzzle.grid.Grid;

public enum Joker {
	bishop {
		@Override
		public void decorate(String word, Grid grid) {
			// Désélectionne toutes les lettres
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(grid);
			
			// Choisit une lettre au hasard
			int index = MathUtils.random(0, word.length() - 1);
			String letter = word.substring(index, index + 1);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setStyle(grid.getNode(letter), JOKER);
		}
	},
	rook {
		@Override
		public void decorate(String word, Grid grid) {
			// Désélectionne toutes les lettres
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(grid);
			
			// Choisit la première lettre
			String letter = word.substring(0, 1);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setStyle(grid.getNode(letter), JOKER);
		}
	},
	knight {
		@Override
		public void decorate(String word, Grid grid) {
			// Désélectionne toutes les lettres
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(grid);
			
			// Choisit la première lettre
			String firstLetter = word.substring(0, 1);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setStyle(grid.getNode(firstLetter), JOKER);
			
			// Choisit la dernière lettre
			int length = word.length();
			String lastLetter = word.substring(length - 1, length);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setStyle(grid.getNode(lastLetter), JOKER);
		}
	},
	queen {
		@Override
		public void decorate(String word, Grid grid) {
			// Désélectionne toutes les lettres
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(grid);
			
			int length = word.length();
			for (int index = 0; index < length; index++) {
				// Choisit la lettre suivante
				String letter = String.valueOf(word.charAt(index));
				
				// Met la lettre en valeur
				PuzzleButtonDecorator.getInstance().setStyle(grid.getNode(letter), JOKER);
			}
		}
	};
	
	public abstract void decorate(String word, Grid grid);
}
