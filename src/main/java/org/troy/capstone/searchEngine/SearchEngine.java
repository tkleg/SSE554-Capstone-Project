package org.troy.capstone.searchEngine;

import java.util.Map;
import java.util.Set;

import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.constants.uiDataNames;
import org.troy.capstone.uiComponents.items.searched.SearchedItemPagination;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class SearchEngine {
    private final Table table;
    private SearchedItemPagination searchedItemPagination = null;

    public SearchEngine(Table table) {
        this.table = table;
    }

    public void setSearchedItemPagination(SearchedItemPagination searchedItemPagination) {
        this.searchedItemPagination = searchedItemPagination;
    }

    //For tags, we use AND so all tags are there as multiple can be selected
    //For other categorical filters, we use OR since only one value is there
    private Set<String> filterItems(Map<uiDataNames, Object> searchData) {
        //Filter price
        Selection selection = table.floatColumn(tableColumns.PRICE.getColumnName()).isBetweenInclusive((double) searchData.get(uiDataNames.MIN_PRICE), (double) searchData.get(uiDataNames.MAX_PRICE));
        System.out.println("After price filter: " + selection.size() + " items");
        
        //Filter star rating
        selection = selection.and( table.floatColumn(tableColumns.REVIEW_SCORE.getColumnName()).isGreaterThanOrEqualTo(((Integer) searchData.get(uiDataNames.MIN_STAR_RATING)).doubleValue()) );
        System.out.println("After rating filter: " + selection.size() + " items");

        //Tags require special handling since it's a set of strings
        Map<String, Set<String>> filtersContainer = (Map<String, Set<String>>)searchData.get(uiDataNames.FILTERS_CONTAINER);
        System.out.println(filtersContainer);
        Set<String> selectedTags = filtersContainer.get("Tags");
        if( selectedTags != null && !selectedTags.isEmpty() )
            for( String selectedTag : selectedTags )
                selection = selection.and( table.stringColumn(tableColumns.TAGS.getColumnName()).lowerCase().containsString(selectedTag.toLowerCase()) );
        System.out.println("After tags filter: " + selection.size() + " items");

        //Apply other categorical filters
        applyCategoricalFilters(selection, searchData);

        System.out.println("Number of results: " + selection.size());
        System.out.println("Total Data Size: " + table.rowCount());

        return table.where(selection).stringColumn(tableColumns.ID.getColumnName()).asSet();
    }

    public void resetItems(Map<uiDataNames, Object> searchData) {
        Set<String> filteredIDs = filterItems(searchData);
        searchedItemPagination.updateContent(filteredIDs);
    }


    
    private void applyCategoricalFilters(Selection currentSelection, Map<uiDataNames, Object> searchData) {
        Set<tableColumns> categoricalColumns = tableColumns.getCategoricalColumns();
        Map<String, Set<String>> filtersContainer = (Map<String, Set<String>>)searchData.get(uiDataNames.FILTERS_CONTAINER);

        for( tableColumns column : categoricalColumns ) {
            //Skip tags since it's already handled
            if( column == tableColumns.TAGS ) continue;

            //Convert enum to the string key format used by FiltersContainer
            String filterKey = column.getColumnName().substring(0, 1).toUpperCase() + column.getColumnName().substring(1).toLowerCase();
            Set<String> selectedValues = filtersContainer.get(filterKey);

            if( selectedValues != null && !selectedValues.isEmpty() ) {
                Selection columnSelection = null;

                //Combine selected values for the column with OR
                for( String value : selectedValues ) {
                    Selection valueSelection = table.stringColumn(column.getColumnName()).lowerCase().isEqualTo(value.toLowerCase());
                    columnSelection = (columnSelection == null) ? valueSelection : columnSelection.or(valueSelection);
                }

                if( columnSelection != null ) {
                    currentSelection = currentSelection.and(columnSelection);
                }
            }
        }

    }



}
