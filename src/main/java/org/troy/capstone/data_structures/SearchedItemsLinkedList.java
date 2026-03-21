package org.troy.capstone.data_structures;

import java.util.List;
import java.util.Optional;

import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.entities.Item;

public class SearchedItemsLinkedList{
    
    private ItemListNode head, current;

    private static final int ITEMS_PER_PAGE = 10;

    @SuppressWarnings("null")
    public SearchedItemsLinkedList(ItemHashMap itemHashMap, List<String> itemIdList){
        if (itemIdList == null || itemIdList.isEmpty()) {
            head = null;
            return;
        }
        ItemListNode currentNode = null;
        for (int startIndex = 0; startIndex < itemIdList.size(); startIndex += ITEMS_PER_PAGE) {
            
            //Get items to a list
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, itemIdList.size());
            List<Item> pageItems
            = itemIdList.subList(startIndex, endIndex).stream()
                    .map(itemHashMap::getItem)
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

    public List<Item> getHead() {
        if (head == null)
            return null;
        return head.getItems();
    }
    
    public List<Item> getNext(){
        if (current == null || current.getNext() == null) {
            System.out.println("Already at the end of the list, cannot advance further.");
            return null;
        }
        current = current.getNext();
        System.out.println("Advanced to next page of results.");
        return current.getItems();
    }

    public List<Item> getPrevious(){
        if (current == null || current.getPrev() == null) {
            System.out.println("Already at the beginning of the list, cannot go back further.");
            return null;
        }
        current = current.getPrev();
        System.out.println("Went back to previous page of results.");
        return current.getItems();
    }
     
    private static class ItemListNode{
        private final List<Item> items;
        private ItemListNode next, prev;

        public ItemListNode(List<Item> items) {
            this.items = items;
        }

        public List<Item> getItems() {
            return items;
        }

        public ItemListNode getNext() {
            return next;
        }

        public ItemListNode getPrev() {
            return prev;
        }

        public void setNext(ItemListNode next) {
            this.next = next;
        }

        public void setPrev(ItemListNode prev) {
            this.prev = prev;
        }
    }
}
