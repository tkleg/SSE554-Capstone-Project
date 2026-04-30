package org.troy.capstone.search_engine;

import java.util.Map;
import java.util.Set;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.UIDataName;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

/**
 * The {@code SearchEngine} class is responsible for filtering items based on various criteria such as price range, star rating, categorical filters, and search queries. It utilizes a {@code PriceRangeFinder} for efficient price range filtering and a {@code QueryFilter} for handling search queries with Lucene.
 */
public class SearchEngine {
    /** The original table containing all item data, used for filtering and retrieving item information. */
    private final Table table;
    /** The {@code PriceFilter} for efficiently finding items within a specified price range. */
    private final PriceFilter priceFilter;
    /** The {@code QueryFilter} for handling search queries using a Lucene index built from the item data. */
    private final QueryFilter queryFilter;

    /**
     * Constructor for {@code SearchEngine}, filled from a {@code Table}.
     * 
     * @param table The {@code Table} containing the data to be searched.
     */
    public SearchEngine(Table table) {
        this.table = table;
        this.priceFilter = new PriceFilter(table);
        this.queryFilter = new QueryFilter(table);
    }

    /**
     * Filters the data by categorical filter selections. For tags, we use AND so all tags are there as multiple can be selected. For other categorical filters, we use OR since only one value is there.
     * 
     * @param searchData The search data containing the filters to be applied.
     * @return The table containing the items that match the search criteria.
    */
    public Table filterItems(Map<UIDataName, Object> searchData) {

        Table filteredTable = table.copy();

        //Filter price
        filteredTable = applyPriceFilters(searchData, filteredTable);
        System.out.println("After price filter: " + filteredTable.rowCount() + " items");
        
        if( !filteredTable.isEmpty() ){
            //Filter star rating
            filteredTable = applyStarFilter(searchData, filteredTable);
            System.out.println("After star rating filter: " + filteredTable.rowCount() + " items");
        }

        if( !filteredTable.isEmpty() ){
            //Apply categorical filters
            filteredTable = applyCategoricalFilters(searchData, filteredTable);
            System.out.println("After categorical filters: " + filteredTable.rowCount() + " items");
        }
        Table queryFilteredTable = filteredTable;
        
        //Filter search query. Add a relevance column in filterItems if not added by the search query filter to ensure that the column is always present for sorting in the UI.
        if( !filteredTable.isEmpty() )
            queryFilteredTable = applySearchQueryFilter((String)searchData.get(UIDataName.SEARCH_QUERY), filteredTable);
        
        if( queryFilteredTable != filteredTable ){
            System.out.println("After applying search query filter: " + queryFilteredTable.rowCount() + " items");
            System.out.print(queryFilteredTable.selectColumns(TableColumnName.NAME.getColumnName(), TableColumnName.RELEVANCE.getColumnName()));
        }else{
            System.out.println("Search query filter not applied. Adding empty relevance column with default value of 0 for all items.");
            FloatColumn relevanceColumn = FloatColumn.create(TableColumnName.RELEVANCE.getColumnName(), filteredTable.rowCount());
            for (int i = 0; i < relevanceColumn.size(); i++)
                relevanceColumn.set(i, 0f);
            queryFilteredTable = queryFilteredTable.addColumns(relevanceColumn);
        }
        System.out.println("Number of results: " + queryFilteredTable.rowCount());
        System.out.println("Total Data Size: " + table.rowCount());

        return queryFilteredTable;
    }

