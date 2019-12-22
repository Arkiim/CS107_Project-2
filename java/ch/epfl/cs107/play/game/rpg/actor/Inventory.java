package ch.epfl.cs107.play.game.rpg.actor;

import java.util.HashMap;
import java.util.Map;

/**
 * inventoHash of Inventory is the Hashmap part of the inventory
 * inventoList of ARPGInventory is the List part of the inventory
 *
 */
public abstract class Inventory {

	/**
	 * List of items => Inventory,
	 * can contain Arrows, Swords, Staffs, Bombs and CastleKeys
	 */
	private Map<InventoryItem, Integer> inventoHash;
	private int maxWeight;
	private int currentWeight;
	public static boolean isItemAdded = false;
	public static boolean isItemRemoved = false;


	protected Inventory(){

		maxWeight = 300;	
		inventoHash = new HashMap<InventoryItem, Integer>();
		currentWeight = 0;

	}

	/**Remover for the Hashmap inventory ( the one that handles the quantities of the ARPGItems ). |||
	 * 
	 *  Remove an amount of "quantity" InventoryItem
	 * and update currentWeight
	 * @param quantity (Integer) : quantity of item to be removed 
	 * @param item (InventoryItem)
	 * @return true if removal is a success / false otherwise
	 */
	 protected boolean remove(InventoryItem item, Integer quantity) {
		Integer oldQuantity = getAmount(item);
		Integer newQuantity = oldQuantity - quantity;

		if(newQuantity > 0) {
			setQuantity(item, newQuantity);
			currentWeight -= item.getWeight()*quantity ; 
			//e.g. takes 10 "kg" off the currentWeight if 2 "5kg"-items from , are removed

		} else if (newQuantity == 0) {
			inventoHash.remove(item);
			//remove the totality of the weight of the removed items off the currentWeight
			currentWeight -= item.getWeight()*oldQuantity;		
		}

		isItemRemoved = true;
		return isItemRemoved;
	}

	/**Remover for the Hashmap inventory ( the one that handles the quantities of the ARPGItems ). |||
	 * 
	 *  Remove all InventoryItems item, no matter how many were in the inventory
	 * and update currentWeight
	 * @param item (InventoryItem)
	 * @return true if removal is a success / false otherwise
	 */
	protected boolean remove(InventoryItem item) {
		currentWeight -= item.getWeight()*getAmount(item);
		inventoHash.remove(item);

		isItemRemoved = true;	
		return isItemRemoved;
	}

	/**
	 * Clears the hashmap inventoHash
	 */
	protected void clear() {
		inventoHash.clear();
	}

	/**Adder for the Hashmap inventory ( the one that handles the quantities of the ARPGItems ). |||
	 * 
	 * Add an amount of "quantity" InventoryItems to the inventory
	 * and update currentWeight
	 * @param quantity (Integer) : quantity of item to be added 
	 * @param item (InventoryItem)
	 * @return true if addition is a success / false otherwise
	 */
	protected boolean add(InventoryItem item, Integer quantity) {
		int weight = (int) (quantity*item.getWeight());

		if(currentWeight + weight <= maxWeight) {

			if(isIn(item) ) {
				Integer oldQuantity = getAmount(item);
				Integer newQuantity = oldQuantity + quantity;
				setQuantity(item, newQuantity);
				currentWeight += weight;

			} else {
				inventoHash.put(item, quantity);
				currentWeight += weight;
			}

			isItemAdded = true;
		}
		return isItemAdded;
	}

	/**
	 * First check if the item is in the inventory if not return 0
	 * @param item
	 * @return quantity of the item in the inventory
	 */
	protected Integer getAmount(InventoryItem item) {
		if(isIn(item)) {
			return inventoHash.get(item);
		} else {
			return 0;
		}
	}

	/**
	 * Change the quantity assotiated to an item in the HashMap Inventory.
	 * WARNING : Does not update currentWeight ! 
	 * @param item (InventoryItem) 
	 * @param quantity (Integer) : New wanted quantity after call of the method (not null, if needs to be null, use remove method)
	 */
	private void setQuantity(InventoryItem item, Integer quantity) {
		Integer oldQuantity = getAmount(item);

		if(oldQuantity != 0 && quantity != 0) {
			inventoHash.remove(item);
			inventoHash.put(item, (quantity));
		}

	}

	/**
	 * @param item
	 * @return true if the HashMap contains the item / false otherwise
	 */
	private boolean isIn(InventoryItem item) {
		return inventoHash.containsKey(item);
	}

	/** @return MaxWeight of the inventory (int)*/
	protected int getMaxWeight() {
		return maxWeight;
	}

	/** @return currentWeight of the inventory (int)*/
	protected int getWeight() {
		return this.currentWeight;
	}

	 /** @return current money (int)*/
	abstract protected int getMoney();

	/**
	 * Getter for the size of the inventory
	 * @return The size of the list part of the inventory
	 */
	abstract protected int getSize();

	/**
	 * @param index (int)
	 * @return The InventoryItem at index "index" in the Inventory
	 */
	abstract protected InventoryItem getItem(int index);

}