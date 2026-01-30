package org.troy.capstone.uiMock;

import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class BasicUI extends Application {

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {

        ScrollPane itemScroller = new ScrollPane();
        VBox itemContainer = new VBox(5); // 5px spacing between items
        itemScroller.setContent(itemContainer);
        

        System.out.println("Enter the number of items: ");
        Scanner scan = new Scanner(System.in);
        int numItems = scan.nextInt();
        scan.close();
        for( int i = 0; i < numItems; i++ ) {
            Item item = Item.randomItem();
            ItemPanel itemPanel = new ItemPanel(item);
            itemContainer.getChildren().add(itemPanel);
        }
        primaryStage.setTitle("Basic UI");
        primaryStage.setScene(new Scene(itemScroller, 600, 400)); // Set scene with ScrollPane
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