    /**
     * Helper method to apply the search query results as a filter on the table.
     * 
     * @param userQuery The {@code userQuery} to be applied as a filter.
     * @param preQueryFilteredTable The {@code Table} after applying all filters except the search query filter, so that the search query filter is only applied to the already filtered items for better performance.
     * @return The filtered {@code Table} with search results, or the original {@code Table} if no filtering was applied.
     */
    public Table applySearchQueryFilter(String userQuery, Table preQueryFilteredTable) {
        System.out.println("Applying search query filter with user query: \"" + userQuery + "\"");
    
        Map<String, Float> searchResults = queryFilter.search(userQuery);

        if( searchResults.isEmpty() )
            return preQueryFilteredTable;
        else{
            StringColumn idColumn = preQueryFilteredTable.stringColumn(TableColumnName.ID.getColumnName());
            FloatColumn relevanceColumn = FloatColumn.create(TableColumnName.RELEVANCE.getColumnName(), preQueryFilteredTable.rowCount());
            for( int i = 0; i < idColumn.size(); i++ )
                relevanceColumn.set(i, searchResults.getOrDefault(idColumn.get(i), 0f));
            Table tableWithRelevance = preQueryFilteredTable.addColumns(relevanceColumn);
            return tableWithRelevance.where( tableWithRelevance.floatColumn(TableColumnName.RELEVANCE.getColumnName()).isGreaterThan(0) );
        }
    }

    /**
     * Helper method to apply tag filters since they have special handling compared to other categorical filters.
     * 
     * @param filtersContainer The filters container containing the selected tags under the "Tags" key.
     * @param filteredTable The table to apply the tag filters on.
     * @return The selection of items that match the selected tags. Returns ALL items if no tags are selected or if the selected tags value is not found in the filters container.
     */
    private Table applyTagFilters(Map<String, Set<String>> filtersContainer, Table filteredTable) {
        Set<String> selectedTags = filtersContainer.get("Tags");
        Selection tagSelection = selectAll(filteredTable.rowCount());

        if( selectedTags == null ){
            System.out.println("Selected tags value not found in filters container. Skipping tag filter.");
            return filteredTable;
        }

        if( selectedTags.size() > 4 ){
            System.out.println("More than 4 tags selected, no items are possibly matching since max tags per item is 4.");
            return filteredTable.emptyCopy();
        }
        if( !selectedTags.isEmpty() )
            for( String selectedTag : selectedTags )
                tagSelection = tagSelection.and( filteredTable.stringColumn(TableColumnName.TAGS.getColumnName()).lowerCase().containsString(selectedTag.toLowerCase()) );
        else
            System.out.println("No tags selected, skipping tags filter.");

        return filteredTable.where(tagSelection);
    }

    /**
     * Helper method to apply the star rating filter.
     * 
     * @param searchData The search data containing the minimum star rating.
     * @param filteredTable The table to apply the star rating filter on.
     * @return The table of items that match the minimum star rating. Returns the original table if the minimum star rating value is not found in the search data or is not of the expected type.
     */
    private Table applyStarFilter(Map<UIDataName, Object> searchData, Table filteredTable) {
        Integer minStarRating;
        try{
            minStarRating = (Integer) searchData.get(UIDataName.MIN_STAR_RATING);
        }catch(ClassCastException e){
            System.out.println("Min star rating value in search data is not of type Integer. Skipping star rating filter.");
            return filteredTable;
        }

        if( minStarRating == null ){
            System.out.println("Min star rating value not found in search data. Skipping star rating filter.");
            return filteredTable;
        }

        if( minStarRating < 0 ){
            System.out.println("Min star rating value out of expected range (0-5). Skipping star rating filter.");
            return filteredTable;
        }
        
        if( minStarRating > 5 ){
            System.out.println("Min star rating value out of expected range (0-5). Skipping star rating filter.");
            return filteredTable;
        }
        
        return filteredTable.where(filteredTable.floatColumn(TableColumnName.REVIEW_SCORE.getColumnName()).isGreaterThanOrEqualTo(minStarRating.doubleValue()));
    }

