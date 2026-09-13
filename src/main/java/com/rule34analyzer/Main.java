package com.rule34analyzer;

import com.rule34analyzer.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MainView view = new MainView();
        Scene scene = new Scene(view.root(), 1100, 760);
        stage.setTitle("Rule34 Tag Analyzer");
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
