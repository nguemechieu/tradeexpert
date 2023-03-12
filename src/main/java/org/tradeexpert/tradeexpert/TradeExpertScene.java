package org.tradeexpert.tradeexpert;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.tradeexpert.tradeexpert.BinanceUs.BinanceUs;
import org.tradeexpert.tradeexpert.Coinbase.Coinbase;
import org.tradeexpert.tradeexpert.oanda.OANDA_ACCESS_TOKEN;
import org.tradeexpert.tradeexpert.oanda.Oanda;

import java.util.Objects;

public class TradeExpertScene extends AnchorPane {

    private static final String BINANCE_ACCESS_TOKEN = "";
    private static final String BINANCE_ACCESS_SECRET = "";
    private static final String BINANCE_ACCESS_PASSWORD = "";
 private static final TabPane  tabPane = new TabPane();
    public TradeExpertScene() throws Exception {

        VBox ordersBox = new VBox(listOrders());
        VBox navigator = new VBox(listNavigator());
        navigator.setPrefHeight(400);
        navigator.setPrefWidth(200);
        navigator.setAlignment(Pos.CENTER);
        navigator.setSpacing(5);
        navigator.setPadding(new Insets(5));
        navigator.setTranslateY(100);
        navigator.setTranslateX(0);
        ordersBox.setTranslateY(600);
        ordersBox.setTranslateX(200);
        ordersBox.setPrefSize(1200, 600);
        String tradePair = "BTC-USD";
        Oanda oanda = new Oanda("77be89b17b7fe4c04affd4200454827c-dea60a746483dc7702878bdfa372bb99", OANDA_ACCESS_TOKEN.ACCOUNT_ID.toString());
        BinanceUs binance = new BinanceUs(BINANCE_ACCESS_TOKEN, BINANCE_ACCESS_SECRET, BINANCE_ACCESS_PASSWORD);
        Coinbase coinbase = new Coinbase(tradePair);

        CandleStickChartContainer coinbaseCandleStickChartContainer = new CandleStickChartContainer(coinbase, tradePair, true);
        String tradePair1="EUR_USD";
        CandleStickChartContainer oandaCandleStickChartContainer = new CandleStickChartContainer(oanda, tradePair1, true);
        String tradePair2="LTCBTC";
        CandleStickChartContainer binanceCandleStickChartContainer = new CandleStickChartContainer(binance, tradePair2, true);

        DraggableTab oandaTab = new DraggableTab("Oanda");
        oandaTab.setContent(new VBox(oandaCandleStickChartContainer));
        DraggableTab binanceTab = new DraggableTab("Binance");
        binanceTab.setContent(new VBox(binanceCandleStickChartContainer));
        DraggableTab coinbaseTab = new DraggableTab("Coinbase");
        coinbaseTab.setContent(new VBox(coinbaseCandleStickChartContainer));
        tabPane.getTabs().addAll(oandaTab, binanceTab, coinbaseTab);
        //candlestickChartContainer,new Separator(Orientation.VERTICAL),navigator,ordersBox, connex
        tabPane.setTranslateY(25);
        tabPane.setCursor(Cursor.DEFAULT);
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        tabPane.setPrefSize(1530,780);

        getChildren().addAll(getMenuBar(), tabPane);

        setStyle(
                "-fx-background-color: rgb(45, 25, 144, 1);" +
                        "-fx-border-color: rgb(45, 25, 144, 1);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 5;" +
                        "-fx-border-insets: 5;" +
                        "-fx-background-insets: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-background-insets: 5;"
        );

        getStylesheets().setAll(Objects.requireNonNull(getClass().getResource("/app.css")).toExternalForm());



    }

