package ch.epfl.cs107.play.game.arpg.actor;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.cs107.play.game.rpg.actor.Inventory;
import ch.epfl.cs107.play.game.rpg.actor.InventoryItem;

public class ARPGInventory extends Inventory {

    private final static int INTITIAL_MONEY = 0;
    private final static int MAX_MONEY = 999;
    private List<InventoryItem> inventoList;
    private int money;
    private boolean isHashAdded, isListAdded, isHashRemoved, isListRemoved, isTotallyRemoved;

    protected ARPGInventory() {
        super();

        inventoList = new ArrayList<InventoryItem>();
        money = INTITIAL_MONEY;
        isTotallyRemoved = false;

    }

    /**
     * "Generic", "Final" method that will be called when collecting items and manipulating them in the game. Handle the aspect of
     * the quantity of an InventoryItem, (the fact that we can have more than 1 bow, more than 1 bomb etc...) with the Hashmap.
     * And the aspect of currentItem and index with the List, throug an ArrayList of inventoryItem.
     *
     * @param item (InventoryItem)
     * @param quantity (Integer)
     * @return true if addition is a success / false otherwise
     */
    protected boolean add(InventoryItem item, Integer quantity) {
        addHash(item, quantity);
        addList(item);

        return (isListAdded && isHashAdded);
    }


    /**
     * "Generic", "Final" method that will be called when collecting items and manipulating them in the game. Handle the aspect of
     * the quantity of an InventoryItem, (the fact that we can have more than 1 bow, more than 1 bomb etc...) with the Hashmap.
     * And the aspect of InventoryItem in itself,  currentItem and index with the List, throug an ArrayList of inventoryItem.
     *
     * @param item (InventoryItem)
     * @param quantity (Integer)
     * @return true if removal is a success / false otherwise
     */
    protected boolean remove(InventoryItem item, Integer quantity) {
        removeHash(item, quantity);
        removeList(item);

        return (isListRemoved && isHashRemoved);
    }

    protected boolean removeAll() {
        super.clear();
        inventoList.clear();
        return true;
    }

    /**
     * Adder for the List part of the inventory, if the list already contains the item => does not add it because it's the hash
     * that takes care of the notion of quantity
     *
     * @param item (InventoryItem)
     */
    private void addList(InventoryItem item) {
        if (item != null && !isInInventory(item)) {
            inventoList.add(item);
            isListAdded = true;
        }

    }


    /**
     * Remover for the List part of the inventory
     * @param item (InventoryItem)
     */
    private void removeList(InventoryItem item) {
        //Means : if have 5 bow either take 5 bow out of hashmap and isTotallyRemoved is True, or else use other method
        //with no quantity parameter
        if (item != null && isInInventory(item) && isTotallyRemoved) {
            //If item to be removed is before currentItem in inventoList, the index of the equipped item (currentItem) must be updated
            //So that the currentItem still refers to the same item in the list as before
            //Or if we remove last item of the List and it's the item equipped => decrease the index, so we don't have an indexOutOfBounds exception
            if (inventoList.indexOf(item) < ARPGPlayer.getCItem() || (item.equals(
                    getItem(ARPGPlayer.getCItem())) && inventoList.indexOf(item) == this.getSize() - 1)) {
                ARPGPlayer.setCurrentItem('-');
            }

            //if we remove equipped item and the item that comes after is an arrow, switch to the item after
            if (inventoList.indexOf(item) == ARPGPlayer.getCItem() && getItem(ARPGPlayer.getCItem() + 1).getName().equals(
                    "arrow")) {
                //If would "overflow" list, set index at 0
                if (ARPGPlayer.getCItem() + 2 >= inventoList.size()) {
                    ARPGPlayer.setCurrentItem('0');
                } else {
                    ARPGPlayer.setCurrentItem('+');
                }
            }

            inventoList.remove(item);

            isListRemoved = true;
        }
    }

    //just add calculus with fortune and money
    private void addHash(InventoryItem item, Integer quantity) {
        if (item != null) super.add(item, quantity);

        isHashAdded = true;
    }

    //just add calculus with fortune and money
    private void removeHash(InventoryItem item, Integer quantity) {
        Integer oldQuantity = getAmount(item);

        if (item != null) {
            super.remove(item, quantity);
            isHashRemoved = true;

        }

        isTotallyRemoved = oldQuantity.equals(quantity);
    }

    /**
     * @param item (InventoryItem)
     * @return true if the list part of the inventory contains the item (just wether it contains it, does not care about the
     *         quantity) / false otherwise
     */
    public boolean isInInventory(InventoryItem item) {
        return (inventoList.contains(item));
    }

    /**
     * Add "amountMoney" money to the current money
     *
     * @param amountMoney (int)
     */
    protected void addMoney(int amountMoney) {
        this.money += amountMoney;

        if (money > MAX_MONEY) {
            money = MAX_MONEY;

        } else if (money < 0) {
            money = 0;
        }
    }


    @Override
    protected int getMoney() {
        return money;
    }

    /**
     * Getter for the size of the inventory
     *
     * @return The size of the list part of the inventory
     */
    @Override
    protected int getSize() {
        return inventoList.size();
    }

    @Override
    protected InventoryItem getItem(int index) {
        return inventoList.get(index);
    }

    @Override
    protected int getWeight() {
        return super.getWeight();
    }

    @Override
    protected int getMaxWeight() {
        return super.getMaxWeight();
    }

    @Override
    protected Integer getAmount(InventoryItem item) {
        return super.getAmount(item);
    }

}