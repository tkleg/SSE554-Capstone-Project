package org.troy.capstone;

import org.troy.capstone.constants.DataPath;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.managers.GeneralManager;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Load data from csv and set into ItemHashMap
        Table table = TableUtils.readData(DataPath.CLEANED_ATTRIBUTED_DATA);
        ItemHashMap itemHashMap = ItemHashMap.fromTable(table);
        
        //Create generalManager
        GeneralManager generalManager = new GeneralManager(table, itemHashMap);
        
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20)); // Add 20px padding around all edges
        gridPane.setHgap(UISizeControl.WIDTH_PADDING.getValue()); // 10px horizontal spacing between columns
        gridPane.setVgap(UISizeControl.HEIGHT_PADDING.getValue()); // 10px vertical spacing between rows

        SearchedItemPagination itemPagination = new SearchedItemPagination(itemHashMap);
        generalManager.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, itemPagination);
        
        gridPane.add(itemPagination, 0, 1, 2, 3);
        //SearchedItemContainer itemScroller = SearchedItemContainer.createFilledContainer(table.first(firstNItems), itemHashMap);
        //gridPane.add(itemScroller, 0, 1, 2, 3);

        RecentlyViewedWindow recentlyViewedWindow = RecentlyViewedWindow.create();
        gridPane.add(recentlyViewedWindow, 3, 1, 1, 2);
        generalManager.addUIElement(UIElementName.RECENTLY_VIEWED_WINDOW, recentlyViewedWindow);
        
        //Get and setup the SearchBar
        SearchBar searchBar = new SearchBar();
        generalManager.addUIElement(UIElementName.SEARCH_FIELD, searchBar.getSearchField());
        generalManager.addUIElement(UIElementName.SORTING_OPTION_DROPDOWN, searchBar.getSortingOptionDropdown());
        generalManager.setButton(searchBar.getSearchButton());

        gridPane.add(searchBar, 0, 0, 1, 1);

        //Insert a FiltersContainer
        FiltersContainer filtersContainer = FiltersContainer.create(itemHashMap);
        generalManager.addUIElement(UIElementName.FILTERS_CONTAINER, filtersContainer);
        gridPane.add(filtersContainer, 2, 1, 1, 1);

        //Get and setup the PriceSlider
        //+1 and -1 to ensure no items cutoff by rounding issues
        double minPrice = table.floatColumn(TableColumnName.PRICE.getColumnName()).min() - 1;
        double maxPrice = table.floatColumn(TableColumnName.PRICE.getColumnName()).max() + 1;
        PriceSlider priceSlider = new PriceSlider(minPrice, maxPrice );
        generalManager.addUIElement(UIElementName.MIN_PRICE_SLIDER, priceSlider.getMinSlider());
        generalManager.addUIElement(UIElementName.MAX_PRICE_SLIDER, priceSlider.getMaxSlider());
        gridPane.add(priceSlider, 1, 0, 2, 1);

        //Get and setup the StarRatingFilter
        StarRatingFilter starRatingFilter = new StarRatingFilter();
        generalManager.addUIElement(UIElementName.STAR_RATING_FILTER, starRatingFilter);
        gridPane.add(starRatingFilter, 3, 0, 1, 1);
        

        // Set preferred size to fit all content initially
        gridPane.setPrefSize(GridPane.USE_COMPUTED_SIZE, GridPane.USE_COMPUTED_SIZE);

        // Let the scene size be determined by the gridPane's preferred size
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Shopping App");
        primaryStage.show();

        // After showing, wrap in a ScrollPane to allow scrolling only when window is shrunken
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(false);

        // Set the scene root to the scrollPane after initial display
        scene.setRoot(scrollPane);

    }

   
    public static void main(String[] args) {
        launch(args);
    }

}
