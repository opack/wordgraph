package com.slamdunk.wordgraph.joker;

import static com.slamdunk.wordgraph.puzzle.LetterStates.JOKER;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.wordgraph.puzzle.PuzzleButtonDecorator;
import com.slamdunk.wordgraph.puzzle.graph.PuzzleGraph;

public enum Joker {
	bishop {
		@Override
		public void decorate(String word, PuzzleGraph graph) {
			// Désélectionne toutes les lettres
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(graph);
			
			// Choisit une lettre au hasard
			int index = MathUtils.random(0, word.length() - 1);
			String letter = word.substring(index, index + 1);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setStyle(graph.getNode(letter), JOKER);
		}
	},
	rook {
		@Override
		public void decorate(String word, PuzzleGraph graph) {
			// Désélectionne toutes les lettres
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(graph);
			
			// Choisit la première lettre
			String letter = word.substring(0, 1);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setStyle(graph.getNode(letter), JOKER);
		}
	},
	knight {
		@Override
		public void decorate(String word, PuzzleGraph graph) {
			// Désélectionne toutes les lettres
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(graph);
			
			// Choisit la première lettre
			String firstLetter = word.substring(0, 1);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setStyle(graph.getNode(firstLetter), JOKER);
			
			// Choisit la dernière lettre
			int length = word.length();
			String lastLetter = word.substring(length - 1, length);
			
			// Met la lettre en valeur
			PuzzleButtonDecorator.getInstance().setStyle(graph.getNode(lastLetter), JOKER);
		}
	},
	queen {
		@Override
		public void decorate(String word, PuzzleGraph graph) {
			// Désélectionne toutes les lettres
			PuzzleButtonDecorator.getInstance().setNormalStyleOnAllNodes(graph);
			
			int length = word.length();
			for (int index = 0; index < length; index++) {
				// Choisit la lettre suivante
				String letter = String.valueOf(word.charAt(index));
				
				// Met la lettre en valeur
				PuzzleButtonDecorator.getInstance().setStyle(graph.getNode(letter), JOKER);
			}
		}
	};
	
	public abstract void decorate(String word, PuzzleGraph graph);
}
