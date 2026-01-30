package org.troy.capstone.uiMock;

import javafx.application.Application;
import javafx.scene.Scene;

public class BasicUI extends Application {

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {

        ItemScroller itemScroller = new ItemScroller();
        int numItems = 10;
        for(int x = 0; x < numItems; x++) {
            itemScroller.addItemPanel(new ItemPanel(Item.randomItem()));
        }

        Scene scene = new Scene(itemScroller, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Basic UI");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
