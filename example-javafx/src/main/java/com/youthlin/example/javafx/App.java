package com.youthlin.example.javafx;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author youthlin.chen
 * @date 2019-10-22 17:26
 */
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        SplitPane parent = new SplitPane(new Pane(new Label("Left")), new Pane(new Label("Right")));
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

}
