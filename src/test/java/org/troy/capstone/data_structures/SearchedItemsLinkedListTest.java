package org.troy.capstone.data_structures;

import org.troy.capstone.entities.Item;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

public class SearchedItemsLinkedListTest {
    private static Table table;
    private static ItemHashMap itemHashMap;
    
    @BeforeAll
    public static void setup() {
        table = TableUtils.readCleanedAttributedData();
        itemHashMap = ItemHashMap.fromTable(table);
    }

    @Test
    public void testInitilizationWithEmptyList() {
        SearchedItemsLinkedList list = new SearchedItemsLinkedList(itemHashMap, List.of());
        assert list.getHead() == null : "Expected head to be null for empty item ID list, but got: " + list.getHead();
    }

    @Test
    public void testNextAndPreviousBackAndForth(){
        List<String> itemIds = itemHashMap.getItemIdsAsList();
        SearchedItemsLinkedList list = new SearchedItemsLinkedList(itemHashMap, itemIds.subList(0, 30)); //3 pages of items
        List<Item> firstPage = list.getHead();
        list.getNext();
        List<Item> firstPageAgain = list.getPrevious();

        assert firstPage == firstPageAgain : "Expected to return to the first page, but got a different page of items.";
    }

    
    @ParameterizedTest
    @CsvSource({
        "1, 1", //Single item ID
        "10, 1",
        "51, 6",
        "961, 97" //All items, should be 97 pages (961 items / 10 items per page = 96.1, rounded up to 97)
    })
    public void testPageCountAllItems(int totalItems, int expectedPageCount) {
        List<String> allItemIds = itemHashMap.getItemIdsAsList().subList(0, totalItems);
        SearchedItemsLinkedList list = new SearchedItemsLinkedList(itemHashMap, allItemIds);
        int actualPageCount = 1; //Start with 1 for the head page
        while (list.getNext() != null)
            actualPageCount++;
        assert actualPageCount == expectedPageCount : "Expected page count: " + expectedPageCount + ", but got: " + actualPageCount;
    }

    @Test
    public void testNextAtEndOfList() {
        List<String> itemIds = itemHashMap.getItemIdsAsList().subList(0, 15); //2 pages of items
        SearchedItemsLinkedList list = new SearchedItemsLinkedList(itemHashMap, itemIds);
        list.getNext(); //Move to second page
        List<Item> result = list.getNext(); //Try to move past the end of the list
        assert result == null : "Expected null when trying to move past the end of the list, but got: " + result;
    }

    @Test
    public void testPreviousAtStartOfList() {
        List<String> itemIds = itemHashMap.getItemIdsAsList().subList(0, 15); //2 pages of items
        SearchedItemsLinkedList list = new SearchedItemsLinkedList(itemHashMap, itemIds);
        List<Item> result = list.getPrevious(); //Try to move before the start of the list
        assert result == null : "Expected null when trying to move before the start of the list, but got: " + result;
    }
}
