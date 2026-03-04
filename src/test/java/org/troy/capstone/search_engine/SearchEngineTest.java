package org.troy.capstone.search_engine;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.selection.Selection;

public class SearchEngineTest {
    private static SearchEngine searchEngine;

    @BeforeAll
    public static void setup() {
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

}
