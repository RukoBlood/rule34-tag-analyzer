package com.rule34analyzer.ui;

import javafx.scene.control.Dialog;

public final class DialogStyles {
    private DialogStyles() {}

    public static void apply(Dialog<?> dialog) {
        var stylesheet = DialogStyles.class.getResource("/style.css");
        if (stylesheet == null) throw new IllegalStateException("style.css not found");
        dialog.getDialogPane().getStylesheets().add(stylesheet.toExternalForm());
        dialog.getDialogPane().getStyleClass().add("app-dialog");
    }
}