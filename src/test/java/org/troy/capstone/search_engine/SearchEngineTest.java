package org.troy.capstone.search_engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class SearchEngineTest {
    private SearchEngine searchEngine;
    private Table table;
    private static final double minMinPrice = 8.619999885559082;
    private static final double maxMaxPrice = 799.0599975585938;

    @BeforeEach
    public void setup() {
        table = TableUtils.readCleanedAttributedData();
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
    public void testPriceRangeFilter(double minPrice, double maxPrice, int expectedCount) {
        Map<UIDataName, Object> searchData = Map.of(
            UIDataName.MIN_PRICE, minPrice,
            UIDataName.MAX_PRICE, maxPrice
        );

        Selection result = searchEngine.applyPriceFilters(searchData);
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
    public void testStarRatingFilter(int minStarRating, int expectedCount) {
        Map<UIDataName, Object> searchData = Map.of(
            UIDataName.MIN_STAR_RATING, minStarRating
        );

        Selection result = searchEngine.applyStarFilter(searchData);
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
    public void testTagFilter(String tagsString, int expectedCount) {
        Map<String, Set<String>> filtersContainer = new HashMap<>();
        
        if(!tagsString.isEmpty())
            filtersContainer.put("Tags", new HashSet<>(Arrays.asList(tagsString.split(", "))));
        else
            filtersContainer.put("Tags", new HashSet<>());
        
        System.out.println("Testing tag filter with tags: " + filtersContainer.get("Tags") + ", Expected Count: " + expectedCount);
        Selection result = searchEngine.applyTagFilters(filtersContainer);
        
        if( expectedCount == 0 )
            assert result == null: "Expected no results for tags: " + filtersContainer.get("Tags") + ", but got some results.";
        else
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
    public void testCategoricalFilters(String tagsString, String categoriesString, String publisherString, int expectedCount) {
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

        Selection result = searchEngine.applyCategoricalFilters(searchData);

        System.out.println("=== Categorical Filter Debug ===");
        System.out.println("Tags: '" + tagsString + "' -> " + filtersContainer.get("Tags"));
        System.out.println("Categories: '" + categoriesString + "' -> " + filtersContainer.get("Category"));  
        System.out.println("Publishers: '" + publisherString + "' -> " + filtersContainer.get("Publisher"));
        System.out.println("Expected: " + expectedCount + ", Actual: " + result.size());
        System.out.println("=================================");

        assert result != null;
        assert result.size() == expectedCount : "Expected " + expectedCount + " results, but got " + result.size();
    }

    @ParameterizedTest
    @DisplayName("Test combined filters count")
    @CsvSource({
        "'Compact', 'Clothing, Books', 'NorthPeak, UrbanNest', -1, -1, -1, 19",
        "'Durable, New Arrival, Versatile', 'Clothing, Sports & Outdoors', 'NorthPeak, Maple Street Press, UrbanNest', -1, -1, -1, 1",
        "'', 'Clothing, Books, Office Supplies, Sports & Outdoors', 'Maple Street Press, Silverline Electronics, NorthPeak, UrbanNest', 118.26167424649577, 676.6693076949581, 2, 159",
        "'Durable, New Arrival, Versatile', '', '', -1, -1, -1, 20",
        "'', 'Home & Kitchen, Books, Clothing', '', -1, -1, -1, 482",
        "'', '', 'Maple Street Press, BlueRiver Outfitters, Horizon Tech, UrbanNest, NorthPeak', -1, -1, -1, 601",
        "'', '', '', -1, -1, 4, 350",
        "'', '', '', 115.92435440837701, 522.830966113716, -1, 481",
        "'', '', '', 115.92435440837701, 522.830966113716, 5, 83"
    })
    public void testCombinedFilters(String tagsString, String categoriesString, String publisherString, double minPrice, double maxPrice, int minStarRating, int expectedCount) {
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
            searchData.put(UIDataName.MIN_PRICE, minMinPrice);
            searchData.put(UIDataName.MAX_PRICE, maxMaxPrice);
        }
        if( minStarRating != -1 )
            searchData.put(UIDataName.MIN_STAR_RATING, minStarRating);
        else
            searchData.put(UIDataName.MIN_STAR_RATING, 0);

        Set<String> filteredIds = searchEngine.filterItems(searchData);

        System.out.println("=== Test Case Debug ===");
        System.out.println("Tags: '" + tagsString + "'");
        System.out.println("Categories: '" + categoriesString + "'");  
        System.out.println("Publishers: '" + publisherString + "'");
        System.out.println("Expected: " + expectedCount + ", Actual: " + filteredIds.size());
        System.out.println("========================");

        assert filteredIds != null;
        assert filteredIds.size() == expectedCount : "Expected " + expectedCount + " results, but got " + filteredIds.size();
    }

    @Nested
    class SearchEngineEdgeCasesTest {
        //I looked at the code coverage report and looked for edge cases that need covering.

        private static final Map<String, Set<String>> emptyFiltersContainer = Map.of(
            "Tags", new HashSet<>(),
            "Category", new HashSet<>(),
            "Publisher", new HashSet<>()
        );

        private static final Map<UIDataName, Object> starClassCastErrorData = Map.of(
                UIDataName.MIN_STAR_RATING, "NotAnInteger",
                UIDataName.MIN_PRICE, minMinPrice,
                UIDataName.MAX_PRICE, maxMaxPrice,
                UIDataName.FILTERS_CONTAINER, emptyFiltersContainer
            );
        
        @Test
        @DisplayName("Test star rating filter handling of ClassCastException when value is of wrong type - direct method call")
        public void testStarFilterClassCastExceptionHandlingDirect() {
            Selection result = searchEngine.applyStarFilter(starClassCastErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when star rating filter value is of wrong type, but got a different number of results.";
        }

        @Test
        @DisplayName("Test star rating filter handling of ClassCastException when value is of wrong type - method call through filterItems")
        public void testStarFilterClassCastExceptionHandlingIndirect() {
            Set<String> filteredIds = searchEngine.filterItems(starClassCastErrorData);
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when star rating filter value is of wrong type, but got a different number of results.";
        }

        private static final Map<UIDataName, Object> starNullErrorData = Map.of(
                UIDataName.MIN_PRICE, minMinPrice,
                UIDataName.MAX_PRICE, maxMaxPrice,
                UIDataName.FILTERS_CONTAINER, emptyFiltersContainer
            );
        @Test
        @DisplayName("Test star rating filter handling of null value when star rating is missing - direct method call")
        public void testStarFilterNullHandlingDirect() {
            Selection result = searchEngine.applyStarFilter(starNullErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when star rating filter value is missing, but got a different number of results.";
        }
        @Test
        @DisplayName("Test star rating filter handling of null value when star rating is missing - method call through filterItems")
        public void testStarFilterNullHandlingIndirect() {
            Set<String> filteredIds = searchEngine.filterItems(starNullErrorData);
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when star rating filter value is missing, but got a different number of results.";
        }

        private static final Map<UIDataName, Object> priceClassCastErrorData = Map.of(
                UIDataName.MIN_STAR_RATING, 0,
                UIDataName.MIN_PRICE, "NotADouble",
                UIDataName.MAX_PRICE, "NotADouble",
                UIDataName.FILTERS_CONTAINER, emptyFiltersContainer
            );
        @Test
        @DisplayName("Test price filter handling of ClassCastException when values are of wrong type - direct method call")
        public void testPriceFilterClassCastExceptionHandlingDirect() {
            Selection result = searchEngine.applyPriceFilters(priceClassCastErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when price filter values are of wrong type, but got a different number of results.";
        }
        @Test
        @DisplayName("Test price filter handling of ClassCastException when values are of wrong type - method call through filterItems")
        public void testPriceFilterClassCastExceptionHandlingIndirect() {
            Set<String> filteredIds = searchEngine.filterItems(priceClassCastErrorData);
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when price filter values are of wrong type, but got a different number of results.";
        }
        
        //Triggers the branch in applyPrice filters with null min price and non-null max price
        @Test
        @DisplayName("Test min price only null handling - direct method call")
        public void testMinPriceNullHandlingDirect() {
            Map<UIDataName, Object> searchData = new HashMap<>();
            searchData.put(UIDataName.MIN_STAR_RATING, 0);
            searchData.put(UIDataName.MAX_PRICE, maxMaxPrice);
            searchData.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);

            Selection result = searchEngine.applyPriceFilters(searchData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when min price value is missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters with null min price and non-null max price
        @Test
        @DisplayName("Test min price only null handling - method call through filterItems")
        public void testMinPriceNullHandlingIndirect() {
            Map<UIDataName, Object> searchData = new HashMap<>();
            searchData.put(UIDataName.MIN_STAR_RATING, 0);
            searchData.put(UIDataName.MAX_PRICE, maxMaxPrice);
            searchData.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);

            Set<String> filteredIds = searchEngine.filterItems(searchData);
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when min price value is missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters with null max price and non-null min price
        @Test
        @DisplayName("Test max price only null handling - direct method call")
        public void testMaxPriceNullHandlingDirect() {
            Map<UIDataName, Object> searchData = new HashMap<>();
            searchData.put(UIDataName.MIN_STAR_RATING, 0);
            searchData.put(UIDataName.MIN_PRICE, minMinPrice);
            searchData.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);

            Selection result = searchEngine.applyPriceFilters(searchData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when max price value is missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters with null max price and non-null min price
        @Test
        @DisplayName("Test max price only null handling - method call through filterItems")
        public void testMaxPriceNullHandlingIndirect() {
            Map<UIDataName, Object> searchData = new HashMap<>();
            searchData.put(UIDataName.MIN_STAR_RATING, 0);
            searchData.put(UIDataName.MIN_PRICE, minMinPrice);
            searchData.put(UIDataName.FILTERS_CONTAINER, emptyFiltersContainer);

            Set<String> filteredIds = searchEngine.filterItems(searchData);
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when max price value is missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters where both min and max price are null
        @Test
        @DisplayName("Test price filter handling of null values when both min and max price are missing - direct method call")
        public void testPriceFilterNullHandlingDirect() {
            Map<UIDataName, Object> searchData = new HashMap<>(priceClassCastErrorData);
            searchData.remove(UIDataName.MIN_PRICE);
            searchData.remove(UIDataName.MAX_PRICE);

            Selection result = searchEngine.applyPriceFilters(searchData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when min and max price values are missing, but got a different number of results.";
        }

        //Triggers the branch in applyPrice filters where both min and max price are null
        @Test
        @DisplayName("Test price filter handling of null values when both min and max price are missing - method call through filterItems")
        public void testPriceFilterNullHandlingIndirect() {
            Map<UIDataName, Object> searchData = new HashMap<>(priceClassCastErrorData);
            searchData.remove(UIDataName.MIN_PRICE);
            searchData.remove(UIDataName.MAX_PRICE);

            Set<String> filteredIds = searchEngine.filterItems(searchData);
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when min and max price values are missing, but got a different number of results.";
        }
        

        private static final Map<UIDataName, Object> categoricalClassCastErrorData = Map.of(
                UIDataName.MIN_STAR_RATING, 0,
                UIDataName.MIN_PRICE, minMinPrice,
                UIDataName.MAX_PRICE, maxMaxPrice,
                UIDataName.FILTERS_CONTAINER, "NotAMap"
            );
        @Test
        public void testCategoricalFilterClassCastExceptionHandlingDirect() {
            Selection result = searchEngine.applyCategoricalFilters(categoricalClassCastErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when categorical filters container is of wrong type, but got a different number of results.";
        }
        @Test
        public void testCategoricalFilterClassCastExceptionHandlingIndirect() {
            Set<String> filteredIds = searchEngine.filterItems(categoricalClassCastErrorData);
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when categorical filters container is of wrong type, but got a different number of results.";
        }

        private static final Map<UIDataName, Object> categoricalNullErrorData = Map.of(
                UIDataName.MIN_STAR_RATING, 0,
                UIDataName.MIN_PRICE, minMinPrice,
                UIDataName.MAX_PRICE, maxMaxPrice
            );
        @Test
        public void testCategoricalFilterNullHandlingDirect() {
            Selection result = searchEngine.applyCategoricalFilters(categoricalNullErrorData);
            assert result.size() == table.rowCount() : "Expected all items to be returned when categorical filters container is missing, but got a different number of results.";
        }
        @Test
        public void testCategoricalFilterNullHandlingIndirect() {
            Set<String> filteredIds = searchEngine.filterItems(categoricalNullErrorData);
            assert filteredIds.size() == table.rowCount() : "Expected all items to be returned when categorical filters container is missing, but got a different number of results.";
        }

    }
    
}
