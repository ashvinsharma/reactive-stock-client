package com.mechanitis.demo.stockui;

import com.mechanitis.demo.stockclient.StockClient;
import com.mechanitis.demo.stockclient.StockPrice;
import com.mechanitis.demo.stockclient.WebClientStockClient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static java.lang.String.valueOf;
import static javafx.collections.FXCollections.observableArrayList;

@Component
public class ChartController {

    @FXML
    public LineChart<String, Double> chart;
    private StockClient stockClient;

    public ChartController(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    @FXML
    public void initialize() {
        final String symbol1 = "SYMBOL1";
        final PriceSubscriber priceSubscriber1 = new PriceSubscriber(symbol1);
        stockClient.pricesFor(symbol1)
                .subscribe(priceSubscriber1);

        final String symbol2 = "SYMBOL2";
        final PriceSubscriber priceSubscriber2 = new PriceSubscriber(symbol2);
        stockClient.pricesFor(symbol2)
                .subscribe(priceSubscriber2);

        ObservableList<XYChart.Series<String, Double>> data = observableArrayList();
        data.add(priceSubscriber1.getSeries());
        data.add(priceSubscriber2.getSeries());
        chart.setData(data);
    }

    private static class PriceSubscriber implements Consumer<StockPrice> {
        private final ObservableList<Data<String, Double>> seriesData = observableArrayList();
        private final XYChart.Series<String, Double> series;

        public PriceSubscriber(String symbol) {
            series = new XYChart.Series<>(symbol, seriesData);
        }

        @Override
        public void accept(StockPrice stockPrice) {
            Platform.runLater(() ->
                    seriesData.add(new Data<>(valueOf(stockPrice.getTime().getSecond()),
                            stockPrice.getPrice()))
            );
        }

        public XYChart.Series<String, Double> getSeries() {
            return series;
        }
    }
}
