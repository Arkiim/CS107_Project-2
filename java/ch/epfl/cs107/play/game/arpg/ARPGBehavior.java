package ch.epfl.cs107.play.game.arpg;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.arpg.actor.FlyableEntity;
import ch.epfl.cs107.play.window.Window;

public class ARPGBehavior extends AreaBehavior {
	/**
	 * Constructor of ARPGBehavior
	 */
	public ARPGBehavior(Window window, String name) {
		super(window, name);

		int height = getHeight();
		int width = getWidth();

		for(int y = 0 ; y < height ; y++) {
			for(int x = 0 ; x < width ; x++) {
				ARPGCellType cellType = ARPGCellType.toType(getRGB(height-1-y,x));
				ARPGCell cell = new ARPGCell(x, y, cellType);
				setCell(x, y, cell);
			}
		}

	}
	
	public enum ARPGCellType {
		NULL (0, false, false),
		WALL (-16777216, false,false), // #000000 RGB code of black
		IMPASSABLE (-8750470, false, true), // #7 A7A7A , RGB color of gray
		INTERACT (-256, true, true), // #FFFF00 , RGB color of yellow
		DOOR (-195580, true, true), // #FD0404 , RGB color of red
		WALKABLE (-1, true, true); // #FFFFFF , RGB color of white

		final int type;
		final boolean isWalkable;
		final boolean isFlyable;
		
		ARPGCellType(int type, boolean isWalkable, boolean isFlyable){
			this.type = type;
			this.isWalkable = isWalkable;
			this.isFlyable = isFlyable;
		}

		public static ARPGCellType toType(int type) {
			switch(type) {
			case -16777216 :
				return WALL;
			case -8750470 :
				return IMPASSABLE;
			case -256 :
				return INTERACT;
			case -195580 :
				return DOOR;
			case -1 :
				return WALKABLE;
			default :
				return NULL;	
			}
		}

	}


	/************* ARPG CELL *************************/


	public class ARPGCell extends Cell {
		private ARPGCellType type;

		/**
		 * Constructor of ARPGCell, where x, y are the coordinates of the cell
		 */
		private ARPGCell(int x, int y, ARPGCellType ttype) {
			super(x,y);
			this.type = ttype;
		}

		@Override
		protected boolean canLeave(Interactable entity) {
			return true;
		}

		@Override
		protected boolean canEnter(Interactable entity) {
			if (entity instanceof FlyableEntity) {
				if (((FlyableEntity) entity).canFly()) {
					return type.isFlyable;
				}
			}
			return (type.isWalkable && (!hasNonTraversableContent() || !entity.takeCellSpace()));
		}

		@Override
		public boolean isCellInteractable() {
			return true;
		}

		@Override
		public boolean isViewInteractable() {
			return false;
		}

		@Override
		public void acceptInteraction(AreaInteractionVisitor v) {}

	}

}
