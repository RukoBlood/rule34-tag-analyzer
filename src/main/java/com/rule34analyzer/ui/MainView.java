package com.rule34analyzer.ui;

import com.rule34analyzer.analysis.*;
import com.rule34analyzer.api.Rule34Client;
import com.rule34analyzer.localization.Localization;
import com.rule34analyzer.model.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class MainView {
    private final VBox root = new VBox();
    private final Localization lang = new Localization("ru");
    private final TextField search = new TextField();
    private final Button analyze = new Button();
    private final Label status = new Label();
    private final Label percentage = new Label("0.00%");
    private final Label message = new Label();
    private final Label posts = new Label("0");
    private final Label ai = new Label("0");
    private final LineChart<String, Number> chart;
    private final VBox warning = new VBox();

    public MainView() {
        root.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );
        root.getStyleClass().add("app");

        HBox header = new HBox();
        header.getStyleClass().add("header");
        Label title = new Label(lang.get("app.title"));
        title.getStyleClass().add("title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer);

        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER);
        search.setPromptText(lang.get("search.placeholder"));
        analyze.setText(lang.get("search.button"));
        analyze.getStyleClass().add("pill-button");
        searchBar.getChildren().addAll(search, analyze);

        warning.getStyleClass().add("warning");
        warning.setVisible(false);
        warning.setManaged(false);

        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Posts");
        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(true);
        chart.setPrefHeight(470);
        chart.getStyleClass().add("chart");

        VBox stats = new VBox(2);
        Label pctLabel = new Label(lang.get("stats.ai_percentage"));
        pctLabel.getStyleClass().add("muted");
        percentage.getStyleClass().add("percentage");
        message.getStyleClass().add("message");
        stats.getChildren().addAll(pctLabel, percentage, message);
        stats.setAlignment(Pos.TOP_RIGHT);

        HBox cards = new HBox(12, card(lang.get("stats.posts"), posts),
                                  card(lang.get("stats.ai"), ai), stats);
        cards.getStyleClass().add("cards");
        HBox.setHgrow(stats, Priority.ALWAYS);

        status.getStyleClass().add("status");
        status.setText(lang.get("status.ready"));

        root.setPadding(new Insets(24));
        root.setSpacing(16);
        root.getChildren().addAll(header, searchBar, warning, cards, chart, status);

        analyze.setOnAction(e -> startAnalysis());
        search.setOnAction(e -> startAnalysis());

        // Connectivity warning: do not expose or require an exact IP.
        // The API request itself determines whether the endpoint is reachable.
    }

    private VBox card(String name, Label value) {
        Label label = new Label(name);
        label.getStyleClass().add("muted");
        value.getStyleClass().add("card-value");
        VBox box = new VBox(5, label, value);
        box.getStyleClass().add("card");
        return box;
    }

    private void startAnalysis() {
        String tag = search.getText().trim();
        if (tag.isEmpty()) return;

        analyze.setDisable(true);
        status.setText(lang.get("status.loading"));
        chart.getData().clear();

        Thread thread = new Thread(() -> {
            try {
                AnalysisResult result = new TagAnalyzer(
                    new Rule34Client(),
                    s -> Platform.runLater(() -> status.setText(s))
                ).analyze(tag);

                Platform.runLater(() -> showResult(result));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    status.setText(lang.get("status.error") + ": " + ex.getMessage());
                    showBlockedWarning();
                });
            } finally {
                Platform.runLater(() -> analyze.setDisable(false));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void showResult(AnalysisResult result) {
        posts.setText(String.valueOf(result.total()));
        ai.setText(String.valueOf(result.ai()));

        BigDecimal pct = PercentageCalculator.calculate(result.ai(), result.total());
        percentage.setText(pct.toPlainString() + "%");
        message.setText(messageFor(pct));

        XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
        totalSeries.setName(lang.get("chart.total"));
        XYChart.Series<String, Number> aiSeries = new XYChart.Series<>();
        aiSeries.setName(lang.get("chart.ai"));

        for (Map.Entry<LocalDate, DailyStats> e : result.dailyStats().entrySet()) {
            totalSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue().total()));
            aiSeries.getData().add(new XYChart.Data<>(e.getKey().toString(), e.getValue().ai()));
        }

        chart.getData().addAll(totalSeries, aiSeries);
        status.setText(lang.get("status.ready"));
        warning.setVisible(false);
        warning.setManaged(false);
    }

    private String messageFor(BigDecimal pct) {
        int p = pct.intValue();
        if (pct.compareTo(BigDecimal.valueOf(100)) >= 0) return lang.get("message.only_ai");
        if (p >= 90) return lang.get("message.extreme");
        if (p >= 70) return lang.get("message.high");
        if (p >= 30) return lang.get("message.balanced");
        if (pct.compareTo(BigDecimal.ZERO) > 0) return lang.get("message.low");
        return lang.get("message.zero");
    }

    private void showBlockedWarning() {
        warning.getChildren().setAll(new Label(lang.get("warning.blocked")));
        warning.setVisible(true);
        warning.setManaged(true);
    }

    public VBox root() {
        return root;
    }
}
