package org.tradeexpert.tradeexpert;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Objects;

import static javafx.scene.layout.AnchorPane.*;

/**
 * A {@link Region} that contains a {@code CandleStickChart} and a {@code CandleStickChartToolbar}.
 * The contained chart will display data for the given {@code tradePair}. The toolbar allows for changing
 * the duration in seconds of each candle as well as configuring the properties of the chart. When a new
 * duration is selected, this container automatically creates a new {@code CandleStickChart} and visually
 * transitions to it.
 *
 * @author noel martial nguemechieu
 */


public class CandleStickChartContainer extends Region {
    boolean liveSyncing;
    CandleStickChartToolbar toolbar;
    VBox candleChartContainer;
     String tradePair;
     SimpleIntegerProperty secondsPerCandle;
    private CandleStickChart candleStickChart;
    public CandleStickChartContainer(Exchange exchange, String tradePair, boolean liveSyncing) throws URISyntaxException, IOException {
        Objects.requireNonNull(exchange, "exchange must not be null");
        Objects.requireNonNull(tradePair, "tradePair must not be null");

        this.tradePair = tradePair;
        this.liveSyncing = liveSyncing;
        secondsPerCandle = new SimpleIntegerProperty(3600);
        getStyleClass().add("candle-chart-container");
        setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        CandleDataSupplier candleDataSupplier = exchange.getCandleDataSupplier(secondsPerCandle.get(), tradePair);
        toolbar = new CandleStickChartToolbar(widthProperty(), heightProperty(), candleDataSupplier.getSupportedGranularities());
        VBox toolbarContainer = new VBox(toolbar);
        toolbarContainer.setPrefWidth(Double.MAX_VALUE);
        toolbarContainer.setPrefHeight(10);
        toolbarContainer.prefWidthProperty().bind(prefWidthProperty());
        toolbarContainer.setTranslateX(1);

        setLeftAnchor(toolbarContainer, 82.0);
        setRightAnchor(toolbarContainer, 0.0);
        candleChartContainer = new VBox();
        setTopAnchor(candleChartContainer, 46.0);
        setLeftAnchor(candleChartContainer, 15.0);
        setRightAnchor(candleChartContainer, 15.0);
        setBottomAnchor(candleChartContainer, 0.0);
        AnchorPane containerRoot = new AnchorPane(toolbarContainer, candleChartContainer);
        getChildren().addAll(containerRoot);
        toolbar.registerEventHandlers(candleStickChart, secondsPerCandle);
        secondsPerCandle.addListener((observableDurationValue, oldDurationValue, newDurationValue) -> {
            if (!oldDurationValue.equals(newDurationValue)) {
                try {
                    createNewChart(exchange,newDurationValue.intValue(), liveSyncing);
                } catch (ParseException | TelegramApiException | IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    toolbar.registerEventHandlers(candleStickChart, secondsPerCandle);
                } catch (URISyntaxException | IOException e) {
                    throw new RuntimeException(e);
                }
                toolbar.setChartOptions(candleStickChart.getChartOptions());
                toolbar.setActiveToolbarButton(secondsPerCandle);
                animateInNewChart(candleStickChart);
            }
        });

    }

    private void createNewChart(Exchange exchange,int secondsPerCandle, boolean liveSyncing) throws ParseException, TelegramApiException, IOException, InterruptedException {
        if (secondsPerCandle <= 0) {
            throw new IllegalArgumentException("secondsPerCandle must be positive but was: " + secondsPerCandle);
        }

        candleStickChart = new CandleStickChart(exchange, exchange.getCandleDataSupplier(secondsPerCandle, tradePair),
                tradePair, liveSyncing, secondsPerCandle, widthProperty(), heightProperty());
        candleStickChart.setTranslateX(5);

        candleStickChart.setPrefSize(widthProperty().get()-70, heightProperty().get()-50);
        candleChartContainer.getChildren().setAll(candleStickChart);
    }

    private void animateInNewChart(CandleStickChart newChart) {
        Objects.requireNonNull(newChart, "newChart must not be null");

        if (candleStickChart != null) {
            FadeTransition fadeTransitionOut = new FadeTransition(Duration.millis(300), candleStickChart);
            fadeTransitionOut.setFromValue(1.0);
            fadeTransitionOut.setToValue(0.0);
            fadeTransitionOut.setOnFinished(event -> {
                candleStickChart = newChart;
                candleChartContainer.getChildren().setAll(newChart);
                FadeTransition fadeTransitionIn = new FadeTransition(Duration.millis(200), candleStickChart);
                fadeTransitionIn.setFromValue(0.0);
                fadeTransitionIn.setToValue(1.0);
                fadeTransitionIn.play();
            });

            fadeTransitionOut.play();
        } else {
            candleStickChart = newChart;
            candleChartContainer.getChildren().setAll(newChart);
            FadeTransition fadeTransitionIn = new FadeTransition(Duration.millis(400), candleStickChart);
            fadeTransitionIn.setFromValue(0.0);
            fadeTransitionIn.setToValue(1.0);
            fadeTransitionIn.play();
        }
    }

    @Override
    protected double computeMinWidth(double height) {
        return 900;
    }

    @Override
    protected double computeMinHeight(double width) {
        return 500;
    }
}