package org.troy.capstone.ui_components.items.searched;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.data_structures.ItemTable.IdHashKey;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.utils.TableUtils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.embed.swing.JFXPanel;
import tech.tablesaw.api.Table;

public class SearchedItemPaginationTest {
    private SearchedItemPagination pagination;
    private GeneralManager generalManager;
    private Table table;
    private ItemHashMap itemHashMap;
    private static final int ITEMS_PER_PAGE = 10; // Match the constant in SearchedItemPagination
    
    @BeforeAll
    public static void setup() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() {
        table = TableUtils.readCleanedAttributedData();
        generalManager = new GeneralManager(table);
        itemHashMap = ItemHashMap.fromTable(table);
        pagination = SearchedItemPagination.create(itemHashMap, generalManager);
    }

    @Test
    @DisplayName("Test createPageContent for page with more items than can be displayed")
    public void testCreatePageContentForPageWithMoreItemsThanCanBeDisplayed() {
        //Create a set of item IDs that exceeds the items per page limit
        Set<String> itemIDs = itemHashMap.keySet().stream()
            .map(IdHashKey::getValue)
            .limit(ITEMS_PER_PAGE + 5) //Exceed by 5 items to ensure more than 1 page
            .collect(Collectors.toSet());
        
        SearchedItemContainer container = pagination.createPageContent(0, itemIDs);
        
        assertEquals( container.getContainerChildren().size(), ITEMS_PER_PAGE, 
            "Page content should contain exactly the number of items per page when there are more items than can be displayed");
    }

    @Test
    @DisplayName("Test createPageContent for page with fewer items than can be displayed")
    public void testCreatePageContentForPageWithFewerItemsThanCanBeDisplayed() {
        //Create a set of item IDs that is less than the items per page limit
        Set<String> itemIDs = itemHashMap.keySet().stream()
            .map(IdHashKey::getValue)
            .limit(ITEMS_PER_PAGE - 3) //Less by 3 items to ensure only 1 page with empty space
            .collect(Collectors.toSet());
        
        SearchedItemContainer container = pagination.createPageContent(0, itemIDs);
        
        //Verify that the page content size is correct (should be less than ITEMS_PER_PAGE)
        assertEquals( container.getContainerChildren().size(), ITEMS_PER_PAGE - 3, 
            "Page content should contain exactly the number of items per page when there are fewer items than can be displayed");
    }

    @Nested
    @DisplayName("Tests for pagination content amount after creation")
    class PaginationContentAmountTests {

        @Test
        @DisplayName("Test page count is calculated correctly for full item set")
        public void testPageCountForFullItemSet() {
            int totalItems = itemHashMap.size();
            int expectedPageCount = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
            
            assertEquals(expectedPageCount, pagination.getPageCount(), 
                "Page count should be calculated correctly based on total items");
        }

        @Test
        @DisplayName("Test page count with empty item set")
        public void testPageCountWithEmptyItemSet() {
            Set<String> emptyItemIDs = new HashSet<>();
            pagination.updateContent(emptyItemIDs);
            
            assertEquals(1, pagination.getPageCount(), 
                "Page count should be 1 when there are no items");
        }

        @Test
        @DisplayName("Test page count with exactly one page of items")
        public void testPageCountWithExactlyOnePageOfItems() {
            Set<String> itemIDs = itemHashMap.keySet().stream()
                .map(IdHashKey::getValue)
                .limit(ITEMS_PER_PAGE)
                .collect(Collectors.toSet());
            
            pagination.updateContent(itemIDs);
            
            assertEquals(1, pagination.getPageCount(), 
                "Page count should be 1 when items exactly fill one page");
        }

        @Test
        @DisplayName("Test page count with one more than one page of items")
        public void testPageCountWithOneMoreThanOnePageOfItems() {
            Set<String> itemIDs = itemHashMap.keySet().stream()
                .map(IdHashKey::getValue)
                .limit(ITEMS_PER_PAGE + 1)
                .collect(Collectors.toSet());
            
            pagination.updateContent(itemIDs);
            
            assertEquals(2, pagination.getPageCount(), 
                "Page count should be 2 when items exceed one page by one item");
        }

        @Test
        @DisplayName("Test page count with exactly two pages of items")
        public void testPageCountWithExactlyTwoPagesOfItems() {
            Set<String> itemIDs = itemHashMap.keySet().stream()
                .map(IdHashKey::getValue)
                .limit(ITEMS_PER_PAGE * 2)
                .collect(Collectors.toSet());
            
            pagination.updateContent(itemIDs);
            
            assertEquals(2, pagination.getPageCount(), 
                "Page count should be 2 when items exactly fill two pages");
        }

        @Test
        @DisplayName("Test updatePageCount method calculates correctly")
        public void testUpdatePageCountCalculation() {
            //Test various total item counts
            int[] testCounts = {0, 1, 5, 10, 11, 20, 25, 100};
            int[] expectedPages = {1, 1, 1, 1, 2, 2, 3, 10};
            
            for (int i = 0; i < testCounts.length; i++) {
                pagination.updatePageCount(testCounts[i]);
                assertEquals(expectedPages[i], pagination.getPageCount(), 
                    String.format("For %d items, expected %d pages but got %d", 
                        testCounts[i], expectedPages[i], pagination.getPageCount()));
            }
        }

        @Test
        @DisplayName("Test pagination maintains correct page count after content updates")
        public void testPageCountAfterContentUpdates() {
            //Start with a large set
            Set<String> largeItemSet = itemHashMap.keySet().stream()
                .map(IdHashKey::getValue)
                .limit(25)
                .collect(Collectors.toSet());
            
            pagination.updateContent(largeItemSet);
            int largeSetPageCount = pagination.getPageCount();
            
            //Update to a smaller set
            Set<String> smallItemSet = itemHashMap.keySet().stream()
                .map(IdHashKey::getValue)
                .limit(5)
                .collect(Collectors.toSet());
            
            pagination.updateContent(smallItemSet);
            int smallSetPageCount = pagination.getPageCount();
            
            assertEquals(3, largeSetPageCount, "Large set should have 3 pages");
            assertEquals(1, smallSetPageCount, "Small set should have 1 page");
        }

        @Test
        @DisplayName("Test pagination handles boundary cases correctly")
        public void testPaginationBoundaryCases() {
            //Test with single item
            Set<String> singleItem = itemHashMap.keySet().stream()
                .map(IdHashKey::getValue)
                .limit(1)
                .collect(Collectors.toSet());
            
            pagination.updateContent(singleItem);
            assertEquals(1, pagination.getPageCount(), "Single item should result in 1 page");
            
            //Test with maximum available items from the hash map
            Set<String> allItems = itemHashMap.keySet().stream()
                .map(IdHashKey::getValue)
                .collect(Collectors.toSet());
            
            pagination.updateContent(allItems);
            int expectedMaxPages = (int) Math.ceil((double) allItems.size() / ITEMS_PER_PAGE);
            assertEquals(expectedMaxPages, pagination.getPageCount(), 
                "All items should result in correctly calculated page count");
        }
    }
}
