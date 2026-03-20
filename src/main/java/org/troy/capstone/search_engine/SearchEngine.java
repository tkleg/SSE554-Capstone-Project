package org.troy.capstone.search_engine;

import java.util.Map;
import java.util.Set;
import java.util.List;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.UIDataName;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

/**
 * The SearchEngine class is responsible for filtering items based on various criteria such as price range, star rating, categorical filters, and search queries. It utilizes a PriceRangeFinder for efficient price range filtering and a QueryFilter for handling search queries with Lucene.
 */
public class SearchEngine {
    /** The original table containing all item data, used for filtering and retrieving item information. */
    private final Table table;
    /** The PriceFilter for efficiently finding items within a specified price range. */
    private final PriceFilter priceFilter;
    /** The QueryFilter for handling search queries using a Lucene index built from the item data. */
    private final QueryFilter queryFilter;

    /**
     * Constructor for SearchEngine, filled from a tablesaw Table.
     * 
     * @param table The table containing the data to be searched.
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
     * @return The list of item IDs that match the search criteria.
    */
    public List<String> filterItems(Map<UIDataName, Object> searchData) {
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

        //Apply filters prior to the search query so the query is only applied to filterd items to reduce time to apply the search query filter
        Table preQueryFilteredTable = table.where(selection);

        //Filter search query
        Table queryFilteredTable = applySearchQueryFilter((String)searchData.get(UIDataName.SEARCH_QUERY), preQueryFilteredTable);
        if( queryFilteredTable != preQueryFilteredTable ){
            System.out.println("After applying search query filter: " + queryFilteredTable.rowCount() + " items");
            System.out.print(queryFilteredTable.selectColumns(TableColumnName.NAME.getColumnName(), TableColumnName.RELEVANCE.getColumnName()));
        }else
            System.out.println("Search query filter not applied.");

        System.out.println("Number of results: " + queryFilteredTable.rowCount());
        System.out.println("Total Data Size: " + table.rowCount());

        return queryFilteredTable.stringColumn(TableColumnName.ID.getColumnName()).asList();
    }

