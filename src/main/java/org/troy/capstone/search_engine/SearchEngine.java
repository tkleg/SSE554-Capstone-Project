package org.troy.capstone.search_engine;

import java.util.Map;
import java.util.Set;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.data_structures.PriceRangeFinder;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class SearchEngine {
    private final Selection ALL_ITEMS;
    private static final Selection NO_ITEMS = Selection.withRange(0, 0);
    private final Table table;
    private final PriceRangeFinder priceRangeFinder;

    /**
     * Constructor for SearchEngine
     * 
     * @param table (Table) : The table containing the data to be searched
     */
    public SearchEngine(Table table) {
        this.table = table;
        this.priceRangeFinder = new PriceRangeFinder(table);
        this.ALL_ITEMS = Selection.withRange(0, table.rowCount());
    }

    /**
     * For tags, we use AND so all tags are there as multiple can be selected
     * For other categorical filters, we use OR since only one value is there
     * 
     * pre-conditions: None, error handling is done within the method to allow for maximum flexibility and fault tolerance,
     * such as skipping filters if expected data is not found or of the wrong type
     * 
     * @param searchData (Map<UIDataName, Object>) : The search data containing the filters to be applied
     * @return Set<String> : The set of item IDs that match the search criteria
    */
    public Set<String> filterItems(Map<UIDataName, Object> searchData) {
        Selection selection;

        //Filter price
        Selection priceResult = applyPriceFilters(searchData);
        selection = priceResult;
        System.out.println("After price filter: " + selection.size() + " items");
        
        //Filter star rating
        Selection starResult = applyStarFilter(searchData);
        selection = selection.and(starResult);
            System.out.println("After star rating filter: " + selection.size() + " items");

        //Apply categorical filters
        Selection categoricalResult = applyCategoricalFilters(searchData);
        selection = selection.and(categoricalResult);
        System.out.println("After categorical filters: " + selection.size() + " items");

        System.out.println("Number of results: " + selection.size());
        System.out.println("Total Data Size: " + table.rowCount());

        return table.where(selection).stringColumn(TableColumnName.ID.getColumnName()).asSet();
    }

    /**
     * Helper method to apply tag filters since they have special handling compared to other categorical filters
     * 
     * @param filtersContainer (Map<String, Set<String>>) : The filters container containing the selected tags under the "Tags" key
     * @return Selection : The selection of items that match the selected tags, or null if more than 4 tags are selected since it's impossible to satisfy that criteria
     */
    Selection applyTagFilters(Map<String, Set<String>> filtersContainer) {
        Set<String> selectedTags = filtersContainer.get("Tags");
        Selection tagSelection = ALL_ITEMS;

        if( selectedTags == null ){
            System.out.println("Selected tags value not found in filters container. Skipping tag filter.");
            return ALL_ITEMS;
        }

        if( selectedTags.size() > 4 ){
            System.out.println("More than 4 tags selected, no items are possibly matching since max tags per item is 4.");
            return null;//Indicates removing all results
        }
        if( !selectedTags.isEmpty() )
            for( String selectedTag : selectedTags )
                tagSelection = tagSelection.and( table.stringColumn(TableColumnName.TAGS.getColumnName()).lowerCase().containsString(selectedTag.toLowerCase()) );
        else
            System.out.println("No tags selected, skipping tags filter.");

        return tagSelection;
    }

    /**
     * Helper method to apply star rating filter
     * 
     * @param searchData (Map<UIDataName, Object>) : The search data containing the minimum star rating
     * @return Selection : The selection of items that match the minimum star rating
     */
    Selection applyStarFilter(Map<UIDataName, Object> searchData) {
        Integer minStarRating;
        try{
            minStarRating = (Integer) searchData.get(UIDataName.MIN_STAR_RATING);
        }catch(ClassCastException e){
            System.out.println("Min star rating value in search data is not of type Integer. Skipping star rating filter.");
            return ALL_ITEMS;
        }

        if( minStarRating == null ){
            System.out.println("Min star rating value not found in search data. Skipping star rating filter.");
            return ALL_ITEMS;
        }

        return table.floatColumn(TableColumnName.REVIEW_SCORE.getColumnName()).isGreaterThanOrEqualTo(minStarRating.doubleValue());
    }

    /**
     * Helper method to apply price filters
     * 
     * @param searchData (Map<UIDataName, Object>) : The search data containing the minimum and/or maximum price
     * @return Selection : The selection of items that match the price criteria
     */
    Selection applyPriceFilters(Map<UIDataName, Object> searchData) {
        Float minPrice, maxPrice;
        try{
            minPrice = (Float) searchData.get(UIDataName.MIN_PRICE);
            maxPrice = (Float) searchData.get(UIDataName.MAX_PRICE);
        }catch(ClassCastException e){
            System.out.println("min and/or max price values in search data are not of type Float. Skipping price filter.");
            return ALL_ITEMS;
        }
        if( minPrice == null && maxPrice != null ){
            System.out.println("Min price value not found in search data. Getting min from table");
            minPrice = (float) table.floatColumn(TableColumnName.PRICE.getColumnName()).min();
            int[] itemIndicesInRange = priceRangeFinder.findItemsInPriceRange(minPrice, maxPrice);
            return Selection.with(itemIndicesInRange);
        }else if( maxPrice == null && minPrice != null ){
            System.out.println("Max price value not found in search data. Getting max from table");
            maxPrice = (float) table.floatColumn(TableColumnName.PRICE.getColumnName()).max();
            int[] itemIndicesInRange = priceRangeFinder.findItemsInPriceRange(minPrice, maxPrice);
            return Selection.with(itemIndicesInRange);
        }else if( minPrice == null && maxPrice == null ){
            System.out.println("Min and max price values not found in search data. Skipping price filter.");
            return ALL_ITEMS;
        }else{
            int[] itemIndicesInRange = priceRangeFinder.findItemsInPriceRange(minPrice, maxPrice);
            return Selection.with(itemIndicesInRange);
        }
    }

    /**
     * Helper method to apply categorical filters (other than tags which have special handling)
      *
     * @param searchData (Map<UIDataName, Object>) : The search data containing the selected categorical filters under the FILTERS_CONTAINER key
     * @return Selection : The selection of items that match the selected categorical filters, or ALL_ITEMS if no valid filters are found in the search data
     */
    Selection applyCategoricalFilters(Map<UIDataName, Object> searchData) {
        Set<TableColumnName> categoricalColumns = TableColumnName.getCategoricalColumns();
        Selection categoricalSelection = ALL_ITEMS;
        System.out.println("Starting categorical filters with " + categoricalSelection.size() + " items");
        Map<String, Set<String>> filtersContainer;
        try{ 
            filtersContainer = (Map<String, Set<String>>)searchData.get(UIDataName.FILTERS_CONTAINER);
        }catch(ClassCastException e){
            System.out.println("Filters container in search data is not of type Map<String, Set<String>>. Skipping categorical filters.");
            return ALL_ITEMS;
        }
        if( filtersContainer == null ){
            System.out.println("Filters container not found in search data. Skipping categorical filters.");
            return ALL_ITEMS;
        }

        for( TableColumnName column : categoricalColumns ) {
            //Tags has special handling since it's a set of strings, so outsource the handling
            if( column == TableColumnName.TAGS ){
                Selection tagResult = applyTagFilters(filtersContainer);
                if( tagResult != null )
                    categoricalSelection = categoricalSelection.and(tagResult);
                else
                    return NO_ITEMS;//If tag result is null, it means more than 4 tags were selected which is impossible to satisfy since max tags per item is 4, so return empty selection
                continue;
            }

            //Convert enum to the string key format used by FiltersContainer
            String filterKey = column.getColumnName().substring(0, 1).toUpperCase() + column.getColumnName().substring(1).toLowerCase();
            Set<String> selectedValues = filtersContainer.get(filterKey);

            if( selectedValues != null && !selectedValues.isEmpty() ) {
                Selection columnSelection = null;

                //Combine selected values for the column with OR
                for( String value : selectedValues ) {
                    Selection valueSelection = table.stringColumn(column.getColumnName()).lowerCase().isEqualTo(value.toLowerCase());
                    if( columnSelection == null )
                        columnSelection = valueSelection;
                    else
                        columnSelection = columnSelection.or(valueSelection);
                }

                //Ensure that one of the selected values for the column is present with AND
                if( columnSelection != null ) {
                    categoricalSelection = categoricalSelection.and(columnSelection);
                    System.out.println("After applying " + filterKey + " filter: " + categoricalSelection.size() + " items selected for cateogries. Not including non-categorical filters.");
                }
            }else if( selectedValues != null )//Empty but not null
                System.out.println("No values selected for " + filterKey + ", skipping " + filterKey + " filter.");
            else//Null, meaning the key was not found in the filters container
                System.out.println("Filter key " + filterKey + " not found in filters container, skipping " + filterKey + " filter.");
        }

        System.out.println("Final categorical filter result: " + categoricalSelection.size() + " items");
        return categoricalSelection;
    }

}
