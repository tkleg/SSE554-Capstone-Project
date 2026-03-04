package org.troy.capstone.search_engine;

import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.selection.Selection;

public class SearchEngineTest {
    private SearchEngine searchEngine;
    private static final float minMinPrice = 8.619999885559082f;
    private static final float maxMaxPrice = 799.0599975585938f;

    @BeforeEach
    public void setup() {
        searchEngine = new SearchEngine(TableUtils.readCleanedAttributedData());
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
}
