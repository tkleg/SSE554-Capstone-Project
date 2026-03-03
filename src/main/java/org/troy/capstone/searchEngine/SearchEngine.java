package org.troy.capstone.searchEngine;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.constants.uiDataNames;
import org.troy.capstone.data_structures.PriceRangeFinder;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class SearchEngine {
    private final Table table;
    private final PriceRangeFinder priceRangeFinder;

    public SearchEngine(Table table) {
        this.table = table;
        this.priceRangeFinder = new PriceRangeFinder(table);
    }

    //For tags, we use AND so all tags are there as multiple can be selected
    //For other categorical filters, we use OR since only one value is there
    public Set<String> filterItems(Map<uiDataNames, Object> searchData) {
        Selection selection = Selection.withRange(0, table.rowCount());

        //Filter price
        Selection priceResult = applyPriceFilters(searchData);
        if( priceResult != null ) {
            selection = priceResult;
            System.out.println("After price filter: " + selection.size() + " items");
        } else
            System.out.println("Price filter not applied.");
        
        //Filter star rating
        Selection starResult = applyStarFilter(searchData);
        if( starResult != null ) {
            selection = selection.and(starResult);
            System.out.println("After star rating filter: " + selection.size() + " items");
        } else
            System.out.println("Star rating filter not applied.");

        //Apply categorical filters
        Selection categoricalResult = applyCategoricalFilters(searchData);
        if( categoricalResult != null ) {
            selection = selection.and(categoricalResult);
            System.out.println("After categorical filters: " + selection.size() + " items");
        } else
            System.out.println("Categorical filters not applied.");

        System.out.println("Number of results: " + selection.size());
        System.out.println("Total Data Size: " + table.rowCount());

        return table.where(selection).stringColumn(tableColumns.ID.getColumnName()).asSet();
    }

    private Selection applyTagFilters(Map<String, Set<String>> filtersContainer) {
        Set<String> selectedTags;
        Selection tagSelection = Selection.withRange(0, table.rowCount());
        try{
            selectedTags = filtersContainer.get("Tags");
        }catch(ClassCastException e){
            System.out.println("Selected tags value in filters container is not of type Set<String>. Skipping tag filter.");
            return null;
        }catch(NullPointerException e){
            System.out.println("Selected tags value not found in filters container. Skipping tag filter.");
            return null;
        }
        if( !( selectedTags == null || selectedTags.isEmpty() ) )
            for( String selectedTag : selectedTags )
                tagSelection = tagSelection.and( table.stringColumn(tableColumns.TAGS.getColumnName()).lowerCase().containsString(selectedTag.toLowerCase()) );
        else if( selectedTags == null )
            System.out.println("Tags filter key not found in filters container, skipping tags filter.");
        else
            System.out.println("No tags selected, skipping tags filter.");
        
        return tagSelection;
    }

    private Selection applyStarFilter(Map<uiDataNames, Object> searchData) {
        Integer minStarRating;
        try{
            minStarRating = (Integer) searchData.get(uiDataNames.MIN_STAR_RATING);
        }catch(ClassCastException e){
            System.out.println("Min star rating value in search data is not of type Integer. Skipping star rating filter.");
            return null;
        }catch(NullPointerException e){
            System.out.println("Min star rating value not found in search data. Skipping star rating filter.");
            return null;
        }
        return table.floatColumn(tableColumns.REVIEW_SCORE.getColumnName()).isGreaterThanOrEqualTo(minStarRating.doubleValue());
    }

    private Selection applyPriceFilters(Map<uiDataNames, Object> searchData) {
        Double minPrice, maxPrice;
        try{
            minPrice = (Double) searchData.get(uiDataNames.MIN_PRICE);
            maxPrice = (Double) searchData.get(uiDataNames.MAX_PRICE);
        }catch(ClassCastException e){
            System.out.println("min and/or max price values in search data are not of type Double. Skipping price filter.");
            return null;
        }catch(NullPointerException e){
            System.out.println("min and/or max price values not found in search data. Skipping price filter.");
            return null;
        }
        List<String> idsInRange = priceRangeFinder.findItemsInPriceRange(minPrice.floatValue(), maxPrice.floatValue());
        return table.stringColumn(tableColumns.ID.getColumnName()).isIn(idsInRange);

    }

    private Selection applyCategoricalFilters(Map<uiDataNames, Object> searchData) {
        Set<tableColumns> categoricalColumns = tableColumns.getCategoricalColumns();
        Selection categoricalSelection = Selection.withRange(0, table.rowCount());
        Map<String, Set<String>> filtersContainer;
        try{ 
            filtersContainer = (Map<String, Set<String>>)searchData.get(uiDataNames.FILTERS_CONTAINER);
        }catch(ClassCastException e){
            System.out.println("Filters container in search data is not of type Map<String, Set<String>>. Skipping categorical filters.");
            return null;
        }catch(NullPointerException e){
            System.out.println("Filters container not found in search data. Skipping categorical filters.");
            return null;
        }

        for( tableColumns column : categoricalColumns ) {
            //Tags has special handling since it's a set of strings, so outsource the handling
            if( column == tableColumns.TAGS ){
                Selection tagResult = applyTagFilters(filtersContainer);
                if( tagResult != null )
                    categoricalSelection = tagResult;
                continue;
            }

            //Convert enum to the string key format used by FiltersContainer
            String filterKey = column.getColumnName().substring(0, 1).toUpperCase() + column.getColumnName().substring(1).toLowerCase();
            Set<String> selectedValues = filtersContainer.get(filterKey);

            if( !( selectedValues == null || selectedValues.isEmpty() ) ) {
                Selection columnSelection = null;

                //Combine selected values for the column with OR
                for( String value : selectedValues ) {
                    Selection valueSelection = table.stringColumn(column.getColumnName()).lowerCase().isEqualTo(value.toLowerCase());
                    columnSelection = (columnSelection == null) ? valueSelection : columnSelection.or(valueSelection);
                }

                //Ensure that one of the selected values for the column is present with AND
                if( columnSelection != null ) {
                    categoricalSelection = categoricalSelection.and(columnSelection);
                    System.out.println("After applying " + filterKey + " filter: " + categoricalSelection.size() + " items selected for cateogries. Not including non-categorical filters.");
                }else
                    System.out.println("Column selection for " + filterKey + " is null, skipping " + filterKey + " filter.");
            }else if( selectedValues == null )
                System.out.println("Filter key " + filterKey + " not found in filters container, skipping " + filterKey + " filter.");
            else
                System.out.println("No values selected for " + filterKey + ", skipping " + filterKey + " filter.");
        }

        return categoricalSelection;
    }



}
