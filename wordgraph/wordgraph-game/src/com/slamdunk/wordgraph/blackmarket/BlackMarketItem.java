package com.slamdunk.wordgraph.blackmarket;

import com.slamdunk.wordgraph.puzzle.grid.Grid;

public enum BlackMarketItem {
	item0 {
		@Override
		public void use(Grid grid) {
			System.out.println("Utilisation du bonus " + name());
		}
	},
	item1 {
		@Override
		public void use(Grid grid) {
			System.out.println("Utilisation du bonus " + name());
		}
	},
	horoscope {
		@Override
		public void use(Grid grid) {
			System.out.println("Utilisation du bonus " + name());
		}
	},
	spyglass {
		@Override
		public void use(Grid grid) {
			System.out.println("Utilisation du bonus " + name());
		}
	},
	item4 {
		@Override
		public void use(Grid grid) {
			System.out.println("Utilisation du bonus " + name());
		}
	},
	chrono {
		@Override
		public void use(Grid grid) {
			System.out.println("Utilisation du bonus " + name());
		}
	},
	two_times {
		@Override
		public void use(Grid grid) {
			System.out.println("Utilisation du bonus " + name());
		}
	},
	map {
		@Override
		public void use(Grid grid) {
			System.out.println("Utilisation du bonus " + name());
		}
	};
	
	public abstract void use(Grid grid);
}