    /**
     * Helper method to apply the search query results as a filter on the table.
     * 
     * @param userQuery The user query to be applied as a filter.
     * @param preQueryFilteredTable The table after applying all filters except the search query filter, so that the search query filter is only applied to the already filtered items for better performance.
     * @return The filtered table with search results, or the original table if no filtering was applied.
     */
    public Table applySearchQueryFilter(String userQuery, Table preQueryFilteredTable) {
        System.out.println("Applying search query filter with user query: \"" + userQuery + "\"");
        if( userQuery == null || userQuery.trim().isEmpty() ){
            System.out.println("User query is null or empty, skipping search query filter.");
            return preQueryFilteredTable;
        }

        Map<String, Float> searchResults = queryFilter.search(userQuery);

        if( searchResults == null || searchResults.isEmpty() )
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
     * @return The selection of items that match the selected tags. Returns ALL items if no tags are selected or if the selected tags value is not found in the filters container.
     */
    private Selection applyTagFilters(Map<String, Set<String>> filtersContainer) {
        Set<String> selectedTags = filtersContainer.get("Tags");
        Selection tagSelection = selectAll();

        if( selectedTags == null ){
            System.out.println("Selected tags value not found in filters container. Skipping tag filter.");
            return selectAll();
        }

        if( selectedTags.size() > 4 ){
            System.out.println("More than 4 tags selected, no items are possibly matching since max tags per item is 4.");
            return selectNone();//Indicates removing all results
        }
        if( !selectedTags.isEmpty() )
            for( String selectedTag : selectedTags )
                tagSelection = tagSelection.and( table.stringColumn(TableColumnName.TAGS.getColumnName()).lowerCase().containsString(selectedTag.toLowerCase()) );
        else
            System.out.println("No tags selected, skipping tags filter.");

        return tagSelection;
    }

    /**
     * Helper method to apply star rating filter.
     * 
     * @param searchData The search data containing the minimum star rating.
     * @return The selection of items that match the minimum star rating. Returns ALL items if the minimum star rating value is not found in the search data or is not of the expected type.
     */
    private Selection applyStarFilter(Map<UIDataName, Object> searchData) {
        Integer minStarRating;
        try{
            minStarRating = (Integer) searchData.get(UIDataName.MIN_STAR_RATING);
        }catch(ClassCastException e){
            System.out.println("Min star rating value in search data is not of type Integer. Skipping star rating filter.");
            return selectAll();
        }

        if( minStarRating == null ){
            System.out.println("Min star rating value not found in search data. Skipping star rating filter.");
            return selectAll();
        }

        return table.floatColumn(TableColumnName.REVIEW_SCORE.getColumnName()).isGreaterThanOrEqualTo(minStarRating.doubleValue());
    }

    /**
     * Helper method to apply price filters.
     * 
     * @param searchData The search data containing the minimum and/or maximum price.
     * @return The selection of items that match the price criteria. Returns ALL items if the minimum or maximum price value are not of the expected type.
     */
    private Selection applyPriceFilters(Map<UIDataName, Object> searchData) {
        float minPrice, maxPrice;
        try{
            minPrice = (float) searchData.getOrDefault(UIDataName.MIN_PRICE,
                (float) table.floatColumn(TableColumnName.PRICE.getColumnName()).min()
            );
            maxPrice = (float) searchData.getOrDefault(UIDataName.MAX_PRICE,
                (float) table.floatColumn(TableColumnName.PRICE.getColumnName()).max()
            );
        }catch(ClassCastException e){
            System.out.println("Min or max price value in search data is not of type Float. Skipping price filters.");
            return selectAll();
        }
        int[] itemIndicesInRange = priceFilter.filterByPriceRange(minPrice, maxPrice);
        return Selection.with(itemIndicesInRange);
    }

    /**
     * Helper method to apply categorical filters (other than tags which have special handling).
     * 
     * @param searchData The search data containing the selected categorical filters under the FILTERS_CONTAINER key.
     * @return The selection of items that match the selected categorical filters, or ALL_ITEMS if no valid filters are found in the search data.
     */
    @SuppressWarnings("unchecked")
    private Selection applyCategoricalFilters(Map<UIDataName, Object> searchData) {
        Set<TableColumnName> categoricalColumns = TableColumnName.getCategoricalColumns();
        Selection categoricalSelection = selectAll();
        System.out.println("Starting categorical filters with " + categoricalSelection.size() + " items");
        Map<String, Set<String>> filtersContainer;
        try{ 
            filtersContainer = (Map<String, Set<String>>)searchData.get(UIDataName.FILTERS_CONTAINER);
            System.out.println("Retrieved filters container from search data for categorical filters: " + filtersContainer);
        }catch(ClassCastException e){
            System.out.println("Filters container in search data is not of type Map<String, Set<String>>. Skipping categorical filters.");
            return selectAll();
        }
        if( filtersContainer == null ){
            System.out.println("Filters container not found in search data. Skipping categorical filters.");
            return selectAll();
        }

        for( TableColumnName column : categoricalColumns ) {
            //Tags has special handling since it's a set of strings, so outsource the handling
            if( column == TableColumnName.TAGS ){
                Selection tagResult = applyTagFilters(filtersContainer);
                categoricalSelection = categoricalSelection.and(tagResult);
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

    /** Select all rows in the table.
     * 
     * @return A Selection object that includes all rows in the table.
    */
    private Selection selectAll(){
        return Selection.withRange(0, table.rowCount());
    }

    /** Select no rows in the table.
     * 
     * @return A Selection object that includes no rows in the table.
    */
    private Selection selectNone(){
        return Selection.withRange(0, 0);
    }

}
