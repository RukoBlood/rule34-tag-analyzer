package com.rule34analyzer.ui;

import com.rule34analyzer.config.ConfigManager;
import com.rule34analyzer.localization.Localization;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class SettingsDialog {
    public static Optional<ConfigManager.Config> show(ConfigManager.Config current, Localization lang){
        Dialog<ConfigManager.Config> dialog = new Dialog<>();
        dialog.setTitle(lang.get("settings.title"));
        dialog.setHeaderText(lang.get("settings.title"));
        DialogStyles.apply(dialog);

        ButtonType save = new ButtonType(lang.get("settings.save"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(lang.get("settings.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(save, cancel);

        ComboBox<String> language = new ComboBox<>();
        language.getItems().addAll("Русский", "English");
        language.setValue("en".equals(current.language) ? "English" : "Русский");

        PasswordField apiKey = new PasswordField();
        apiKey.setPromptText(lang.get("settings.api.placeholder"));
        apiKey.setText(current.api_key);

        VBox content = new VBox(
                10,
                new Label(lang.get("settings.language")),
                language,
                new Label(lang.get("settings.api")),
                apiKey
        );
        content.setPadding(new Insets(12));
        content.setPrefWidth(380);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button != save) {
                return null;
            }

            ConfigManager.Config updated = new ConfigManager.Config();
            updated.language = "English".equals(language.getValue()) ? "en" : "ru";
            updated.api_key = apiKey.getText().trim();
            return updated;
        });

        return dialog.showAndWait();
    }
}
