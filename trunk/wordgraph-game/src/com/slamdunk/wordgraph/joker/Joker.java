package com.slamdunk.wordgraph.joker;

import com.badlogic.gdx.math.MathUtils;
import com.slamdunk.wordgraph.puzzle.graph.Graph;

public enum Joker {
	bishop {
		@Override
		public void decorate(String word, Graph graph) {
			// Désélectionne toutes les lettres
			graph.highlightAllNodes(false);
			
			// Choisit une lettre au hasard
			int index = MathUtils.random(0, word.length() - 1);
			String letter = word.substring(index, index + 1);
			
			// Met la lettre en valeur
			graph.highlightNode(letter);
		}
	},
	rook {
		@Override
		public void decorate(String word, Graph graph) {
			// Désélectionne toutes les lettres
			graph.highlightAllNodes(false);
			
			// Choisit la première lettre
			String letter = word.substring(0, 1);
			
			// Met la lettre en valeur
			graph.highlightNode(letter);
		}
	},
	knight {
		@Override
		public void decorate(String word, Graph graph) {
			// Désélectionne toutes les lettres
			graph.highlightAllNodes(false);
			
			// Choisit la première lettre
			String firstLetter = word.substring(0, 1);
			
			// Met la lettre en valeur
			graph.highlightNode(firstLetter);
			
			// Choisit la dernière lettre
			int length = word.length();
			String lastLetter = word.substring(length - 1, length);
			
			// Met la lettre en valeur
			graph.highlightNode(lastLetter);
		}
	},
	queen {
		@Override
		public void decorate(String word, Graph graph) {
			// Désélectionne toutes les lettres
			graph.highlightAllNodes(false);
			
			int length = word.length();
			for (int index = 0; index < length; index++) {
				// Choisit la lettre suivante
				String letter = String.valueOf(word.charAt(index));
				
				// Met la lettre en valeur
				graph.highlightNode(letter);
			}
		}
	};
	
	public abstract void decorate(String word, Graph graph);
}
