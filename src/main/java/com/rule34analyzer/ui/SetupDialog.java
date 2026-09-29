package com.rule34analyzer.ui;

import com.rule34analyzer.config.ConfigManager;
import com.rule34analyzer.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class SetupDialog {
    public static Optional<ConfigManager.Config> show(ConfigManager.Config initial, Localization lang) {
        Dialog<ConfigManager.Config> dialog = new Dialog<>();
        dialog.setTitle(lang.get("setup.title"));
        dialog.setHeaderText(lang.get("setup.title"));
        DialogStyles.apply(dialog);

        ButtonType save = new ButtonType(lang.get("setup.continue"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(lang.get("setup.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(save, cancel);

        ComboBox<String> language = new ComboBox<>();
        language.getItems().addAll("Русский", "English");
        language.setValue("en".equals(initial.language) ? "English" : "Русский");

        Label instructions = new Label(lang.get("setup.api_help"));
        instructions.setWrapText(true);

        PasswordField apiKey = new PasswordField();
        apiKey.setPromptText("setup.api_placeholder");
        apiKey.setText(initial.api_key);

        VBox content = new VBox(
                10,
                new Label(lang.get("settings.language")),
                language,
                new Label(lang.get("setup.how_to_get_api_key")),
                instructions,
                new Label(lang.get("settings.api")),
                apiKey
        );

        content.setPadding(new Insets(12));
        content.setPrefWidth(440);

        dialog.getDialogPane().setContent(content);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(save);
        saveButton.disableProperty().bind(apiKey.textProperty().isEmpty());

        dialog.setResultConverter(button -> {
            if (button != save) {
                return null;
            }

            ConfigManager.Config config = new ConfigManager.Config();
            config.language = "English".equals(language.getValue()) ? "en" : "ru";
            config.api_key = apiKey.getText().trim();
            return config;
        });

        return dialog.showAndWait();
    }
}
