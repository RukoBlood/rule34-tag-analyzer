package com.rule34analyzer;

import com.rule34analyzer.config.ConfigManager;
import com.rule34analyzer.localization.Localization;
import com.rule34analyzer.ui.MainView;
import com.rule34analyzer.ui.SetupDialog;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        ConfigManager.Config config;
        try {
            config = ConfigManager.load();
        } catch (Exception e) {
            showError("Failed to load configuration", e);
            return;
        }

        if (config.api_key.isBlank()) {
            var result = SetupDialog.show(config, new Localization(config.language));

            if (result.isEmpty()) {
                stage.close();
                return;
            }

            config = result.get();

            try {
                ConfigManager.save(config);
            } catch (Exception e) {
                showError("Failed to save configuration", e);
                return;
            }
        }

        showMain(config);
    }

    private void showMain(ConfigManager.Config config) {
        MainView view = new MainView(config, this::showMain, getHostServices());

        Scene scene = new Scene(view.root(), 1100, 760);
        stage.setTitle("Rule34 Tag Analyzer");
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    private void showError(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Rule34 Tag Analyzer");
        alert.setHeaderText(title);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        stage.close();
    }
}
