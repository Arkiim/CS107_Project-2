package ch.epfl.cs107.play.game.arpg.actor;

import ch.epfl.cs107.play.game.rpg.actor.InventoryItem;
	
/**
 * List all possible ARPGitems
 */
public enum ARPGItem implements InventoryItem{

	BOW ("bow", 30, 50,"zelda/bow.icon"),
	ARROW ("arrow", 1, 5,"zelda/arrow.icon"),
	BOMB ("bomb", 15, 30, "zelda/bomb"),
	SWORD ("sword", 25, 40, "zelda/sword.icon"),
	STAFF ("staff", 10, 50, "zelda/staff_water.icon"),
	CASTLEKEY ("castleKey", 1, 1, "zelda/key");
	
	private String name;
	private int weight;
	private int price;
	private String spriteName;
	
	ARPGItem(String name, int weight, int price, String spriteName) {
		
		this.name = name;
		this.weight = weight;
		this.price = price;
		this.spriteName = spriteName;
		
	}
	
	@Override
	public String getName() {
		return name;

	}
		

	@Override
	public float getWeight() {
		return weight;
	}

	@Override
	public int getPrice() {
		return price;
	}
	
	@Override
	public String getSpriteName() {
		return spriteName;
	}

	
}
