package org.troy.capstone.uiMock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.troy.capstone.utils.TableUtils;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import net.datafaker.Faker;
import tech.tablesaw.api.Table;

public class BasicUI extends Application {

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {

        //Use Faker to generate random data
        Faker faker = new Faker();
        
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20)); // Add 20px padding around all edges
        gridPane.setHgap(10); // 10px horizontal spacing between columns
        gridPane.setVgap(10); // 10px vertical spacing between rows

        //Get and set up the ItemScroller with random items
        ItemScroller itemScroller = new ItemScroller();
        int numItems = 10;
        for(int x = 0; x < numItems; x++)
            itemScroller.addItemPanel(new ItemPanel(Item.randomItem()));
        itemScroller.setPrefSize(500, 600);
        gridPane.add(itemScroller, 0, 1, 2, 3);

        //Get and setup the SearchBar
        SearchBar searchBar = new SearchBar();
        gridPane.add(searchBar, 0, 0, 2, 1);

        //Generate 10 random departments and categories and make Sets
        Set<String> departments = new HashSet<String>();
        Set<String> categories = new HashSet<String>();
        for( int x = 0; x < 10; x++){
            departments.add( faker.commerce().department() );
            categories.add( faker.commerce().material() );
        }
        //Insert a FiltersContainer
        FiltersContainer filtersContainer = new FiltersContainer();
        filtersContainer.addFilterPanel("Departments", departments);
        filtersContainer.addFilterPanel("Categories", categories);
        gridPane.add(filtersContainer, 2, 1, 2, 1);

        //Set additional action for SearchBar to get Checked options
        final Map<String, Set<String>> selectedOptions = new HashMap<>();
        searchBar.addAdditionalAction((ActionEvent e) ->{
            selectedOptions.clear();
            selectedOptions.putAll( filtersContainer.getSelectedFilters() );
            System.out.println("Selected Options: " + selectedOptions.toString());
        });

        //Insert a TableView to test the Tablesaw integration
        TableView<ObservableList<Object>> tableView = TableUtils.tablesawTableToTableView(
            Table.read().csv("data/teams.csv")
        );
        tableView.setMaxSize(400, 300);
        gridPane.add(tableView, 2, 3, 2, 2);


        ///Get and setup the PriceSlider
        PriceSlider priceSlider = new PriceSlider(0, 500);
        gridPane.add(priceSlider, 2, 0, 2, 1);
        gridPane.setPrefSize(1000, 700);
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Basic UI");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
