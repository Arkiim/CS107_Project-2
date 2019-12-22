package ch.epfl.cs107.play.game.rpg.actor;

public interface InventoryItem {
	
	
		/**
		 * Getter for the name
		 * @return Name of the item
		 */
		String getName();
		
		/**
		 * Getter for the weight
		 * @return (int) Weight of the item
		 */
		float getWeight();
		
		/**
		 * Getter for the price
		 * @return (int) Price of the item
		 */
		int getPrice();
		
		/**
		 * Getter for the name of the sprite used to represent the item
		 * @return (String) name of the sprite
		 */
		String getSpriteName();

		/**  sub-interface InventoryItem.Holder  **/

		public interface Holder {
			
			boolean possess(InventoryItem item);
		
		}
		
}