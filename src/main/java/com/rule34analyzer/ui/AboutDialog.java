package com.rule34analyzer.ui;

import com.rule34analyzer.localization.Localization;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AboutDialog {
    public static void show(HostServices hostServices, Localization lang, String version) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(lang.get("about.title"));
        dialog.setHeaderText(lang.get("about.title"));
        DialogStyles.apply(dialog);

        ButtonType close = new ButtonType(lang.get("about.close"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(close);

        Hyperlink github = new Hyperlink(lang.get("about.repository"));
        github.setOnAction(event -> hostServices.showDocument("https://github.com/RukoBlood/rule34-tag-analyzer"));

        VBox content = new VBox(10,
                new Label("Rule34 Tag Analyzer"),
                new Label(lang.get("about.version") + ": " + version),
                new Label(lang.get("about.author") + ": RukoBlood"),
                new Label(lang.get("about.vibe_coding") + ": OpenAI ChatGPT"),
                new Label(lang.get("about.technologies") + ": Java 21, JavaFX, Gradle, Gson")
        );
        content.getChildren().add(github);
        content.setPadding(new Insets(12));
        content.setPrefWidth(360);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
}
