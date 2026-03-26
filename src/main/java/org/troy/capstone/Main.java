package org.troy.capstone;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.managers.RecentlyViewedManager;
import org.troy.capstone.ui_components.PriceSlider;
import org.troy.capstone.ui_components.SearchBar;
import org.troy.capstone.ui_components.filters.StarRatingFilter;
import org.troy.capstone.ui_components.filters.categorical.FiltersContainer;
import org.troy.capstone.ui_components.items.RecentlyViewedWindow;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;
import org.troy.capstone.utils.TableUtils;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Load data from csv and set into ItemHashMap
        Table table = TableUtils.readCleanedAttributedData();
        ItemHashMap itemHashMap = ItemHashMap.fromTable(table);
        
        //Create generalManager
        GeneralManager generalManager = new GeneralManager(table);
        
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20)); // Add 20px padding around all edges
        gridPane.setHgap(UISizeControl.WIDTH_PADDING.getValue()); // 10px horizontal spacing between columns
        gridPane.setVgap(UISizeControl.HEIGHT_PADDING.getValue()); // 10px vertical spacing between rows

        SearchedItemPagination itemPagination = SearchedItemPagination.create(itemHashMap, generalManager);
        gridPane.add(itemPagination, 0, 1, 2, 3);
        //SearchedItemContainer itemScroller = SearchedItemContainer.createFilledContainer(table.first(firstNItems), itemHashMap);
        //gridPane.add(itemScroller, 0, 1, 2, 3);

        //Get and setup the SearchBar
        SearchBar searchBar = SearchBar.create(generalManager);
        gridPane.add(searchBar, 0, 0, 2, 1);

        //Insert a FiltersContainer
        FiltersContainer filtersContainer = FiltersContainer.create(generalManager, itemHashMap);
        gridPane.add(filtersContainer, 2, 1, 1, 1);

        //Get and setup the PriceSlider
        //+1 and -1 to ensure no items cutoff by rounding issues
        double minPrice = table.floatColumn(TableColumnName.PRICE.getColumnName()).min() - 1;
        double maxPrice = table.floatColumn(TableColumnName.PRICE.getColumnName()).max() + 1;
        PriceSlider priceSlider = new PriceSlider(minPrice, maxPrice, generalManager );
        gridPane.add(priceSlider, 2, 0, 2, 1);

        //Get and setup the StarRatingFilter
        StarRatingFilter starRatingFilter = StarRatingFilter.create(generalManager);
        gridPane.add(starRatingFilter, 2, 2, 1, 1);
        

        RecentlyViewedWindow recentlyViewedWindow = RecentlyViewedWindow.create();
        RecentlyViewedManager recentlyViewedManager = new RecentlyViewedManager(itemHashMap, recentlyViewedWindow);
        gridPane.add(recentlyViewedWindow, 3, 1, 1, 2);

        gridPane.setPrefSize(1000, 700);
        
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Shopping App");
        primaryStage.show();

    }

   
    public static void main(String[] args) {
        launch(args);
    }

}