    /**
     * Helper method to apply price filters.
     * 
     * @param searchData The search data containing the minimum and/or maximum price.
     * @param filteredTable The table to apply the price filter on.
     * @return The table of items that match the price criteria. Returns the original table if the minimum or maximum price value are not of the expected type.
     */
    private Table applyPriceFilters(Map<UIDataName, Object> searchData, Table filteredTable) {
        float minPrice, maxPrice;
        try{
            minPrice = (float) searchData.getOrDefault(UIDataName.MIN_PRICE,
                (float) filteredTable.floatColumn(TableColumnName.PRICE.getColumnName()).min()
            );
            maxPrice = (float) searchData.getOrDefault(UIDataName.MAX_PRICE,
                (float) filteredTable.floatColumn(TableColumnName.PRICE.getColumnName()).max()
            );
        }catch(ClassCastException e){
            System.out.println("Min or max price value in search data is not of type Float. Skipping price filters.");
            return filteredTable;
        }

        if( minPrice > maxPrice ){
            System.out.println("Min price is greater than max price. No items can match this criteria.");
            return filteredTable.emptyCopy();
        }

        int[] itemIndicesInRange = priceFilter.filterByPriceRange(minPrice, maxPrice);
        return filteredTable.where(Selection.with(itemIndicesInRange));
    }

    /**
     * Helper method to apply categorical filters (other than tags which have special handling).
     * 
     * @param searchData The search data containing the selected categorical filters under the {@code FILTERS_CONTAINER} key.
     * @param filteredTable The table to apply the categorical filters on.
     * @return The table of items that match the selected categorical filters, or the original table if no valid filters are found in the search data.
     */
    @SuppressWarnings("unchecked")
    private Table applyCategoricalFilters(Map<UIDataName, Object> searchData, Table filteredTable) {
        Set<TableColumnName> categoricalColumns = TableColumnName.getCategoricalColumns();
        System.out.println("Starting categorical filters with " + filteredTable.rowCount() + " items");
        Map<String, Set<String>> filtersContainer;
        try{ 
            filtersContainer = (Map<String, Set<String>>)searchData.get(UIDataName.FILTERS_CONTAINER);
            System.out.println("Retrieved filters container from search data for categorical filters: " + filtersContainer);
        }catch(ClassCastException e){
            System.out.println("Filters container in search data is not of type Map<String, Set<String>>. Skipping categorical filters.");
            return filteredTable;
        }
        if( filtersContainer == null ){
            System.out.println("Filters container not found in search data. Skipping categorical filters.");
            return filteredTable;
        }

        for( TableColumnName column : categoricalColumns ) {
            //Tags has special handling since it's a set of strings, so outsource the handling
            if( column == TableColumnName.TAGS ){
                filteredTable = applyTagFilters(filtersContainer, filteredTable);
                continue;
            }

            //Convert enum to the string key format used by FiltersContainer
            String filterKey = column.getColumnName().substring(0, 1).toUpperCase() + column.getColumnName().substring(1).toLowerCase();
            Set<String> selectedValues = filtersContainer.get(filterKey);

            if( selectedValues != null && !selectedValues.isEmpty() ) {
                Selection columnSelection = selectAll(filteredTable.rowCount());

                //Combine selected values for the column with OR
                for( String value : selectedValues ) {
                    Selection valueSelection = filteredTable.stringColumn(column.getColumnName()).lowerCase().isEqualTo(value.toLowerCase());
                    if( columnSelection.size() == filteredTable.rowCount() )//First value for the column, so initialize the column selection to this value's selection
                        columnSelection = valueSelection;
                    else
                        columnSelection = columnSelection.or(valueSelection);
                }

                filteredTable = filteredTable.where(columnSelection);
                System.out.println("After applying " + filterKey + " filter: " + filteredTable.rowCount() + " items selected for cateogries. Not including non-categorical filters.");
            }else if( selectedValues != null )//Empty but not null
                System.out.println("No values selected for " + filterKey + ", skipping " + filterKey + " filter.");
            else//Null, meaning the key was not found in the filters container
                System.out.println("Filter key " + filterKey + " not found in filters container, skipping " + filterKey + " filter.");
        }

        System.out.println("Final categorical filter result: " + filteredTable.rowCount() + " items");
        return filteredTable;
    }

    /** Select all rows in the table.
     * 
     * @param rowCount The number of rows in the table to create a selection that includes all rows.
     * @return A {@code Selection} object that includes all rows in the table.
    */
    private Selection selectAll(int rowCount){
        return Selection.withRange(0, rowCount);
    }

}
