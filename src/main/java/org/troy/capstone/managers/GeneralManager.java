package org.troy.capstone.managers;

import java.util.Map;
import java.util.Set;

import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.constants.uiDataNames;
import org.troy.capstone.constants.uiElementName;
import org.troy.capstone.uiComponents.items.searched.SearchedItemPagination;

import javafx.scene.Node;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class GeneralManager {
    private final UIElementManager uiManager;
    private final Table dataTable;

    public GeneralManager(Table table) {
        uiManager = new UIElementManager();
        dataTable = table;
    }

    public UIElementManager getUiManager() {
        return uiManager;
    }

    public Map<uiDataNames, Object> getSearchData() {
        return uiManager.getSearchData();
    }

    public void addUIElement(uiElementName key, Node element) {
        uiManager.addElement(key, element);
    }

    //For tags, we use AND so all tags are there as multiple can be selected
    //For other categorical filters, we use OR since only one value is there
    public void filterAndPrintNumberOfResults() {
        Map<uiDataNames, Object> searchData = getSearchData();
        System.out.println("Search Data: " + searchData);
        
        //Filter price
        Selection selection = dataTable.floatColumn(tableColumns.PRICE.getColumnName()).isBetweenInclusive((double) searchData.get(uiDataNames.MIN_PRICE), (double) searchData.get(uiDataNames.MAX_PRICE));
        System.out.println("After price filter: " + selection.size() + " items");
        
        //Filter star rating
        selection = selection.and( dataTable.floatColumn(tableColumns.REVIEW_SCORE.getColumnName()).isGreaterThanOrEqualTo(((Integer) searchData.get(uiDataNames.MIN_STAR_RATING)).doubleValue()) );
        System.out.println("After rating filter: " + selection.size() + " items");

        //Filter categorical filters
        Set<tableColumns> categoricalColumns = tableColumns.getCategoricalColumns();
        
        //Tags require special handling since it's a set of strings
        Map<String, Set<String>> filtersContainer = (Map<String, Set<String>>)searchData.get(uiDataNames.FILTERS_CONTAINER);
        System.out.println(filtersContainer);
        Set<String> selectedTags = filtersContainer.get("Tags");
        if( selectedTags != null && !selectedTags.isEmpty() )
            for( String selectedTag : selectedTags )
                selection = selection.and( dataTable.stringColumn(tableColumns.TAGS.getColumnName()).lowerCase().containsString(selectedTag.toLowerCase()) );
        System.out.println("After tags filter: " + selection.size() + " items");

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
                    Selection valueSelection = dataTable.stringColumn(column.getColumnName()).lowerCase().isEqualTo(value.toLowerCase());
                    columnSelection = (columnSelection == null) ? valueSelection : columnSelection.or(valueSelection);
                }

                if( columnSelection != null ) {
                    selection = selection.and(columnSelection);
                }
            }
            System.out.println("After " + filterKey + " filter: " + selection.size() + " items");
        }

        System.out.println("Number of results: " + selection.size());
        System.out.println("Total Data Size: " + dataTable.rowCount());

        //Reset pagination to first page after filtering
        SearchedItemPagination pagination = (SearchedItemPagination) uiManager.getElement(uiElementName.SEARCHED_ITEM_PAGINATION).get();
        Set<String> filteredItemIDs = dataTable.where(selection).stringColumn(tableColumns.ID.getColumnName()).asSet();
        pagination.updateContent(filteredItemIDs);
    }

}