    private @NotNull HBox connexion() {
        HBox connexionInfo = new HBox();
        connexionInfo.setPrefHeight(100);
        connexionInfo.setPrefWidth(100);
        connexionInfo.setAlignment(Pos.CENTER);
        connexionInfo.setSpacing(10);
        connexionInfo.setPadding(new Insets(10));
        connexionInfo.setTranslateY(100);
        connexionInfo.setTranslateX(200);
        connexionInfo.setPrefSize(1200, 550);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefHeight(20);
        progressBar.setPrefWidth(100);

        progressBar.setStyle("-fx-background-color: rgb(45, 25, 144, 1)");
        progressBar.setCursor(Cursor.DEFAULT);
        progressBar.setProgress(1.3);
        progressBar.setDepthTest(DepthTest.ENABLE);

        Label label = new Label("Connexion");
        label.setPrefHeight(20);
        label.setPrefWidth(100);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-size: 20px;");
        label.setPrefHeight(20);
        label.setPrefWidth(100);

        connexionInfo.getChildren().addAll(label,progressBar);
        Label label2 = new Label("Connexion In progress");
        label2.setPrefHeight(20);
        if (Trade.getConnexionInfo()== 200) {
            label2=new Label("Connected...");
            label2.setStyle("-fx-font-size: 15px;");
            label2.setStyle("-fx-font-weight: bold;");
            label2.setStyle("-fx-font-style: italic;");
            label2.setPrefHeight(20);
            label2.setPrefWidth(100);
            progressBar.setProgress(100);


        }else
        if (Trade.getConnexionInfo()== 400) {
            label2=new Label(
                    "Connection refused"
            );
            label2.setStyle("-fx-font-size: 15px;");
            label2.setStyle("-fx-font-weight: bold;");
            label2.setStyle("-fx-font-style: italic;");
            label2.setPrefHeight(20);
            label2.setPrefWidth(100);
            progressBar.setProgress(100);
        }else if (
                Trade.getConnexionInfo() ==
                        401
        ){
            label2=new Label("Unauthorized");
            label2.setStyle("-fx-font-size: 15px;");
            label2.setStyle("-fx-font-weight: bold;");
            label2.setStyle("-fx-font-style: italic;");
            label2.setPrefHeight(20);
            label2.setPrefWidth(100);
            progressBar.setProgress(50);
        }else if (
                Trade.getConnexionInfo() ==
                        402
        ){
            label2=new Label("Bad credentials");
            label2.setStyle("-fx-font-size: 15px;");
            label2.setStyle("-fx-font-weight: bold;");
            label2.setStyle("-fx-font-style: italic;");
            label2.setPrefHeight(20);
            label2.setPrefWidth(100);
            progressBar.setProgress(50);
        }
        else if (
                Trade.getConnexionInfo() ==500
        ){
            label2=new Label("Internal server error");
            label2.setStyle("-fx-font-size: 15px;");
            label2.setStyle("-fx-font-weight: bold;");
            label2.setStyle("-fx-font-style: italic;");
            label2.setPrefHeight(20);
            label2.setPrefWidth(100);
            progressBar.setProgress(50);
        }

        label2.setPrefHeight(20);
        label2.setPrefWidth(100);
        label2.setAlignment(Pos.CENTER);
        connexionInfo.setTranslateY(730);
        connexionInfo.setTranslateX(0);
        connexionInfo.setPrefSize(1500, 30);

        connexionInfo.getChildren().addAll(new Separator(Orientation.VERTICAL),label2);


        return connexionInfo;
    }

    private @NotNull TreeTableView<Objects> listNavigator() {
        TreeTableView<Objects> orders = new TreeTableView<>();
        orders.setPrefHeight(300);
        orders.setPrefWidth(200);
        return orders;
    }

