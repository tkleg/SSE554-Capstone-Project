package org.troy.capstone.uiMock;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class BasicUI extends Application {

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20)); // Add 20px padding around all edges
        gridPane.setHgap(10); // 10px horizontal spacing between columns
        gridPane.setVgap(10); // 10px vertical spacing between rows

        //Get and set up the ItemScroller with random items
        ItemScroller itemScroller = new ItemScroller();
        int numItems = 10;
        for(int x = 0; x < numItems; x++)
            itemScroller.addItemPanel(new ItemPanel(Item.randomItem()));
        itemScroller.setPrefSize(400, 700);
        gridPane.add(itemScroller, 0, 1, 2, 3);

        //Get and setup the SearchBar
        SearchBar searchBar = new SearchBar();
        gridPane.add(searchBar, 0, 0, 2, 1);


        ///Get and setup the PriceSlider
        PriceSlider priceSlider = new PriceSlider(0, 500);
        gridPane.add(priceSlider, 2, 0, 2, 1);
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Basic UI");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
