package org.troy.capstone.search_engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.UIDataName;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class SearchEngineTest {
    private SearchEngine searchEngine;
    private static final Table table = TestDataHolder.getTableCopy();
    private static final float MIN_MIN_PRICE = 8.619999885559082f;
    private static final float MAX_MAX_PRICE = 799.0599975585938f;

    //Make method variables to reference private methods in order to get coverage on those methods, since they contain branches that need testing and are not called directly by the public filterItems method, which is already well covered by the parameterized tests.
    private static Method applyPriceFiltersTest, applyStarFilterTest, applyTagFiltersTest, applyCategoricalFiltersTest;

    @BeforeAll
    public static void setupAll() throws NoSuchMethodException {
        applyPriceFiltersTest = SearchEngine.class.getDeclaredMethod("applyPriceFilters", Map.class);
        applyPriceFiltersTest.setAccessible(true);
        applyStarFilterTest = SearchEngine.class.getDeclaredMethod("applyStarFilter", Map.class);
        applyStarFilterTest.setAccessible(true);
        applyTagFiltersTest = SearchEngine.class.getDeclaredMethod("applyTagFilters", Map.class);
        applyTagFiltersTest.setAccessible(true);
        applyCategoricalFiltersTest = SearchEngine.class.getDeclaredMethod("applyCategoricalFilters", Map.class);
        applyCategoricalFiltersTest.setAccessible(true);
    }
    
    @BeforeEach
    public void setupEach() {
        searchEngine = new SearchEngine(table);
    }

    @ParameterizedTest
    @DisplayName("Test price range filter count")
    @CsvSource({
        "335.84516021256815, 750.1887073798845, 501",
        "28.805967568069317, 654.5709657258885, 750",
        "146.73451560799788, 231.7280637448834, 97",
        "8.619999885559082, 38.36773464105467, 47"
    })
    public void testPriceRangeFilter(float minPrice, float maxPrice, int expectedCount) throws IllegalAccessException, InvocationTargetException {
        Map<UIDataName, Object> searchData = new HashMap<>();
        searchData.put(UIDataName.MIN_PRICE, minPrice);
        searchData.put(UIDataName.MAX_PRICE, maxPrice);

        Selection result = (Selection) applyPriceFiltersTest.invoke(searchEngine, searchData);
        
        assert result != null;
        assert result.size() == expectedCount : "Expected " + expectedCount + " results, but got " + result.size();
    }

    @DisplayName("Test star rating filter count")
    @ParameterizedTest
    @CsvSource({
        "0, 961",
        "1, 961",
        "2, 765",
        "3, 551",
        "4, 350",
        "5, 150"
    })
    public void testStarRatingFilter(int minStarRating, int expectedCount) throws IllegalAccessException, InvocationTargetException {
        Map<UIDataName, Object> searchData = new HashMap<>();
        searchData.put(UIDataName.MIN_STAR_RATING, minStarRating);

        Selection result = (Selection) applyStarFilterTest.invoke(searchEngine, searchData);
        System.out.println("Min Star Rating: " + minStarRating + ", Result Count: " + result.size());
        assert result != null;
        assert result.size() == expectedCount : "Expected " + expectedCount + " results, but got " + result.size();
    }

    @ParameterizedTest
    @DisplayName("Test tag filter count")
    @CsvSource({
        "'Durable, New Arrival, Versatile', 20",
        "'Versatile, Modern, Bestseller, Premium', 3",
        "'Versatile, Modern, Bestseller, Premium, Giftable', 0",
    })
    public void testTagFilter(String tagsString, int expectedCount) throws IllegalAccessException, InvocationTargetException {
        Map<String, Set<String>> filtersContainer = new HashMap<>();
        
        if(!tagsString.isEmpty())
            filtersContainer.put("Tags", new HashSet<>(Arrays.asList(tagsString.split(", "))));
        else
            filtersContainer.put("Tags", new HashSet<>());
        
        System.out.println("Testing tag filter with tags: " + filtersContainer.get("Tags") + ", Expected Count: " + expectedCount);
        Selection result = (Selection) applyTagFiltersTest.invoke(searchEngine, filtersContainer);

        assert result.size() == expectedCount : "Expected " + expectedCount + " results, but got " + result.size();
    }

    @ParameterizedTest
    @DisplayName("Test categorical filters count")  
    @CsvSource({
        "'Versatile, Durable', 'Clothing, Sports & Outdoors', 'NorthPeak, UrbanNest', 4",
        "'Versatile, Durable, Modern, Portable, Essential', 'Clothing, Sports & Outdoors', 'NorthPeak, UrbanNest', 0",
        "'', '', '', 961",
        "'', '', 'BlueRiver Outfitters, Horizon Tech', 241",
        "'', '', 'Maple Street Press, BlueRiver Outfitters, Horizon Tech, UrbanNest, NorthPeak', 601",
        "'', 'Clothing, Electronics, Home & Kitchen', '', 484",
        "'', 'Clothing, Sports & Outdoors, Home & Kitchen, Office Supplies', '', 647",
        "'Durable, New Arrival, Versatile', '', '', 20",
        "'Versatile, Modern, Bestseller, Premium', '', '', 3",
        "'Versatile, Modern, Bestseller, Premium, Giftable', '', '', 0",
    })
    public void testCategoricalFilters(String tagsString, String categoriesString, String publisherString, int expectedCount) throws IllegalAccessException, InvocationTargetException {
        Map<String, Set<String>> filtersContainer = new HashMap<>();
        
        // Handle empty strings properly to avoid creating sets with empty string elements
        if(!tagsString.isEmpty())
            filtersContainer.put("Tags", new HashSet<>(Arrays.asList(tagsString.split(", "))));
        else
            filtersContainer.put("Tags", new HashSet<>());
        
        if(!categoriesString.isEmpty())
            filtersContainer.put("Category", new HashSet<>(Arrays.asList(categoriesString.split(", "))));
        else
            filtersContainer.put("Category", new HashSet<>());
        
        
        if(!publisherString.isEmpty())
            filtersContainer.put("Publisher", new HashSet<>(Arrays.asList(publisherString.split(", "))));
        else
            filtersContainer.put("Publisher", new HashSet<>());
        
        Map<UIDataName, Object> searchData = Map.of(
            UIDataName.FILTERS_CONTAINER, filtersContainer
        );

        Selection result = (Selection) applyCategoricalFiltersTest.invoke(searchEngine, searchData);

        assert result != null;
        assert result.size() == expectedCount : "Expected " + expectedCount + " results, but got " + result.size();
    }

    @ParameterizedTest
    @DisplayName("Test combined filters count")
    @CsvSource({
        "'Compact', 'Clothing, Books', 'NorthPeak, UrbanNest', -1, -1, -1, '', 19",
        "'Durable, New Arrival, Versatile', 'Clothing, Sports & Outdoors', 'NorthPeak, Maple Street Press, UrbanNest', -1, -1, -1, '', 1",
        "'Wireless, Bestseller', 'Clothing, Books, Office Supplies, Sports & Outdoors', 'Maple Street Press, Silverline Electronics, Summit Gear Co., BrightLeaf Publishing', 118.26167424649577, 676.6693076949581, 2, '', 15",
        "'Wireless, Bestseller', 'Clothing, Books, Office Supplies, Sports & Outdoors', 'Maple Street Press, Silverline Electronics, Summit Gear Co., BrightLeaf Publishing', 118.26167424649577, 676.6693076949581, 2, 'elec', 1",
        "'Durable, New Arrival, Versatile', '', '', -1, -1, -1, '', 20",
        "'', 'Home & Kitchen, Books, Clothing', '', -1, -1, -1, '', 482",
        "'', '', 'Maple Street Press, BlueRiver Outfitters, Horizon Tech, UrbanNest, NorthPeak', -1, -1, -1, '', 601",
        "'', '', '', -1, -1, 4, '', 350",
        "'', '', '', 115.92435440837701, 522.830966113716, -1, '', 481",
        "'', '', '', 115.92435440837701, 522.830966113716, 5, '', 83",
        "'', '', '', -1, -1, -1, 'elec', 304"
    })
    public void testCombinedFilters(String tagsString, String categoriesString, String publisherString, float minPrice, float maxPrice, int minStarRating, String query, int expectedCount) throws IllegalAccessException, InvocationTargetException {
        Map<String, Set<String>> filtersContainer = new HashMap<>();
        
        // Handle empty strings properly to avoid creating sets with empty string elements
        if(!tagsString.isEmpty())
            filtersContainer.put("Tags", new HashSet<>(Arrays.asList(tagsString.split(", "))));
        else
            filtersContainer.put("Tags", new HashSet<>());
        
        if(!categoriesString.isEmpty())
            filtersContainer.put("Category", new HashSet<>(Arrays.asList(categoriesString.split(", "))));
        else
            filtersContainer.put("Category", new HashSet<>());
        
        
        if(!publisherString.isEmpty())
            filtersContainer.put("Publisher", new HashSet<>(Arrays.asList(publisherString.split(", "))));
        else
            filtersContainer.put("Publisher", new HashSet<>());

        
        Map<UIDataName, Object> searchData = new HashMap<>();
        searchData.put(UIDataName.FILTERS_CONTAINER, filtersContainer);
        
        if( minPrice != -1 && maxPrice != -1 ){
            searchData.put(UIDataName.MIN_PRICE, minPrice);
            searchData.put(UIDataName.MAX_PRICE, maxPrice);
        } else {
            searchData.put(UIDataName.MIN_PRICE, MIN_MIN_PRICE);
            searchData.put(UIDataName.MAX_PRICE, MAX_MAX_PRICE);
        }
        if( minStarRating != -1 )
            searchData.put(UIDataName.MIN_STAR_RATING, minStarRating);
        else
            searchData.put(UIDataName.MIN_STAR_RATING, 0);

        if( query != null && !query.isEmpty() )
            searchData.put(UIDataName.SEARCH_QUERY, query);

        List<String> filteredIds = searchEngine.filterItems(searchData).stringColumn(TableColumnName.ID.getColumnName()).asList();
        
        assert filteredIds != null;
        assert filteredIds.size() == expectedCount : "Expected " + expectedCount + " results, but got " + filteredIds.size();
    }

    @Nested
    @DisplayName("Edge Cases for Search Engine Filters")
    @SuppressWarnings("unused")
    class SearchEngineEdgeCasesTest {
        //I looked at the code coverage report and looked for edge cases that need covering.

        private static final Map<String, Set<String>> emptyFiltersContainer = new HashMap<>();
        static {
            emptyFiltersContainer.put("Tags", new HashSet<>());
            emptyFiltersContainer.put("Category", new HashSet<>());
            emptyFiltersContainer.put("Publisher", new HashSet<>());
        }

        private static Map<UIDataName, Object> createStarClassCastErrorData() {
            Map<UIDataName, Object> data = new HashMap<>();
            data.put(UIDataName.MIN_STAR_RATING, "NotAnInteger");
            data.put(UIDataName.MIN_PRICE, MIN_MIN_PRICE);
            data.put(UIDataName.MAX_PRICE, MAX_MAX_PRICE);
            data.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);
            return data;
        }
        private static final Map<UIDataName, Object> starClassCastErrorData = createStarClassCastErrorData();
        
        @Test
        @DisplayName("Test star rating filter handling of ClassCastException when value is of wrong type - direct method call")
        public void testStarFilterClassCastExceptionHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Selection result = (Selection) applyStarFilterTest.invoke(searchEngine, starClassCastErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when star rating filter value is of wrong type, but got a different number of results.";
        }

        @Test
        @DisplayName("Test star rating filter handling of ClassCastException when value is of wrong type - method call through filterItems")
        public void testStarFilterClassCastExceptionHandlingIndirect() {
            List<String> filteredIds = searchEngine.filterItems(starClassCastErrorData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when star rating filter value is of wrong type, but got a different number of results.";
        }

        private static Map<UIDataName, Object> createStarNullErrorData() {
            Map<UIDataName, Object> data = new HashMap<>();
            data.put(UIDataName.MIN_PRICE, MIN_MIN_PRICE);
            data.put(UIDataName.MAX_PRICE, MAX_MAX_PRICE);
            data.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);
            return data;
        }
        private static final Map<UIDataName, Object> starNullErrorData = createStarNullErrorData();
        @Test
        @DisplayName("Test star rating filter handling of null value when star rating is missing - direct method call")
        public void testStarFilterNullHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Selection result = (Selection) applyStarFilterTest.invoke(searchEngine, starNullErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when star rating filter value is missing, but got a different number of results.";
        }
        @Test
        @DisplayName("Test star rating filter handling of null value when star rating is missing - method call through filterItems")
        public void testStarFilterNullHandlingIndirect() {
            List<String> filteredIds = searchEngine.filterItems(starNullErrorData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when star rating filter value is missing, but got a different number of results.";
        }

        private static Map<UIDataName, Object> createPriceClassCastErrorData() {
            Map<UIDataName, Object> data = new HashMap<>();
            data.put(UIDataName.MIN_STAR_RATING, 0);
            data.put(UIDataName.MIN_PRICE, "NotAFloat");
            data.put(UIDataName.MAX_PRICE, "NotAFloat");
            data.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);
            return data;
        }
        private static final Map<UIDataName, Object> priceClassCastErrorData = createPriceClassCastErrorData();
        @Test
        @DisplayName("Test price filter handling of ClassCastException when values are of wrong type - direct method call")
        public void testPriceFilterClassCastExceptionHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Selection result = (Selection) applyPriceFiltersTest.invoke(searchEngine, priceClassCastErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when price filter values are of wrong type, but got a different number of results.";
        }
        @Test
        @DisplayName("Test price filter handling of ClassCastException when values are of wrong type - method call through filterItems")
        public void testPriceFilterClassCastExceptionHandlingIndirect() {
            List<String> filteredIds = searchEngine.filterItems(priceClassCastErrorData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when price filter values are of wrong type, but got a different number of results.";
        }
        
        //Triggers the branch in applyPrice filters with null min price and non-null max price
        @Test
        @DisplayName("Test min price only null handling - direct method call")
        public void testMinPriceNullHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Map<UIDataName, Object> searchData = new HashMap<>();
            searchData.put(UIDataName.MIN_STAR_RATING, 0);
            searchData.put(UIDataName.MAX_PRICE, MAX_MAX_PRICE);
            searchData.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);

            Selection result = (Selection) applyPriceFiltersTest.invoke(searchEngine, searchData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when min price value is missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters with null min price and non-null max price
        @Test
        @DisplayName("Test min price only null handling - method call through filterItems")
        public void testMinPriceNullHandlingIndirect() {
            Map<UIDataName, Object> searchData = new HashMap<>();
            searchData.put(UIDataName.MIN_STAR_RATING, 0);
            searchData.put(UIDataName.MAX_PRICE, MAX_MAX_PRICE);
            searchData.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);

            List<String> filteredIds = searchEngine.filterItems(searchData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when min price value is missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters with null max price and non-null min price
        @Test
        @DisplayName("Test max price only null handling - direct method call")
        public void testMaxPriceNullHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Map<UIDataName, Object> searchData = new HashMap<>();
            searchData.put(UIDataName.MIN_STAR_RATING, 0);
            searchData.put(UIDataName.MIN_PRICE, MIN_MIN_PRICE);
            searchData.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);

            Selection result = (Selection) applyPriceFiltersTest.invoke(searchEngine, searchData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when max price value is missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters with null max price and non-null min price
        @Test
        @DisplayName("Test max price only null handling - method call through filterItems")
        public void testMaxPriceNullHandlingIndirect() {
            Map<UIDataName, Object> searchData = new HashMap<>();
            searchData.put(UIDataName.MIN_STAR_RATING, 0);
            searchData.put(UIDataName.MIN_PRICE, MIN_MIN_PRICE);
            searchData.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);

            List<String> filteredIds = searchEngine.filterItems(searchData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when max price value is missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters where both min and max price are null
        @Test
        @DisplayName("Test price filter handling of null values when both min and max price are missing - direct method call")
        public void testPriceFilterNullHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Map<UIDataName, Object> searchData = new HashMap<>(priceClassCastErrorData);
            searchData.remove(UIDataName.MIN_PRICE);
            searchData.remove(UIDataName.MAX_PRICE);

            Selection result = (Selection) applyPriceFiltersTest.invoke(searchEngine, searchData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when min and max price values are missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters where both min and max price are null
        @Test
        @DisplayName("Test price filter handling of null values when both min and max price are missing - method call through filterItems")
        public void testPriceFilterNullHandlingIndirect() {
            Map<UIDataName, Object> searchData = new HashMap<>(priceClassCastErrorData);
            searchData.remove(UIDataName.MIN_PRICE);
            searchData.remove(UIDataName.MAX_PRICE);

            List<String> filteredIds = searchEngine.filterItems(searchData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when min and max price values are missing, but got a different number of results.";
        }
        

        private static Map<UIDataName, Object> createCategoricalClassCastErrorData() {
            Map<UIDataName, Object> data = new HashMap<>();
            data.put(UIDataName.MIN_STAR_RATING, 0);
            data.put(UIDataName.MIN_PRICE, MIN_MIN_PRICE);
            data.put(UIDataName.MAX_PRICE, MAX_MAX_PRICE);
            data.put(UIDataName.FILTERS_CONTAINER, "NotAMap");
            return data;
        }
        private static final Map<UIDataName, Object> categoricalClassCastErrorData = createCategoricalClassCastErrorData();
        @Test
        @DisplayName("Test categorical filters handling of ClassCastException when filters container is of wrong type - direct method call")
        public void testCategoricalFilterClassCastExceptionHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Selection result = (Selection) applyCategoricalFiltersTest.invoke(searchEngine, categoricalClassCastErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when categorical filters container is of wrong type, but got a different number of results.";
        }

        @Test
        @DisplayName("Test categorical filters handling of ClassCastException when filters container is of wrong type - method call through filterItems")
        public void testCategoricalFilterClassCastExceptionHandlingIndirect() {
            List<String> filteredIds = searchEngine.filterItems(categoricalClassCastErrorData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when categorical filters container is of wrong type, but got a different number of results.";
        }

        private static Map<UIDataName, Object> createCategoricalNullErrorData() {
            Map<UIDataName, Object> data = new HashMap<>();
            data.put(UIDataName.MIN_STAR_RATING, 0);
            data.put(UIDataName.MIN_PRICE, MIN_MIN_PRICE);
            data.put(UIDataName.MAX_PRICE, MAX_MAX_PRICE);
            return data;
        }

        private static final Map<UIDataName, Object> categoricalNullErrorData = createCategoricalNullErrorData();

        @Test
        @DisplayName("Test categorical filters handling of null value when filters container is missing - direct method call")
        public void testCategoricalFilterNullHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Selection result = (Selection) applyCategoricalFiltersTest.invoke(searchEngine, categoricalNullErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when categorical filters container is missing, but got a different number of results.";
        }
        @Test
        @DisplayName("Test categorical filters handling of null value when filters container is missing - method call through filterItems")
        public void testCategoricalFilterNullHandlingIndirect() {
            List<String> filteredIds = searchEngine.filterItems(categoricalNullErrorData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when categorical filters container is missing, but got a different number of results.";
        }

        @Test
        @DisplayName("Test categorical filters handling of empty sets in filters container - direct method call")
        public void testCategoricalFilterEmptySetsHandlingDirect() throws IllegalAccessException, InvocationTargetException {
            Map<UIDataName, Object> searchData = new HashMap<>(categoricalNullErrorData);
            searchData.put(UIDataName.FILTERS_CONTAINER, Map.of());

            Selection result = (Selection) applyCategoricalFiltersTest.invoke(searchEngine, searchData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when categorical filters container has empty sets, but got a different number of results.";
        }

        @Test
        @DisplayName("Test categorical filters handling of empty sets in filters container - method call through filterItems")
        public void testCategoricalFilterEmptySetsHandlingIndirect() {
            Map<UIDataName, Object> searchData = new HashMap<>(categoricalNullErrorData);
            searchData.put(UIDataName.FILTERS_CONTAINER, Map.of());

            List<String> filteredIds = searchEngine.filterItems(searchData).stringColumn(TableColumnName.ID.getColumnName()).asList();
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when categorical filters container has empty sets, but got a different number of results.";
        }


    }
    
}