    private @NotNull ListView<Order> listOrders() {
        ListView<Order> orders = new ListView<>();
        orders.setPrefHeight(100);
        orders.setPrefWidth(100);
        orders.setCellFactory(param -> new OrderCell());
        orders.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Order order = orders.getSelectionModel().getSelectedItem();
                if (order!= null) {
                    order.showOrderDetails();
                }
            }
        });
        return orders;
    }


    MenuBar menuBar = new MenuBar();
    public Node getMenuBar() {
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(
                new MenuItem("New")
        );
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(
                new MenuItem("Open")
        );
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(
                new MenuItem("Save")
        );
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(
                new MenuItem("Save As")
        );
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(
                new MenuItem("Print")
        );
        fileMenu.getItems().add(
                new MenuItem("Exit")
        );


        Menu editMenu = new Menu("Edit");
        editMenu.getItems().add(new SeparatorMenuItem());
        editMenu.getItems().add(
                new MenuItem("Cut")
        );
        editMenu.getItems().add(new SeparatorMenuItem());
        editMenu.getItems().add(
                new MenuItem("Copy")
        );
        editMenu.getItems().add(new SeparatorMenuItem());
        editMenu.getItems().add(
                new MenuItem("Paste")
        );
        editMenu.getItems().add(
                new MenuItem("Delete")
        );

        Menu viewMenu = new Menu("View");
        viewMenu.getItems().add(new MenuItem("Zoom In"));
        viewMenu.getItems().add(new SeparatorMenuItem());
        viewMenu.getItems().add(new MenuItem("Zoom Out"));
        viewMenu.getItems().add(new SeparatorMenuItem());
        viewMenu.getItems().add(new MenuItem("Reset Zoom"));
        viewMenu.getItems().add(new SeparatorMenuItem());
        viewMenu.getItems().add(new MenuItem("Reset"));
        Menu windowMenu = new Menu("Window");
        windowMenu.getItems().add(new MenuItem("Minimize"));
        windowMenu.getItems().add(new MenuItem("Maximize"));
        windowMenu.getItems().add(new MenuItem("Close"));

        Menu chartMenu = new Menu("Charts");
        chartMenu.getItems().add(new MenuItem("Bar Chart"));
        chartMenu.getItems().add(new SeparatorMenuItem());
        chartMenu.getItems().add(new MenuItem("Line Chart"));
        chartMenu.getItems().add(new SeparatorMenuItem());
        chartMenu.getItems().add(new MenuItem("Pie Chart"));
        chartMenu.getItems().add(new SeparatorMenuItem());
        chartMenu.getItems().add(new MenuItem("Scatter Chart"));

        chartMenu.getItems().add(new SeparatorMenuItem());
        chartMenu.getItems().add(new MenuItem("Radar Chart"));
        chartMenu.getItems().add(new SeparatorMenuItem());
        chartMenu.getItems().add(new MenuItem("Bubble Chart"));
        chartMenu.getItems().add(new SeparatorMenuItem());
        chartMenu.getItems().add(new MenuItem("Candle Stick Chart"));

        Menu insertMenu = new Menu("Insert");
        insertMenu.getItems().add(new Menu("Indicators"));
        insertMenu.getItems().add(new SeparatorMenuItem());
        Menu lines=new Menu("Lines");

        lines.getItems().add(new MenuItem("Trend Line By Angle"));
        lines.getItems().add(new SeparatorMenuItem());
        lines.getItems().add(new MenuItem("Vertical Line"));
        lines.getItems().add(new SeparatorMenuItem());
        lines.getItems().add(new MenuItem("Horizontal Line"));
        lines.getItems().add(new SeparatorMenuItem());
        lines.getItems().add(new MenuItem("Trend Line"));
        lines.getItems().add(new SeparatorMenuItem());
        insertMenu.getItems().add(lines);

        insertMenu.getItems().add(new Menu("Areas"));
        insertMenu.getItems().add(new SeparatorMenuItem());
        insertMenu.getItems().add(new Menu("Channels"));
        insertMenu.getItems().add(new SeparatorMenuItem());
        insertMenu.getItems().add(new Menu("Markers"));
        insertMenu.getItems().add(new SeparatorMenuItem());
        insertMenu.getItems().add(new Menu("Gann"));
        insertMenu.getItems().add(new SeparatorMenuItem());
        insertMenu.getItems().add(new Menu("Fibonacci"));
        insertMenu.getItems().add(new SeparatorMenuItem());
        insertMenu.getItems().add(new Menu("Arrows"));
        insertMenu.getItems().add(new SeparatorMenuItem());
        insertMenu.getItems().add(new Menu("Shapes"));
        insertMenu.getItems().add(new SeparatorMenuItem());
        insertMenu.getItems().add(new Menu("Objects"));



        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(new MenuItem("About"));
        helpMenu.getItems().add(new SeparatorMenuItem());
        helpMenu.getItems().add(new MenuItem("Help"));



        menuBar.getMenus().addAll(
                fileMenu,
                editMenu,
                viewMenu,
                windowMenu,
                chartMenu,
                insertMenu,
                helpMenu);

        Menu toolsMenu = new Menu("Tools");
        toolsMenu.getItems().add(new MenuItem("New Orders"));
        toolsMenu.getItems().add(new SeparatorMenuItem());
        toolsMenu.getItems().add(new MenuItem("History Center"));
        toolsMenu.getItems().add(new SeparatorMenuItem());
        toolsMenu.getItems().add(new MenuItem("Balance Sheet"));
        return menuBar;
    }



}
