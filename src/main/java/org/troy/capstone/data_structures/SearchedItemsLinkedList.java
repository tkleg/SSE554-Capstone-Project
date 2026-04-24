package org.troy.capstone.data_structures;

import java.util.List;
import java.util.Optional;

import org.troy.capstone.entities.Item;
import org.troy.capstone.interfaces.ItemRepo;

/**
 * The SearchedItemsLinkedList class represents a custom linked list data structure that organizes search results into pages.
 * Each node in the linked list contains a list of items corresponding to a page of search results, and the list provides methods to navigate between pages.
 */
public class SearchedItemsLinkedList{
    
    /** The head node of the linked list. */
    private ItemListNode head;

    /** The current node in the linked list, representing the current page of search results. */
    private ItemListNode current;

    /** The number of items to display per page. */
    private static final int ITEMS_PER_PAGE = 10;

    /** Constructor for SearchedItemsLinkedList. Initializes the linked list based on a list of item IDs and an item repository.
     * @pre itemRepo should contain valid item data for all item IDs in itemIdList, and itemIdList should not be null.
     * @param itemRepo The item repository containing all items, used to populate the linked list nodes based on the provided item IDs.
     * @param itemIdList The list of item IDs representing the search results to be organized into pages in the linked list.
    */
    @SuppressWarnings("null")
    public SearchedItemsLinkedList(ItemRepo itemRepo, List<String> itemIdList){
        if (itemIdList.isEmpty()) {
            head = null;
            return;
        }
        ItemListNode currentNode = null;
        for (int startIndex = 0; startIndex < itemIdList.size(); startIndex += ITEMS_PER_PAGE) {
            
            //Get items to a list
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, itemIdList.size());
            List<Item> pageItems
            = itemIdList.subList(startIndex, endIndex).stream()
                    .map(itemRepo::getItem)
                    .map(Optional::orElseThrow)
                    .toList();

            ItemListNode newNode = new ItemListNode(pageItems);
            if (head == null) {
                head = newNode;
                currentNode = head;
            } else {
                currentNode.setNext(newNode);
                newNode.setPrev(currentNode);
                currentNode = newNode;
            }
        }
        current = head;
    }

    /** Returns the list of items in the head node of the linked list.
     * @return The list of items in the head node, or null if the linked list is empty.
     */
    public List<Item> getHead() {
        if (head == null)
            return null;
        return head.getItems();
    }
    
    /** Returns the list of items in the next node of the linked list.
     * 
     * @post If there is a next node, the current node is advanced to the next node.
     * 
     * @return The list of items in the next node, or null if there is no next node.
     */
    public List<Item> getNext(){
        if ( current.getNext() == null ) {
            System.out.println("Already at the end of the list, cannot advance further.");
            return null;
        }
        current = current.getNext();
        System.out.println("Advanced to next page of results.");
        return current.getItems();
    }

    /** Returns the list of items in the previous node of the linked list.
     * @post If there is a previous node, the current node is moved to the previous node.
      *
     * @return The list of items in the previous node, or null if there is no previous node.
     */
    public List<Item> getPrevious(){
        if ( current.getPrev() == null ) {
            System.out.println("Already at the start of the list, cannot go back further.");
            return null;
        }
        current = current.getPrev();
        System.out.println("Went back to previous page of results.");
        return current.getItems();
    }
     
    /** Private inner class representing a node in the linked list. */
    private static class ItemListNode{
        /** The list of items corresponding to a page of search results contained in this node. */
        private final List<Item> items;

        /** The next node in the linked list. */
        private ItemListNode next;

        /** The previous node in the linked list. */
        private ItemListNode prev;

        /** Constructor for ItemListNode.
         * @pre items should be a valid list of items corresponding to a page of search results, and should not be null.
         * @param items The list of items to be contained in this node, representing a page of search results.
        */
        public ItemListNode(List<Item> items) {
            this.items = items;
        }

        /** Returns the list of items contained in this node.
         * @return The list of items in this node.
         */
        public List<Item> getItems() {
            return items;
        }

        /** Returns the next node in the linked list.
         * @return The next node in the linked list, or null if there is none.
         */
        public ItemListNode getNext() {
            return next;
        }

        /** Returns the previous node in the linked list.
         * @return The previous node in the linked list, or null if there is none.
         */
        public ItemListNode getPrev() {
            return prev;
        }

        /** Sets the next node in the linked list.
         * @pre next should be a valid ItemListNode or null.
         * @param next The node to set as the next node in the linked list.
         */
        public void setNext(ItemListNode next) {
            this.next = next;
        }

        /** Sets the previous node in the linked list.
         * @pre prev should be a valid ItemListNode or null.
         * @param prev The node to set as the previous node in the linked list.
         */
        public void setPrev(ItemListNode prev) {
            this.prev = prev;
        }
    }
}
