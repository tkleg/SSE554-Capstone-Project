package org.troy.capstone.ui_components.items.searched;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.data_structures.SearchedItemsLinkedList;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.entities.Item;
import org.troy.capstone.managers.general.GeneralManager;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class SearchedItemPaginationTest {
    @SuppressWarnings("unused")
    private SearchedItemPagination pagination;
    private GeneralManager generalManager;
    private static final ItemHashMap itemHashMap = TestDataHolder.getItemHashMapCopy();

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() throws NoSuchMethodException {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() {
        generalManager = new GeneralManager(TestDataHolder.getTableCopy());
        pagination = new SearchedItemPagination(itemHashMap, null);
        generalManager.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, pagination);
    }

    @Test
    @DisplayName("Test that generalManager has the pagination component and it is cast properly")
    public void testPaginationComponentInGeneralManager() {
        Optional<Node> paginationNode = generalManager.getUIElement(UIElementName.SEARCHED_ITEM_PAGINATION);
        assert paginationNode.isPresent() : "Expected generalManager to have a SEARCHED_ITEM_PAGINATION element, but it was not found.";
        assert paginationNode.get() instanceof SearchedItemPagination : "Expected SEARCHED_ITEM_PAGINATION element to be an instance of SearchedItemPagination, but got: " + paginationNode.get().getClass();    
    }

    @Nested
    @SuppressWarnings("unused")
    @DisplayName("Tests for getPreviousPage and getNextPage")
    class TestGetPreviousAndNextPage {
        private static Method getNextPageMethod, getPreviousPageMethod;

        @BeforeAll
        public static void setup() throws NoSuchMethodException {
            getNextPageMethod = SearchedItemPagination.class.getDeclaredMethod("getNextPage");
            getNextPageMethod.setAccessible(true);
            getPreviousPageMethod = SearchedItemPagination.class.getDeclaredMethod("getPreviousPage");
            getPreviousPageMethod.setAccessible(true);
        }
        
        @Test
        @DisplayName("Test showNextPage normally")
        public void testShowNextPageNormal() throws IllegalAccessException, InvocationTargetException {
            @SuppressWarnings("unchecked")
            List<Item> secondPageItems = (List<Item>) getNextPageMethod.invoke(pagination);
            
            assert secondPageItems != null : "Expected getNextPage to return a list of items for the next page, but got null.";
            assert secondPageItems.size() == 10 : "Expected getNextPage to return a list of 10 items for the next page, but got: " + secondPageItems.size();
        }

        @Test
        @DisplayName("Test showNextPage at end of pages")
        public void testShowNextPageAtEnd() throws IllegalAccessException, InvocationTargetException {
            int numberOfPages = 97;//note that the first page is already loaded, so we only need to call getNextPage 96 more times to reach the end
            for (int i = 1; i < numberOfPages; i++)
                getNextPageMethod.invoke(pagination);

            @SuppressWarnings("unchecked")
            List<Item> result = (List<Item>) getNextPageMethod.invoke(pagination);

            assert result == null : "Expected getNextPage to return null when trying to go past the end of the pages, but got: " + result;
        }

        @Test
        @DisplayName("Test showPreviousPage at start of pages")
        public void testShowPreviousPageAtStart() throws IllegalAccessException, InvocationTargetException {
            @SuppressWarnings("unchecked")
            List<Item> result = (List<Item>) getPreviousPageMethod.invoke(pagination);

            assert result == null : "Expected getPreviousPage to return null when trying to go before the start of the pages, but got: " + result;
        }

        @Test
        @DisplayName("Test showPreviousPage normally")
        public void testShowPreviousPageNormal() throws IllegalAccessException, InvocationTargetException {
            //First go to the second page
            getNextPageMethod.invoke(pagination);

            @SuppressWarnings("unchecked")
            List<Item> firstPageItems = (List<Item>) getPreviousPageMethod.invoke(pagination);
            
            assert firstPageItems != null : "Expected getPreviousPage to return a list of items for the previous page, but got null.";
            assert firstPageItems.size() == 10 : "Expected getPreviousPage to return a list of 10 items for the previous page, but got: " + firstPageItems.size();
        }
    }

    @Nested
    @SuppressWarnings("unused")
    @DisplayName("Tests for the previous and next buttons in the pagination UI")
    class TestPaginationButtons {
        //Tests for the pagination buttons would go here, but would require more extensive setup to properly test the UI interactions, so they are not included in this test suite.
        private static Field prevButtonField, nextButtonField;

        @BeforeAll
        public static void setup() throws NoSuchFieldException {
            prevButtonField = SearchedItemPagination.class.getDeclaredField("prevButton");
            prevButtonField.setAccessible(true);
            nextButtonField = SearchedItemPagination.class.getDeclaredField("nextButton");
            nextButtonField.setAccessible(true);
        }

        @Test
        @DisplayName("Test that Previous button is set up to call getPreviousPage and update items")
        public void testPreviousButtonAction() throws Exception {
            try (MockedStatic<SearchedItemContainer> mockSearchedItemContainerCreate = Mockito.mockStatic(SearchedItemContainer.class)) {
                SearchedItemContainer mockContainer = Mockito.mock(SearchedItemContainer.class);
                mockSearchedItemContainerCreate.when(() -> SearchedItemContainer.create(Mockito.anyList(), Mockito.any())).thenReturn(mockContainer);

                SearchedItemPagination paginationWithMockContainer = new SearchedItemPagination(itemHashMap, null);

                //Inject a mock pageList that returns a non-null list for getPrevious()
                Field pageListField = SearchedItemPagination.class.getDeclaredField("pageList");
                pageListField.setAccessible(true);
                SearchedItemsLinkedList mockPageList = Mockito.mock(SearchedItemsLinkedList.class);
                Mockito.when(mockPageList.getPrevious()).thenReturn(Collections.singletonList(Mockito.mock(Item.class)));
                pageListField.set(paginationWithMockContainer, mockPageList);

                Button prevButton = (Button) prevButtonField.get(paginationWithMockContainer);
                prevButton.fire();

                Mockito.verify(mockContainer).updateItems(Mockito.anyList());
            }
        }

        @Test
        @DisplayName("Test that Next button is set up to call getNextPage and update items")
        public void testNextButtonAction() throws Exception {
            try (MockedStatic<SearchedItemContainer> mockSearchedItemContainerCreate = Mockito.mockStatic(SearchedItemContainer.class)) {
                SearchedItemContainer mockContainer = Mockito.mock(SearchedItemContainer.class);
                mockSearchedItemContainerCreate.when(() -> SearchedItemContainer.create(Mockito.anyList(), Mockito.any())).thenReturn(mockContainer);

                SearchedItemPagination paginationWithMockContainer = new SearchedItemPagination(itemHashMap, null);

                //Inject a mock pageList that returns a non-null list for getNext()
                Field pageListField = SearchedItemPagination.class.getDeclaredField("pageList");
                pageListField.setAccessible(true);
                SearchedItemsLinkedList mockPageList = Mockito.mock(SearchedItemsLinkedList.class);
                Mockito.when(mockPageList.getNext()).thenReturn(Collections.singletonList(Mockito.mock(Item.class)));
                pageListField.set(paginationWithMockContainer, mockPageList);

                Button nextButton = (Button) nextButtonField.get(paginationWithMockContainer);
                nextButton.fire();

                Mockito.verify(mockContainer).updateItems(Mockito.anyList());
            }
        }
    }
}
