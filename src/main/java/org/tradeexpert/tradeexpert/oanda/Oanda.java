package org.tradeexpert.tradeexpert.oanda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Alert;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.tradeexpert.tradeexpert.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static java.lang.System.out;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.tradeexpert.tradeexpert.NewsManager.news;
import static org.tradeexpert.tradeexpert.oanda.Oanda.OandaCandleDataSupplier.OBJECT_MAPPER;


//"eff696fb26e0557011710149e462c859-3bcbe04cbf3fd0099d2511f5fbbc1b88
public class Oanda extends Exchange {



    public Oanda(String api_key, String accountID) {
        super(ur);
      Oanda.api_key = api_key;
      Oanda.accountID = accountID;

    }
    static  String ur = "wss://api-fxtrade.oanda.com/v3/accounts/"+Oanda.accountID+"/pricing/stream";
    //"wss://api-fxtrade.oanda.com/v3/accounts/";
    private static final Accounts accounts = new Accounts();

    private static final ObjectMapper mapper = new ObjectMapper();




    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
    public static String accountID = "001-001-2783446-006";

    //
//            GET	/v3/accounts/{accountID}/instruments
//    Get the list of traceable instruments for the given Account. The list of tradeable instruments is dependent on the regulatory division that the Account is located in, thus should be the same for all Accounts owned by a single user.
    public static ArrayList<String> instrumentsList = new ArrayList<>();
    static Instrument instrument = new Instrument();
    static Position positionObj;
    static String host = "https://api-fxtrade.oanda.com";
    static ArrayList<CandleData> array = new ArrayList<>();
    private static String api_key = "77be89b17b7fe4c04affd4200454827c-dea60a746483dc7702878bdfa372bb99";
    Alert alert = new Alert(Alert.AlertType.WARNING, "Test Alert");
    String accountInstrument;
    private OandaTransaction oandaTransaction;





    static Root    root = new Root();


    @Contract(pure = true)
    public static @NotNull ArrayList<OandaOrder> getOrdersHistory() throws OandaException {
        return getOrdersList();

    }



    // Trade Endpoints

//
//
//    GET	/v3/accounts/{accountID}/trades
//    Get a list of Trades for an Account

//
//    GET	/v3/accounts/{accountID}/openTrades
//    Get the list of open Trades for an Account

    @Contract(value = " -> new", pure = true)
    public static @NotNull Collection<Object> getOpenTradesList() {
        return new ArrayList<>();
    }
//
//    GET	/v3/accounts/{accountID}/trades/{tradeSpecifier}
//    Get the details of a specific Trade in an Account

    public static Accounts getAccount() {
        return accounts;
    }

//    PUT	/v3/accounts/{accountID}/trades/{tradeSpecifier}/close
//    Close (partially or fully) a specific open Trade in an Account

    public static void setApi_Key(String apiKey) {
        api_key = apiKey;
    }
//
//    PUT	/v3/accounts/{accountID}/trades/{tradeSpecifier}/clientExtensions
//    Update the Client Extensions for a Trade. Do not add, update, or delete the Client Extensions if your account is associated with MT4.

    public static @Nullable Trade getTrade(String tradeSpecifier) throws OandaException, TelegramApiException {

        String path = "/v3/accounts/" + accountID + "/trades/" + tradeSpecifier;

        JSONObject jsonObject = makeRequest("GET", path);


        if (
                jsonObject.has("trades")
        ) {
            JSONArray jsonArray = jsonObject.getJSONArray("trades");
            if (jsonArray.length() > 0) {
                JSONObject trade = jsonArray.getJSONObject(0);
                return new Trade(
                        trade.getString("tradeID"),
                        trade.getString("instrument"),
                        trade.getString("side"),
                        trade.getString("quantity"),
                        trade.getString("price"),
                        trade.getString("time"),
                        trade.getString("transactionID"),
                        trade.getString("clientExtensions")
                );
            }
        }

        return null;

    }

    public static void createMarketOrder(String tradePair, String side, double size) {
        try {

            String path = "/v3/accounts/" + accountID + "/trades/" + tradePair + "/orders"+
                    "?side=" + side +
                    "&quantity=" + size;

            JSONObject jsonObject = makeRequest("POST", path);



            if (jsonObject.has("order")) {
                JSONObject order = jsonObject.getJSONObject("order");
                System.out.println(order.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } catch (OandaException e) {
            throw new RuntimeException(e);
        }

    }

    public static double getBalance() {
        return 0;
    }

    public static double getMarginPercent() {
        return 0;
    }

    public static double getOpen() {
        return 0;
    }

    public static double getHigh() {
        return 0;
    }

    public static double getLow() {
        return 0;
    }

    public static double getClose() {
        return 0;
    }

    public static double getVolume() {
        return 0;
    }

    public static double getMarketCap() {
        return 0;
    }
//
//    PUT	/v3/accounts/{accountID}/trades/{tradeSpecifier}/orders
//    Create, replace and cancel a Trade???s dependent Orders (Take Profit, Stop Loss and Trailing Stop Loss) through the Trade itself

    public ArrayList<String> getTradeAbleInstruments() throws OandaException {


        try {

            String path = "/v3/accounts/" + accountID + "/instruments";
            JSONObject playload = makeRequest("GET", path);


            JSONArray instrumentsArray = playload.getJSONArray("instruments");
            for (int i = 0; i < instrumentsArray.length(); i++) {
                instrumentsList.add(i, instrumentsArray.getJSONObject(i).getString("displayName"));

            }
        } catch (Exception e) {
            throw new OandaException(e.getMessage());
            //throw new OandaException(e);
        }


        return instrumentsList;

    }

    public static void createMarketOrder(String symbo, String type, String side, int amount) throws OandaException {
        createOrder(symbo, amount, type, side);
    }

//
//            GET	/v3/accounts/{accountID}
//    Get the full details for a single Account that a client has access to. Full pending Order, open Trade and open Position representations are provided.

    public static Root getAccountFullDetails() throws OandaException {
        root = new Root();
        /*{"lastTransactionID":"101357","account":{"createdByUserID":2783446,"NAV":"3.0537",
         * "marginCloseoutUnrealizedPL":"0.0000","marginCallMarginUsed":"0.0000",
         * "openPositionCount":0,"withdrawalLimit":"3.0537","positionValue":"0.0000",
         * "marginRate":"0.02","marginCallPercent":"0.00000","balance":"3.0537","lastTransactionID":"101357","resettablePL":"-764.0716",
         * "financing":"-11.5775","createdTime":"2019-04-30T02:39:18.895364468Z","alias":"MT4","currency":"USD",
         * "commission":"0.2672","marginCloseoutPercent":"0.00000","id":"001-001-2783446-002","openTradeCount":0,
         * "pendingOrderCount":0,"hedgingEnabled":false,"resettablePLTime":"0","trades":[],
         * "positions":[{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-174.4318","guaranteedExecutionFees":"0.0000","financing":"-6.3885","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-69.5392","guaranteedExecutionFees":"0.0000","financing":"0.8731","units":"0","pl":"-69.5392"},"instrument":"EUR_USD","commission":"0.0000","pl":"-174.4318","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-104.8926","guaranteedExecutionFees":"0.0000","financing":"-7.2616","units":"0","pl":"-104.8926"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.2548","guaranteedExecutionFees":"0.0000","financing":"-0.0129","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.5702","guaranteedExecutionFees":"0.0000","financing":"-0.0024","units":"0","pl":"-0.5702"},"instrument":"EUR_GBP","commission":"0.0000","pl":"-0.2548","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.3154","guaranteedExecutionFees":"0.0000","financing":"-0.0105","units":"0","pl":"0.3154"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-7.5055","guaranteedExecutionFees":"0.0000","financing":"0.0002","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.4026","guaranteedExecutionFees":"0.0000","financing":"0.0018","units":"0","pl":"0.4026"},"instrument":"EUR_AUD","commission":"0.0000","pl":"-7.5055","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-7.9081","guaranteedExecutionFees":"0.0000","financing":"-0.0016","units":"0","pl":"-7.9081"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-6.8286","guaranteedExecutionFees":"0.0000","financing":"-0.5200","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.3465","guaranteedExecutionFees":"0.0000","financing":"-0.0027","units":"0","pl":"-1.3465"},"instrument":"EUR_CAD","commission":"0.0000","pl":"-6.8286","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-5.4821","guaranteedExecutionFees":"0.0000","financing":"-0.5173","units":"0","pl":"-5.4821"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.9599","guaranteedExecutionFees":"0.0000","financing":"-0.0025","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.5949","guaranteedExecutionFees":"0.0000","financing":"-0.0001","units":"0","pl":"-1.5949"},"instrument":"EUR_SGD","commission":"0.0000","pl":"-2.9599","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.3650","guaranteedExecutionFees":"0.0000","financing":"-0.0024","units":"0","pl":"-1.3650"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"3.2567","guaranteedExecutionFees":"0.0000","financing":"-0.1442","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.2801","guaranteedExecutionFees":"0.0000","financing":"-0.0021","units":"0","pl":"0.2801"},"instrument":"EUR_CHF","commission":"0.0000","pl":"3.2567","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"2.9766","guaranteedExecutionFees":"0.0000","financing":"-0.1421","units":"0","pl":"2.9766"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-11.1395","guaranteedExecutionFees":"0.0000","financing":"-0.1106","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-10.0630","guaranteedExecutionFees":"0.0000","financing":"-0.0131","units":"0","pl":"-10.0630"},"instrument":"EUR_JPY","commission":"0.0000","pl":"-11.1395","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.0765","guaranteedExecutionFees":"0.0000","financing":"-0.0975","units":"0","pl":"-1.0765"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-135.9999","guaranteedExecutionFees":"0.0000","financing":"0.3747","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-138.8771","guaranteedExecutionFees":"0.0000","financing":"0.6645","units":"0","pl":"-138.8771"},"instrument":"EUR_NZD","commission":"0.0000","pl":"-135.9999","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"2.8772","guaranteedExecutionFees":"0.0000","financing":"-0.2898","units":"0","pl":"2.8772"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1659","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1659","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.1659"},"instrument":"EUR_HKD","commission":"0.0000","pl":"-0.1659","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.3280","guaranteedExecutionFees":"0.0000","financing":"-0.0078","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1557","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.1557"},"instrument":"EUR_CZK","commission":"0.0000","pl":"-2.3280","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.1723","guaranteedExecutionFees":"0.0000","financing":"-0.0078","units":"0","pl":"-2.1723"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1526","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1526","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.1526"},"instrument":"EUR_NOK","commission":"0.0000","pl":"-0.1526","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0180","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0180","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0180"},"instrument":"EUR_SEK","commission":"0.0000","pl":"-0.0180","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0959","guaranteedExecutionFees":"0.0000","financing":"-0.0001","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"EUR_TRY","commission":"0.0000","pl":"-0.0959","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0959","guaranteedExecutionFees":"0.0000","financing":"-0.0001","units":"0","pl":"-0.0959"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0682","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0682","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0682"},"instrument":"EUR_ZAR","commission":"0.0000","pl":"-0.0682","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-55.9512","guaranteedExecutionFees":"0.0000","financing":"-0.1911","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-44.8502","guaranteedExecutionFees":"0.0000","financing":"-0.1419","units":"0","pl":"-44.8502"},"instrument":"USD_CAD","commission":"0.0000","pl":"-55.9512","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-11.1010","guaranteedExecutionFees":"0.0000","financing":"-0.0492","units":"0","pl":"-11.1010"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.6893","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"USD_SGD","commission":"0.0000","pl":"0.6893","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.6893","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.6893"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-10.3165","guaranteedExecutionFees":"0.0000","financing":"-0.0503","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"1.8985","guaranteedExecutionFees":"0.0000","financing":"-0.3812","units":"0","pl":"1.8985"},"instrument":"USD_CHF","commission":"0.0000","pl":"-10.3165","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-12.2150","guaranteedExecutionFees":"0.0000","financing":"0.3309","units":"0","pl":"-12.2150"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-52.3156","guaranteedExecutionFees":"0.0000","financing":"-0.3810","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-18.0731","guaranteedExecutionFees":"0.0000","financing":"-0.1277","units":"0","pl":"-18.0731"},"instrument":"USD_JPY","commission":"0.1172","pl":"-52.3156","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-34.2425","guaranteedExecutionFees":"0.0000","financing":"-0.2533","units":"0","pl":"-34.2425"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.3140","guaranteedExecutionFees":"0.0000","financing":"-0.1435","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0547","guaranteedExecutionFees":"0.0000","financing":"-0.0055","units":"0","pl":"0.0547"},"instrument":"USD_HKD","commission":"0.0000","pl":"-0.3140","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.3687","guaranteedExecutionFees":"0.0000","financing":"-0.1380","units":"0","pl":"-0.3687"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.7163","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"USD_CZK","commission":"0.0000","pl":"-2.7163","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.7163","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-2.7163"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0077","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"USD_DKK","commission":"0.0000","pl":"-0.0077","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0077","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0077"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1515","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0564","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0564"},"instrument":"USD_MXN","commission":"0.0000","pl":"-0.1515","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0951","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0951"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0108","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"USD_NOK","commission":"0.0000","pl":"-0.0108","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0108","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0108"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0991","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"USD_PLN","commission":"0.0000","pl":"-0.0991","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0991","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0991"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0132","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0132","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0132"},"instrument":"USD_SEK","commission":"0.0000","pl":"-0.0132","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1990","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"USD_THB","commission":"0.0000","pl":"-0.1990","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1990","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.1990"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-11.7711","guaranteedExecutionFees":"0.0000","financing":"-0.0166","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-10.7225","guaranteedExecutionFees":"0.0000","financing":"-0.0092","units":"0","pl":"-10.7225"},"instrument":"USD_CNH","commission":"0.0000","pl":"-11.7711","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.0486","guaranteedExecutionFees":"0.0000","financing":"-0.0074","units":"0","pl":"-1.0486"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-33.1687","guaranteedExecutionFees":"0.0000","financing":"-0.9368","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-23.1159","guaranteedExecutionFees":"0.0000","financing":"-0.0970","units":"0","pl":"-23.1159"},"instrument":"GBP_USD","commission":"0.0000","pl":"-33.1687","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-10.0528","guaranteedExecutionFees":"0.0000","financing":"-0.8398","units":"0","pl":"-10.0528"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1144","guaranteedExecutionFees":"0.0000","financing":"-0.0001","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0075","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0075"},
         * "instrument":"GBP_AUD","commission":"0.0000","pl":"-0.1144","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1069","guaranteedExecutionFees":"0.0000","financing":"-0.0001","units":"0","pl":"-0.1069"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-8.2870","guaranteedExecutionFees":"0.0000","financing":"-0.0355","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-3.0620","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-3.0620"},"instrument":"GBP_CAD","commission":"0.0000","pl":"-8.2870","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-5.2250","guaranteedExecutionFees":"0.0000","financing":"-0.0355","units":"0","pl":"-5.2250"}},
         * {"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.8250","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"GBP_SGD","commission":"0.0000","pl":"-0.8250","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.8250","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.8250"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-4.5789","guaranteedExecutionFees":"0.0000","financing":"-0.0682","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1570","guaranteedExecutionFees":"0.0000","financing":"-0.0687","units":"0","pl":"-0.1570"},"instrument":"GBP_CHF","commission":"0.0000","pl":"-4.5789","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-4.4219","guaranteedExecutionFees":"0.0000","financing":"0.0005","units":"0","pl":"-4.4219"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-9.3425","guaranteedExecutionFees":"0.0000","financing":"-0.0696","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.4019","guaranteedExecutionFees":"0.0000","financing":"-0.0653","units":"0","pl":"0.4019"},"instrument":"GBP_JPY","commission":"0.0000","pl":"-9.3425","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-9.7444","guaranteedExecutionFees":"0.0000","financing":"-0.0043","units":"0","pl":"-9.7444"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.2934","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.2934","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.2934"},"instrument":"GBP_NZD","commission":"0.1500","pl":"-0.2934","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.7998","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.2639","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.2639"},"instrument":"GBP_PLN","commission":"0.0000","pl":"-0.7998","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.5359","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.5359"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0588","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0168","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0168"},"instrument":"GBP_ZAR","commission":"0.0000","pl":"-0.0588","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0420","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.0420"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-68.3675","guaranteedExecutionFees":"0.0000","financing":"-0.4409","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-46.2640","guaranteedExecutionFees":"0.0000","financing":"-0.1092","units":"0","pl":"-46.2640"},"instrument":"AUD_USD","commission":"0.0000","pl":"-68.3675","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-22.1035","guaranteedExecutionFees":"0.0000","financing":"-0.3317","units":"0","pl":"-22.1035"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-18.0252","guaranteedExecutionFees":"0.0000","financing":"-0.0411","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-16.6204","guaranteedExecutionFees":"0.0000","financing":"-0.0072","units":"0","pl":"-16.6204"},"instrument":"AUD_CAD","commission":"0.0000","pl":"-18.0252","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.4048","guaranteedExecutionFees":"0.0000","financing":"-0.0339","units":"0","pl":"-1.4048"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.6495","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.6495","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-2.6495"},"instrument":"AUD_SGD","commission":"0.0000","pl":"-2.6495","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-8.2180","guaranteedExecutionFees":"0.0000","financing":"-0.0008","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.2083","guaranteedExecutionFees":"0.0000","financing":"-0.0090","units":"0","pl":"0.2083"},"instrument":"AUD_CHF","commission":"0.0000","pl":"-8.2180","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-8.4263","guaranteedExecutionFees":"0.0000","financing":"0.0082","units":"0","pl":"-8.4263"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-11.5033","guaranteedExecutionFees":"0.0000","financing":"-0.7496","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-9.0972","guaranteedExecutionFees":"0.0000","financing":"-0.6077","units":"0","pl":"-9.0972"},"instrument":"AUD_JPY","commission":"0.0000","pl":"-11.5033","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.4061","guaranteedExecutionFees":"0.0000","financing":"-0.1419","units":"0","pl":"-2.4061"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-17.4180","guaranteedExecutionFees":"0.0000","financing":"-0.0608","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-11.3032","guaranteedExecutionFees":"0.0000","financing":"-0.0377","units":"0","pl":"-11.3032"},"instrument":"AUD_NZD","commission":"0.0000","pl":"-17.4180","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-6.1148","guaranteedExecutionFees":"0.0000","financing":"-0.0231","units":"0","pl":"-6.1148"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1069","guaranteedExecutionFees":"0.0000","financing":"-0.0008","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.1069","guaranteedExecutionFees":"0.0000","financing":"-0.0008","units":"0","pl":"-0.1069"},"instrument":"AUD_HKD","commission":"0.0000","pl":"-0.1069","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.2722","guaranteedExecutionFees":"0.0000","financing":"-0.0006","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.2758","guaranteedExecutionFees":"0.0000","financing":"-0.0006","units":"0","pl":"-0.2758"},"instrument":"CAD_SGD","commission":"0.0000","pl":"-0.2722","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0036","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0036"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-5.5937","guaranteedExecutionFees":"0.0000","financing":"-0.0086","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.8108","guaranteedExecutionFees":"0.0000","financing":"-0.0095","units":"0","pl":"-2.8108"},"instrument":"CAD_CHF","commission":"0.0000","pl":"-5.5937","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.7829","guaranteedExecutionFees":"0.0000","financing":"0.0009","units":"0","pl":"-2.7829"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-6.6766","guaranteedExecutionFees":"0.0000","financing":"-0.0047","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-4.1264","guaranteedExecutionFees":"0.0000","financing":"-0.0157","units":"0","pl":"-4.1264"},"instrument":"CAD_JPY","commission":"0.0000","pl":"-6.6766","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.5502","guaranteedExecutionFees":"0.0000","financing":"0.0110","units":"0","pl":"-2.5502"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.9540","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.9540","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-0.9540"},"instrument":"CAD_HKD","commission":"0.0000","pl":"-0.9540","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.8765","guaranteedExecutionFees":"0.0000","financing":"-0.0040","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.8765","guaranteedExecutionFees":"0.0000","financing":"-0.0040","units":"0","pl":"-1.8765"},"instrument":"SGD_CHF","commission":"0.0000","pl":"-1.8765","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-11.2861","guaranteedExecutionFees":"0.0000","financing":"-0.0318","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.4127","guaranteedExecutionFees":"0.0000","financing":"-0.0052","units":"0","pl":"0.4127"},"instrument":"SGD_JPY","commission":"0.0000","pl":"-11.2861","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-11.6988","guaranteedExecutionFees":"0.0000","financing":"-0.0266","units":"0","pl":"-11.6988"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0281","guaranteedExecutionFees":"0.0000","financing":"-0.0001","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0281","guaranteedExecutionFees":"0.0000","financing":"-0.0001","units":"0","pl":"0.0281"},"instrument":"CHF_JPY","commission":"0.0000","pl":"0.0281","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0021","guaranteedExecutionFees":"0.0000","financing":"-0.0002","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"CHF_ZAR","commission":"0.0000","pl":"-0.0021","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0021","guaranteedExecutionFees":"0.0000","financing":"-0.0002","units":"0","pl":"-0.0021"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-68.7120","guaranteedExecutionFees":"0.0000","financing":"-1.2905","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-35.2630","guaranteedExecutionFees":"0.0000","financing":"-0.7206","units":"0","pl":"-35.2630"},"instrument":"NZD_USD","commission":"0.0000","pl":"-68.7120","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-33.4490","guaranteedExecutionFees":"0.0000","financing":"-0.5699","units":"0","pl":"-33.4490"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-3.8232","guaranteedExecutionFees":"0.0000","financing":"-0.2521","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"3.8895","guaranteedExecutionFees":"0.0000","financing":"-0.1901","units":"0","pl":"3.8895"},"instrument":"NZD_CAD","commission":"0.0000","pl":"-3.8232","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-7.7127","guaranteedExecutionFees":"0.0000","financing":"-0.0620","units":"0","pl":"-7.7127"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-6.3953","guaranteedExecutionFees":"0.0000","financing":"0.0120","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.4028","guaranteedExecutionFees":"0.0000","financing":"-0.0003","units":"0","pl":"-0.4028"},"instrument":"NZD_CHF","commission":"0.0000","pl":"-6.3953","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-5.9925","guaranteedExecutionFees":"0.0000","financing":"0.0123","units":"0","pl":"-5.9925"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.6892","guaranteedExecutionFees":"0.0000","financing":"0.0016","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.3974","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.3974"},"instrument":"NZD_JPY","commission":"0.0000","pl":"-1.6892","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-2.0866","guaranteedExecutionFees":"0.0000","financing":"0.0016","units":"0","pl":"-2.0866"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0283","guaranteedExecutionFees":"0.0000","financing":"-0.0001","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-0.0515","guaranteedExecutionFees":"0.0000","financing":"-0.0001","units":"0","pl":"-0.0515"},"instrument":"NZD_HKD","commission":"0.0000","pl":"-0.0283","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0232","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0232"}},{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.1650","guaranteedExecutionFees":"0.0000","financing":"0.0000","short":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"0.0000","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"0.0000"},"instrument":"TRY_JPY","commission":"0.0000","pl":"-1.1650","long":{"dividendAdjustment":"0.0000","unrealizedPL":"0.0000","resettablePL":"-1.1650","guaranteedExecutionFees":"0.0000","financing":"0.0000","units":"0","pl":"-1.1650"}}],"marginAvailable":"3.0537","dividendAdjustment":"0","marginCloseoutPositionValue":"0.0000","marginCloseoutMarginUsed":"0.0000","unrealizedPL":"0.0000","marginCloseoutNAV":"3.0537","guaranteedStopLossOrderMode":"DISABLED","marginUsed":"0.0000","guaranteedExecutionFees":"0.0000",
         * "orders":[],"pl":"-764.0716"}}*/

        String path = "/v3/accounts/" + accountID;
        JSONObject playload = makeRequest("GET", path);

        if (playload.has("lastTransactionID")) {
            root.lastTransactionID =
                    playload.getString("lastTransactionID");
        }
        if (playload.has("lastTransactionTime")) {
            root.lastTransactionTime =
                    playload.getString("lastTransactionTime");
        }
        if (playload.has("account")) {

            if (
                    playload.getJSONObject("account").has("createdByUserID")
            ) {
                root.account.createdByUserID = playload.getJSONObject("account").getInt("createdByUserID");

            }
            if (
                    playload.getJSONObject("account").has("NAV")) {

                root.account.nAV =
                        playload.getJSONObject("account").getString("NAV");
            }
            if (
                    playload.getJSONObject("account").has("marginCloseoutUnrealizedPL")) {

                root.account.marginCloseoutUnrealizedPL =
                        playload.getJSONObject("account").getString("marginCloseoutUnrealizedPL");
            }

            if (
                    playload.getJSONObject("account").has("marginCallMarginUsed")) {

                root.account.marginCallMarginUsed =
                        playload.getJSONObject("account").getString("marginCallMarginUsed");
            }

            if (
                    playload.getJSONObject("account").has("openPositionCount")) {

                root.account.openPositionCount =
                        playload.getJSONObject("account").getInt("openPositionCount");
            }


            if (
                    playload.getJSONObject("account").has("withdrawalLimit")) {

                root.account.withdrawalLimit = String.valueOf(playload.getJSONObject("account").getDouble("withdrawalLimit"));
            }

            if (
                    playload.getJSONObject("account").has("positionValue")) {

                root.account.positionValue =
                        String.valueOf(playload.getJSONObject("account").getDouble("positionValue"));
            }


            if (
                    playload.getJSONObject("account").has("marginRate")) {

                root.account.marginRate =
                        playload.getJSONObject("account").getDouble("marginRate");
            }

            if (
                    playload.getJSONObject("account").has("marginCallPercent")) {
                root.account.marginCallPercent = String.valueOf(playload.getJSONObject("account").getDouble("marginCallPercent"));
            }

            if (
                    playload.getJSONObject("account").has("balance")) {

                root.account.balance =
                        playload.getJSONObject("account").getDouble("balance");
            }

            if (
                    playload.getJSONObject("account").has("resettablePL")) {

                root.account.resettablePL =
                        playload.getJSONObject("account").getDouble("resettablePL");
            }

            if (
                    playload.getJSONObject("account").has("financing")) {


                root.account.financing =
                        playload.getJSONObject("account").getDouble("financing");
            }

            if (
                    playload.getJSONObject("account").has("createdTime")) {

                root.account.createdTime =
                        playload.getJSONObject("account").getString("createdTime");
            }

            if (
                    playload.getJSONObject("account").has("alias")) {

                root.account.alias =
                        playload.getJSONObject("account").getString("alias");
            }

            if (
                    playload.getJSONObject("account").has("currency")) {

                root.account.currency =
                        playload.getJSONObject("account").getString("currency");
            }

            if (
                    playload.getJSONObject("account").has("commission")) {

                root.account.commission = playload.getJSONObject("account").getDouble("commission");

            }
            if (
                    playload.getJSONObject("account").has("marginCloseoutPercent")) {

                root.account.marginCloseoutPercent =
                        playload.getJSONObject("account").getDouble("marginCloseoutPercent");


            }
            if (
                    playload.getJSONObject("account").has("id")) {

                root.account.id =
                        playload.getJSONObject("account").getString("id");

            }


            if (
                    playload.getJSONObject("account").has("openTradeCount")) {

                root.account.openTradeCount =
                        playload.getJSONObject("account").getInt("openTradeCount");
            }

            if (
                    playload.getJSONObject("account").has("pendingOrderCount")) {

                root.account.pendingOrderCount =
                        playload.getJSONObject("account").getInt("pendingOrderCount");
            }
            if (
                    playload.getJSONObject("account").has("hedgingEnabled")) {

                root.account.hedgingEnabled =
                        playload.getJSONObject("account").getBoolean("hedgingEnabled");
            }

            if (
                    playload.getJSONObject("account").has("resettablePLTime")) {

                root.account.resettablePLTime =

                        playload.getJSONObject("account").getString("resettablePLTime");
            }

            if (
                    playload.getJSONObject("account").has("pendingOrderCount")) {

                root.account.pendingOrderCount =
                        playload.getJSONObject("account").getInt("pendingOrderCount");
            }


            if (playload.getJSONObject("account").has("positions")) {
                for (
                        Object o :
                        playload.getJSONObject("account").getJSONArray("positions")) {

                    JSONObject position = (JSONObject) o;
                    positionObj = new Position();
                    positionObj.commission = String.valueOf(position.getDouble("commission"));
                    positionObj.dividendAdjustment =
                            String.valueOf(position.getDouble("dividendAdjustment"));

                    positionObj.guaranteedExecutionFees =
                            String.valueOf(position.getDouble("guaranteedExecutionFees"));

                    positionObj.pl = String.valueOf(position.getDouble("pl"));

                    positionObj.instrument =
                            position.getString("instrument");

                    positionObj.financing =
                            position.getString("financing");

                    positionObj.resettablePL =
                            String.valueOf(position.getDouble("resettablePL"));

                    positionObj.unrealizedPL =
                            String.valueOf(position.getDouble("unrealizedPL"));

                    if (position.has("long")) {


                        positionObj.mylong = Long.getLong(String.valueOf(position.getJSONObject("long")));

                    } else if (position.has("short")) {


                        JSONObject longObj = position.getJSONObject("short");


                        if (longObj != null) {

                            if (
                                    longObj.has("dividendAdjustment")) {
                                positionObj.myshort.dividendAdjustment =
                                        String.valueOf(longObj.getDouble("dividendAdjustment"));
                            }

                            if (
                                    longObj.has("unrealizedPL")) {

                                positionObj.myshort.unrealizedPL =
                                        String.valueOf(longObj.getDouble("unrealizedPL"));
                            }

                            if (
                                    longObj.has("resettablePL")) {

                                positionObj.myshort.resettablePL =
                                        String.valueOf(longObj.getDouble("resettablePL"));
                            }
                            if (
                                    longObj.has("guaranteedExecutionFees")) {

                                positionObj.myshort.guaranteedExecutionFees =
                                        String.valueOf(longObj.getDouble("guaranteedExecutionFees"));
                            }

                            if (
                                    longObj.has("pl")) {

                                positionObj.myshort.pl =
                                        String.valueOf(longObj.getDouble("pl"));
                            }

                            if (
                                    longObj.has("units")) {

                                positionObj.myshort.units =
                                        String.valueOf(longObj.getInt("units"));

                            }
                            if (
                                    longObj.has("financing")) {

                                positionObj.myshort.financing =
                                        String.valueOf(longObj.getDouble("financing"));
                            }

                        }


                    }
                }


            }


        }
        return root;

    }
//
//    GET	/v3/accounts/{accountID}/summary
//    Get a summary for a single Account that a client has access to.

    private static @Nullable CandleData getCandle(String accountInstrument, int index) throws OandaException {


        String path = "/v3/instruments/" + accountInstrument + "/candles";
        JSONObject payload = makeRequest("GET", path);

        if (payload.has("candles")) {
            JSONArray candles = payload.getJSONArray("candles");
            for (int i = 0; i < candles.length(); i++) {
                JSONObject jsonObject = candles.getJSONObject(i).getJSONObject("mid");
                String time = candles.getJSONObject(i).getString("time");


                int volume = candles.getJSONObject(i).getInt("volume");
                System.out.println("Candles " + Trade.candle);
                Trade.candle = new CandleData(

                        Double.parseDouble(jsonObject.getString("o")),
                        Double.parseDouble(jsonObject.getString("c")),

                        Double.parseDouble(jsonObject.getString("h")),
                        Double.parseDouble(jsonObject.getString("l")),

                        DateToInt(time),
                        Double.parseDouble(String.valueOf(volume)));

                array.add(i, Trade.candle);

            }
            return array.get(index);
        } else {

            Log.info("Instrument " + instrument + " does not have candles", news.toString());
        }

        return new CandleData(0, 0, 0, 0, 0, 0);
    }

    private static @NotNull JSONObject makeRequest(String method, String path) throws OandaException {


        JSONObject payload = null;
        try {
            String url = getHost() + path;
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6)");
            connection.setRequestProperty("Authorization", "Bearer " + api_key);//"3285e03f03fbff5da0be47c99d00219c-6e783e35a9bd5658f2ec46d717132e21");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setRequestMethod(method);

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == 200) {
                InputStream in = connection.getInputStream();
                payload = new JSONObject(new JSONTokener(new InputStreamReader(in)));
                in.close();
                System.out.println(payload);
                return payload;
            } else {
                throw new OandaException(connection.getResponseMessage() + "  | " + connection.getResponseCode());
            }

        } catch (Exception e) {
            throw new OandaException("Error making request: " + e.getMessage());

        }
    }

    public static @NotNull ArrayList<OandaOrder> getOrdersList() throws OandaException {
        ArrayList<OandaOrder> oandaOrders = new ArrayList<>();
        String path = "/v3/accounts/" + accountID + "/orders";
        JSONObject payload = makeRequest("GET", path);


        System.out.println("order " + payload);
        for (int i = 0; i < payload.getJSONArray("orders").length(); i++) {

            OandaOrder oandaOrder = new OandaOrder();
            oandaOrder.setOrderId(payload.getJSONArray("orders").getJSONObject(i).get("orderId").toString());
            oandaOrder.setPrice(payload.getJSONArray("orders").getJSONObject(i).get("price"));
            oandaOrder.setSide(payload.getJSONArray("orders").getJSONObject(i).get("type"));
            oandaOrder.setUnits(payload.getJSONArray("orders").getJSONObject(i).get("units"));
            oandaOrders.add(i, oandaOrder);
        }
        Log.info("Orders " + oandaOrders, news.toString());


        return oandaOrders;

    }


//            GET	/v3/accounts/{accountID}/changes



    static int DateToInt(String time) {
        return (int) Date.from(Instant.parse(time)).getTime();


    }
//    Endpoint used to poll an Account for its current state and changes since a specified TransactionID.

    public static @Nullable OrderBook getOrderBook(String instrument) throws OandaException {
        String path = "/v3/instruments/" + instrument + "/orderBook";
        JSONObject payload = makeRequest("GET", path);
        if (
                payload.has("orderBook")) {

            return new OrderBook().setBuckets(payload.getJSONObject("orderBook").getJSONArray("buckets").toList());

        } else
            System.out.println(
                    "Instrument " + instrument + " does not have orderBook");

        return
                null;

    }

//
//    Pricing Endpoints
//
//
//
//    GET	/v3/accounts/{accountID}/candles/latest
//    Get dancing bears and most recently completed candles within an Account for specified combinations of instrument, granularity, and price component.

    public static ArrayList<CandleData> getForexCandles(String string) throws OandaException, InterruptedException {


        System.out.println("instruments list" + instrumentsList);
        for (int i = 0; i < instrumentsList.size(); i++) {
            //Get all forex symbols candles
            array.add(getCandle((string), i));
        }
        return array;

    }

    public static void createOrder(String symbol, double quantity, String price, String side) throws OandaException {

        //todo create order update
        String path = "/v3/accounts/" + accountID + "/orders";
        JSONObject payload = makeRequest("POST", path);
        payload.put("symbol", symbol);
        payload.put("quantity", quantity);
        payload.put("price", price);
        payload.put("side", side);

        if (
                payload.getJSONObject("data") != null &&
                        payload.getJSONObject("data").get("orderId") != null &&
                        payload.getJSONObject("data").get("orderId") != null
        ) {
            payload.getJSONObject("data").get("orderId");
        }
    }
//
//    GET	/v3/accounts/{accountID}/pricing
//    Get pricing information for a specified list of Instruments within an Account.

    public static String getApi_key() {
        return api_key;
    }
//
//    GET	/v3/accounts/{accountID}/pricing/stream
//    Get a stream of Account Prices starting from when the request is made. This pricing stream does not include every single price created for the Account, but instead will provide at most 4 prices per second (every 250 milliseconds) for each instrument being requested. If more than one price is created for an instrument during the 250 millisecond window, only the price in effect at the end of the window is sent. This means that during periods of rapid price movement, subscribers to this stream will not be sent every price. Pricing windows for different connections to the price stream are not all aligned in the same way (i.e. they are not all aligned to the top of the second). This means that during periods of rapid price movement, different subscribers may observe different prices depending on their alignment.

    public static String getHost() {
        return host;
    }
//    Note: This endpoint is served by the streaming URLs.


//    Instrument Endpoints
//
//
//
//    GET	/v3/instruments/{instrument}/candles
//    Fetch candlestick data for an instrument.

    public ArrayList<String> getForexSymbols() throws OandaException {
        return getTradeAbleInstruments();

    }

    public static void setAccountID(String accountID) {
        Oanda.accountID = accountID;
    }

    public List<Trade> getTrades() throws OandaException {

        String path = "/v3/accounts/" + accountID + "/trades";
        JSONObject jsonObject11 = makeRequest("GET", path);
        JSONArray jsonArray = jsonObject11.getJSONArray("trades");

        List<Trade> trades = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject trade = jsonArray.getJSONObject(i);


            System.out.println("trade -->" + trades.get(i));

        }

        return trades;
    }

//
//    GET	/v3/instruments/{instrument}/orderBook
//    Fetch an order book for an instrument.

    public void updateTradeClientExtensions(String tradeSpecifier) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/trades/" + tradeSpecifier + "/clientExtensions";

        JSONObject jsonObject = makeRequest("PUT", path);

        if (
                jsonObject.has("clientExtensions")
        ) {
            JSONArray jsonArray = jsonObject.getJSONArray("clientExtensions");
            if (jsonArray.length() > 0) {
                JSONObject trade = jsonArray.getJSONObject(0);
                System.out.println(trade);
            }
        }
    }

//    GET	/v3/instruments/{instrument}/positionBook
//    Fetch a position book for an instrument.

    public void createTradeOrders(String tradeSpecifier) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/trades/" + tradeSpecifier + "/orders";

        JSONObject jsonObject = makeRequest("PUT", path);

        if (
                jsonObject.has("orders")
        ) {
            JSONArray jsonArray = jsonObject.getJSONArray("orders");
            if (jsonArray.length() > 0) {
                JSONObject trade = jsonArray.getJSONObject(0);
                System.out.println(trade);
            }

        }
    }

    //String urls = String.format("https://api-fxtrade.oanda.com/v3/instruments/" + tradePair.toString('_') + "/candles?count=10&price=A&from=" + startDateString + "&granularity=" + actualGranularity);
    // static String api_key = "7e0018e5e2e0d287c854c5bd8a509712-2c4ed485f470ed2db68159fb308272a8";

//
//    Position Endpoints
//
//
//
//    GET	/v3/accounts/{accountID}/positions
//    List all Positions for an Account. The Positions returned are for every instrument that has had a position during the lifetime of an  Account.

    //    Account Endpoints
//
//
//
//    GET	/v3/accounts
//    Get a list of all Accounts authorized for the provided token.
    ArrayList<Accounts> getTokenAuthorizedList() throws OandaException {
        String path = "/v3/accounts";

        JSONObject payload = makeRequest("GET", path);

        JSONArray accounts = payload.getJSONArray("accounts");
        ArrayList<Accounts> account_ids = new ArrayList<>();
        for (int i = 0; i < accounts.length(); i++) {
            JSONObject account = accounts.getJSONObject(i);
            account.get("accountId");

            Account accountObj = new Account();

            System.out.println(accountObj);

        }
        return account_ids;


    }

    double getPricingStream() throws OandaException {
        String path = "/v3/accounts/" + accountID + "/pricing/stream";

        JSONObject payload = makeRequest("GET", path);
        System.out.println(payload);
        payload.getJSONObject("instruments");
        return 0;
    }
//            GET	/v3/accounts/{accountID}/positions/{instrument}
//    Get the details of a single Instrument???s Position in an Account. The Position may by open or not.

    String patchAccountConfiguration() throws OandaException {
        String path = "/v3/accounts/" + accountID + "/configuration";

        JSONObject accountConfiguration = makeRequest("GET", path);
        System.out.println(accountConfiguration);
        return accountConfiguration.toString();


    }
//
//            PUT	/v3/accounts/{accountID}/positions/{instrument}/close

    private Date StringToDate(@NotNull String time) throws ParseException {
        // "2017-05-05T21:00:00.000000000Z
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(time.replace("0000000Z", ""));

    }
//    Closeout the open Position for a specific instrument in an Account.
    /*
     * Create a new Oanda account
     * <p>
     *     This method
     *     above
     *     creates
     *     a new O
     * */

    public List<Trade> getOpenTrades() throws OandaException {

        String path = "/v3/accounts/" + accountID + "/openTrades";

        JSONObject jsonObject = makeRequest("GET", path);

        JSONArray jsonArray = jsonObject.getJSONArray("trades");

        List<Trade> trades = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {


            System.out.println("trade -->" + trades.get(i));
        }

        return trades;
    }

    double getPricing(String accountInstrument) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/pricing?instruments=" + accountInstrument;

        JSONObject playload = makeRequest2("GET", path);
        System.out.println(playload);
        //  playload.getJSONObject("instruments");
        return 0;
    }

    List<Object> getAllPositions() throws OandaException {
        String path = "/v3/accounts/" + accountID + "/positions";

        JSONObject accountConfiguration = makeRequest("GET", path);
        System.out.println(accountConfiguration);
        return accountConfiguration.getJSONArray("positions").toList();
    }

    //
//    GET	/v3/accounts/{accountID}/openPositions
//    List all open Positions for an Account. An open Position is a Position in an Account that currently has a Trade opened for it.
    public List<Position> getAllOpenPositions() throws OandaException {
        String path = "/v3/accounts/" + accountID+ "/positions";

        JSONObject accountPositions = makeRequest("GET", path);


        ArrayList<Position> p = new ArrayList<>();
        if (accountPositions.has("positions")) {
            JSONArray positions = accountPositions.getJSONArray("positions");
            for (int i = 0; i < positions.length(); i++) {
                JSONObject position = positions.getJSONObject(i);
                System.out.println("Position " + position);
                if (position.has("instrument")) {
                    System.out.println("Instrument " + position.getString("instrument"));
                    p.add(new Position().setInstrument(position.getString("instrument")));
                }
                if (
                        position.has("pl")) {

                    p.add(new Position().setPl(position.getString("pl")));
                    System.out.println("Position " + position.getString("pl"));
                }

                if (
                        position.has("commission")) {
                    System.out.println("Commission " + position.getString("commission"));
                    p.add(new Position().setCommission(position.getString("commission")));
                }

                Position posit = new Position();


                p.add(i, posit);
            }


        } else {
            System.out.println("Account " + accountID+ " does not have positions");
            return null;
        }
        return p;


    }

    public JSONObject get(String path) throws OandaException {

        return makeRequest("GET", path);

    }

    public JSONObject post(String path) throws OandaException {

        return makeRequest("POST", path);
    }

    public JSONObject put(String path) throws OandaException {

        return makeRequest("PUT", path);
    }

    public JSONObject delete(String path) throws OandaException {

        return makeRequest("DELETE", path);
    }

    public void closeTrade(String tradeSpecifier) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/trades/" + tradeSpecifier + "/close";

        JSONObject jsonObject = makeRequest("PUT", path);

        if (
                jsonObject.has("trades")
        ) {
            JSONArray jsonArray = jsonObject.getJSONArray("trades");
            if (jsonArray.length() > 0) {
                JSONObject trade = jsonArray.getJSONObject(0);
                System.out.println(trade);
            }
        }


    }

    public String getPositionDetails(String accountInstrument) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/positions/" + accountInstrument;
        JSONObject payload = makeRequest("GET", path);
        System.out.println(payload);
        return payload.toString();

    }

    public String closeAccountPositions(String accountInstrument) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/positions/" + accountInstrument + "/close";
        JSONObject accountPositions = makeRequest("GET", path);
        System.out.println(accountPositions);
        return accountPositions.toString();
    }

    public PositionBook getPositionBook(String instrument) throws OandaException {
        String path = "/v3/instruments/" + instrument + "/positionBook";
        JSONObject payload = makeRequest("GET", path);
        if (
                payload.has("positionBook")) {
            return new PositionBook().setBuckets((Collection<?>) payload.getJSONObject("positionBook").
                    getJSONArray("buckets"));
        } else {
            System.out.println(
                    "Instrument " + instrument + " does not have positionBook");

            alert.setContentText(
                    "Instrument " + instrument + " does not have positionBook"

            );

            alert.showAndWait();
        }
        return null;
    }

    private @NotNull JSONObject makeRequest2(String method, String path) throws OandaException {
        String host = "https://api-fxtrade.oanda.com";
        String url = host + path;
        try {
        
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();

            connection.setRequestProperty("Authorization", "Bearer " + api_key);//"3285e03f03fbff5da0be47c99d00219c-6e783e35a9bd5658f2ec46d717132e21");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("AcceptDatetime-Format", String.valueOf(new Date()));

            connection.setRequestProperty(
                    "Content-Encoding",
                    "gzip"
            );
            connection.setRequestProperty("Transfer-Encoding", "chunked");

            connection.setRequestProperty("Access-Control-Allow-Origin", "*");
            connection.setRequestProperty("Access-Control-Allow-Methods", "PUT, PATCH, POST, GET, OPTIONS, DELETE");
            connection.setRequestProperty("Access-Control-Allow-Headers", "Origin, X-Requested-");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestMethod(method);

            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();

            if (response == 200) {
                InputStream in = connection.getInputStream();
                JSONObject payload = new JSONObject(new JSONTokener(new InputStreamReader(in)));
                in.close();
                System.out.println(payload);
                return payload;
            } else {
                alert.setTitle("NETWORK ERROR");
                alert.setContentText(

                        connection.getResponseCode()

                                + " "
                                + connection.getResponseMessage()
                );
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.showAndWait();
                throw new org.tradeexpert.tradeexpert.oanda.OandaException(connection.getResponseMessage() + "  | " + connection.getResponseCode());
            }

        } catch (Exception e) {
            throw new OandaException("Error making request: " + e.getMessage());

        }
    }

    public String getAccountSummary() throws OandaException {
        String path = "/v3/accounts/" + accountID + "/summary";

        JSONObject payload = makeRequest("GET", path);
        System.out.println(payload);
        return payload.toString();


    }

    public void getAccountChanges() throws OandaException {
        String path = "/v3/accounts/" + accountID + "/changes";

        JSONObject payload = makeRequest("GET", path);
        System.out.println(payload);


    }

    String
    getWithdrawalHistory(String currency) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/withdrawal_history";
        JSONObject payload = makeRequest("GET", path);
        return payload.getJSONObject("data").get(currency).toString();
    }

    public String getAccountInstrument() {
        return accountInstrument;
    }

    public ArrayList<Double> getForexBidData() throws OandaException {

        String path = "/v3/accounts/" + accountID + "/forex_bid";
        JSONObject payload = makeRequest("GET",path);
        return null;
    }


//
//    GET	/v3/accounts/{accountID}/pricing/stream
//    Get a stream of Account Prices starting from when the request is made. This pricing stream does not include every single price created for the Account, but instead will provide at most 4 prices per second (every 250 milliseconds) for each instrument being requested. If more than one price is created for an instrument during the 250 millisecond window, only the price in effect at the end of the window is sent. This means that during periods of rapid price movement, subscribers to this stream will not be sent every price. Pricing windows for different connections to the price stream are not all aligned in the same way (i.e. they are not all aligned to the top of the second). This means that during periods of rapid price movement, different subscribers may observe different prices depending on their alignment.

    public CandleData getLatestCandle(String accountInstrument) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/candles/latest";

        JSONObject payload = makeRequest("GET", path);
        System.out.println(payload);
        payload.getJSONObject("candles");
        return new CandleData(


                Double.parseDouble(payload.getJSONObject("candles").getString("o")),

                Double.parseDouble(payload.getJSONObject("candles").getString("c")),
                Double.parseDouble(payload.getJSONObject("candles").getString("h")),
                Double.parseDouble(payload.getJSONObject("candles").getString("l")),
                Integer.parseInt(payload.getJSONObject("candles").getString("time")),
                Double.parseDouble(payload.getJSONObject("candles").getString("volume")));
    }

    public String
    getWithdrawalAddress(String currency) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/withdrawal_address";
        JSONObject payload = makeRequest("GET", path);
        return payload.getJSONObject("data").get(currency).toString();
    }

    public String
    getDepositHistory(String currency) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/deposit_history";
        JSONObject payload = makeRequest("GET", path);
        return payload.getJSONObject("data").get(currency).toString();
    }

//
//
//
//
//    POST	/v3/accounts/{accountID}/orders
//    Create an Order for an Account

    public String


    getDepositAddress(String currency) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/deposit_address";
        JSONObject payload = makeRequest("GET", path);
        return payload.getJSONObject("data").get(currency).toString();
    }
//
//    GET	/v3/accounts/{accountID}/orders
//    Get a list of Orders for an Account

    public JSONObject getPriceStream(String accountInstrument) throws OandaException {

        String path = "/v3/accounts/" + accountID + "/pricing/stream/" + accountInstrument;
        System.out.println(path);
        JSONObject payload = makeRequest2("GET", path);
        System.out.println(payload);
        return payload;
    }

//
//    GET	/v3/accounts/{accountID}/pendingOrders
//    List all pending Orders in an Account

    public ArrayList<OandaOrder> getPendingOrdersList() throws OandaException {
        ArrayList<OandaOrder> oandaOrders = new ArrayList<>();
        String path = "/v3/accounts/" + accountID + "/pendingOrders";
        JSONObject payload = makeRequest("GET", path);
        System.out.println(payload);
        for (int i = 0; i < payload.getJSONArray("orders").length(); i++) {
            OandaOrder oandaOrder = null;
            oandaOrder.setOrderId(payload.getJSONArray("orders").getJSONObject(i).
                    get("orderId").toString());

            oandaOrder.setPrice(payload.getJSONArray("orders").getJSONObject(i).
                    get("price").toString());
        }
        return oandaOrders;
    }

    //    GET	/v3/accounts/{accountID}/orders/{orderSpecifier}
//    Get details for a single Order in an Account
    public Order getOrderDetails(String orderSpecifier) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/orders/" + orderSpecifier;
        JSONObject payload = makeRequest("GET", path);
        if (
                payload.getJSONObject("orders") != null &&
                        payload.getJSONObject("orders").get("orderId") != null
        ) {
            return new Order(    payload.getJSONObject("orders").get("time").toString(),
                    TRADE_ORDER_TYPE.BUY,
                    Double.parseDouble(payload.getJSONObject("orders").get("price").toString()),
                    Double.parseDouble(payload.getJSONObject("orders").get("size").toString()),
                    Double.parseDouble(payload.getJSONObject("orders").get("price").toString()),
                    Double.parseDouble(payload.getJSONObject("orders").get("size").toString())


            );
        }
        return null;
    }
    public CompletableFuture<List<Trade>> fetchRecentTradesUntil(String tradePair, Instant stopAt) {
        Objects.requireNonNull(tradePair);
        Objects.requireNonNull(stopAt);
        if (stopAt.isAfter(Instant.now())) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        CompletableFuture<List<Trade>> futureResult = new CompletableFuture<>();

        // It is not easy to fetch trades concurrently because we need to get the "cb-after" header after each request.
        CompletableFuture.runAsync(() -> {
            IntegerProperty afterCursor = new SimpleIntegerProperty(-1);
            List<Trade> tradesBeforeStopTime = new ArrayList<>();

//            // For Public Endpoints, our rate limit is 3 requests per second, up to 6 requests per second in
//            // burst.
//            // We will know if we get rate limited if we get a 429 response code.
//            // FIXME: We need to address this!
            for (int i = 0; !futureResult.isDone(); i++) {
                String uriStr = "https://api-fxtrade.oanda.com/v3/accounts/" + accountID +
                        "/trades/instruments" + tradePair;

                if (i != 0) {
                    uriStr += "?cb-after=" + afterCursor.get();
                }

                try {
                    HttpRequest.Builder re = HttpRequest.newBuilder();
                    re.header("Authorization", "Bearer " + api_key);
                    re.header(
                            "Content-Type",
                            "application/json"
                    );
                    re.header(
                            "Accept",
                            "application/json"
                    );
                    re.uri(URI.create(uriStr));
                    HttpResponse<String> response = HttpClient.newHttpClient().send(re.build(), HttpResponse.BodyHandlers.ofString());

                    Log.info("response headers: " , String.valueOf(response.headers()));
                    if (response.headers().firstValue("cb-after").isEmpty()) {
                        futureResult.completeExceptionally(new RuntimeException(
                                "OANDA trades response did not contain header \"cb-after\": " + response));
                        return;
                    }

                    afterCursor.setValue(Integer.valueOf((response.headers().firstValue("cb-after").toString())));

                    JsonNode tradesResponse = OBJECT_MAPPER.readTree(response.toString());
                    out.println(response);

                    if (!tradesResponse.isArray()) {
                        futureResult.completeExceptionally(new RuntimeException(
                                "Oanda trades response was not an array!"));
                    }
                    if (tradesResponse.isEmpty()) {
                        out.println("Oanda trades response was empty!");
                        futureResult.completeExceptionally(new IllegalArgumentException("tradesResponse was empty"));
                    } else {
                        for (JsonNode j : tradesResponse) {

                            Instant time = Instant.from(Instant.ofEpochSecond(Trade.candle.getOpenTime()));
                            if (time.compareTo(stopAt) <= 0) {
                                futureResult.complete(tradesBeforeStopTime);
                                break;
                            } else {
                                tradesBeforeStopTime.add(new Trade(tradePair,
                                        DefaultMoney.ofFiat(j.get("price").asDouble(), tradePair),
                                        DefaultMoney.ofCrypto(j.get("size").asDouble(), tradePair),
                                        Side.getSide(j.get("side").toString()), j.get("trade_id").asLong(), time));
                                out.println("Trade: " + time + " " + tradePair + " " + j.get("price"));
                            }
                        }
                    }
                } catch (IOException | InterruptedException ex) {
                    Log.error("ex: " + ex);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return futureResult;
    }

    @Override
    public CompletableFuture<Optional<InProgressCandleData>> fetchCandleDataForInProgressCandle(String tradePair, Instant currentCandleStartedAt, long secondsIntoCurrentCandle, int secondsPerCandle) {

                        String startDateString = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.ofInstant(
                    currentCandleStartedAt, ZoneOffset.UTC));
            long idealGranularity = Math.max(10, secondsIntoCurrentCandle / 200);
            // Get the closest supported granularity to the ideal granularity.
//            int actualGranularity = getCandleDataSupplier(secondsPerCandle, tradePair).getSupportedGranularities().stream()
//                    .min(Comparator.comparingInt(i -> (int) Math.abs(i - idealGranularity)))
//                    .orElseThrow(() -> new NoSuchElementException("Supported granularities was empty!"));

            String x;
            String str;
            if (secondsPerCandle < 3600) {
                x = String.valueOf(secondsPerCandle / 60);
                str = "M";
            } else if (secondsPerCandle < 86400) {
                x = String.valueOf((secondsPerCandle / 3600));
                str = "H";
            } else if (secondsPerCandle < 604800) {
                x = "";//String.valueOf(secondsPerCandle / 86400);
                str = "D";
            } else if (secondsPerCandle < 2592000) {
                x = String.valueOf((secondsPerCandle / 604800));
                str = "W";
            } else {
                x = String.valueOf((secondsPerCandle * 7 / 2592000 / 7));
                str = "M";
            }


            String granularity = str + x;
            //   String uriStr = "https://api-fxtrade.oanda.com/v3/instruments/" + tradePair + "/candles?price=BA&from=2016-10-17T15%3A00%3A00.000000000Z&granularity=" + granularity;
            String uriStr = "https://api-fxtrade.oanda.com/v3/instruments/" + tradePair + "/candles?price=BA&from=2016-10-17T15%3A00%3A00.000000000Z&granularity=" + granularity;

            return HttpClient.newHttpClient().sendAsync(
                            HttpRequest.newBuilder()
                                    .uri(URI.create(uriStr)).GET().build(),
                            HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(response -> {
                        Log.info("Oanda response: ", response);
                        JsonNode res;
                        try {
                            res = OBJECT_MAPPER.readTree(response);
                        } catch (JsonProcessingException ex) {
                            throw new RuntimeException(ex);
                        }

                        if (res.isEmpty()) {
                            out.println("Oanda -->Empty response");
                            return Optional.empty();
                        }

                        JsonNode currCandle;
                        double  highSoFar=0;
                        double  lowSoFar=0;
                        double  volumeSoFar=0;
                        double lastTradePrice = 0;

                        int time = 0;
                        if (res.has("time")) {
                            time = res.get("time").asInt();
                        }

                        int openTime = 0;
                        double openPrice = 0;

                        if (res.has("candles")) {
                            currCandle = res.get("candles");


                            openPrice = -1;
                            if (currCandle.has("bid")) {

                                Iterator<JsonNode> it = currCandle.get("bid").elements();
                                out.println("bid: " + it.next().get("price").asDouble());
                                while (it.hasNext()) {
                                    JsonNode candle = it.next();
                                    out.println("Oanda JSON " + candle);
                                    Iterator<JsonNode> candleItr = it.next().elements();

                                    out.println("Oanda JSON " + candleItr);


                                    while (candleItr.hasNext()) {
                                        currCandle = candleItr.next();

                                        if (time < currentCandleStartedAt.getEpochSecond() ||
                                                time >= currentCandleStartedAt.getEpochSecond() +
                                                        secondsPerCandle) {
                                            out.println("currentTills " + time + " currentCandleStartedAt ");
                                            continue;
                                        } else {
                                            // FIXME: Why are we only using the first sub-candle here?
                                            lastTradePrice = currCandle.get(4).asDouble();
                                        }
                                        openPrice = currCandle.get(3).asDouble();

                                        if (currCandle.get(2).asDouble() > highSoFar) {
                                            highSoFar = currCandle.get(2).asDouble();
                                        }

                                        if (currCandle.get(1).asDouble() < lowSoFar) {
                                            lowSoFar = currCandle.get(1).asDouble();
                                        }

                                        volumeSoFar += currCandle.get(5).asDouble();

                                        out.println("Test me "  + " " + currCandle);


                                    }


                                }
                            }


                            openTime = (int) (currentCandleStartedAt.toEpochMilli() / 1000L);
                        }
                        int currentTill = 0;
                        return Optional.of(new InProgressCandleData(openTime, openPrice, highSoFar, lowSoFar,
                                currentTill, lastTradePrice, volumeSoFar));


                    });
        }


    @Override
     public void onOpen(@NotNull ServerHandshake handshake) {

        Log.info("OANDA connection opened",handshake.toString());

     }

     @Override
     public void onMessage(String message) {

        Log.info("OANDA message received",message);

     }

     @Override
     public void onClose(int code, String reason, boolean remote) {
        Log.info("OANDA connection closed",code+" "+ reason+" "+remote);

     }

     @Override
     public void onError(Exception ex) {
        Log.error("OANDA connection error"+ex);

     }


//
//    PUT	/v3/accounts/{accountID}/orders/{orderSpecifier}
//    Replace an Order in an Account by simultaneously cancelling it and creating a replacement Order

    public OandaOrder getOrder(String orderSpecifier) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/orders/" + orderSpecifier;
        JSONObject payload = makeRequest("GET", path);
        return null;
    }
//
//    PUT	/v3/accounts/{accountID}/orders/{orderSpecifier}/cancel
//    Cancel a pending Order in an Account
//

    public OandaOrder cancelOrder(String orderSpecifier) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/orders/" + orderSpecifier;
        JSONObject payload = makeRequest("PUT", path);

        System.out.println("order " + payload);

        if (
                payload.getJSONObject("orders") != null) {

            for (int i = 0; i < payload.getJSONArray("orders").length(); i++) {
                OandaOrder oandaOrder = null;
                oandaOrder.setOrderId(payload.getJSONArray("orders").getJSONObject(i).get("orderId"));

            }
        }
        return null;
    }

//    PUT	/v3/accounts/{accountID}/orders/{orderSpecifier}/clientExtensions
//    Update the Client Extensions for an Order in an Account. Do not set, modify, or delete clientExtensions if your account is associated with MT4.

    public OandaOrder getClientExtensions(String orderSpecifier) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/orders/" + orderSpecifier + "/clientExtensions";
        JSONObject payload = makeRequest("PUT", path);

        System.out.println("order " + payload);

        if (
                payload.getJSONObject("orders") != null) {

            for (int i = 0; i < payload.getJSONArray("orders").length(); i++) {
                OandaOrder oandaOrder = null;
                oandaOrder.setOrderId(payload.getJSONArray("orders").getJSONObject(i).
                        get("orderId").toString());
            }
        }
        return null;
    }


//
//
//    Transaction Endpoints
//
//
//
//    GET	/v3/accounts/{accountID}/transactions
//    Get a list of Transactions pages that satisfy a time-based Transaction query.

    public OandaTransaction[] getTransactions(String query) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/transactions";
        JSONObject payload = makeRequest("GET", path);
        if (
                payload.getJSONObject("transactions") != null &&
                        payload.getJSONObject("transactions").getJSONObject("transactions") != null
        ) {

            for (int i = 0; i < payload.getJSONArray("transactions").length();
            ) {

                oandaTransaction = new OandaTransaction();
                oandaTransaction.setTransactionId(payload.getJSONArray("transactions").getJSONObject(i).
                        get("transactionId").toString());


            }

        }
        return new OandaTransaction[]{oandaTransaction};
    }
//
//            GET	/v3/accounts/{accountID}/transactions/{transactionID}
//    Get the details of a single Account Transaction.

    public OandaTransaction getTransaction(String transactionID) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/transactions/" + transactionID;
        JSONObject payload = makeRequest("GET", path);
        if (
                payload.getJSONObject("transaction") != null &&
                        payload.getJSONObject("transaction").getJSONObject("transaction") != null
        ) {

            for (int i = 0; i < payload.getJSONArray("transaction").length(); i++) {
                OandaTransaction oandaTransaction = new OandaTransaction();

            }

        }
        return null;
    }
//
//    GET	/v3/accounts/{accountID}/transactions/idrange
//    Get a range of Transactions for an Account based on the Transaction IDs.
//

    public OandaTransaction[] getTransactionRange(String query) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/transactions/idrange";
        JSONObject payload = makeRequest("GET", path);
        if (payload.getJSONObject("transactions") != null) {
            payload.getJSONObject("transactions").getJSONObject("transactions");
        }
        return null;
    }
    //            GET	/v3/accounts/{accountID}/transactions/sinceid
//    Get a range of Transactions for an Account starting at (but not including) a provided Transaction ID.
//
//
//    GET	/v3/accounts/{accountID}/transactions/stream
//    Get a stream of Transactions for an Account starting from when the request is made.

    public OandaTransaction[] getTransactionStream(String query) throws OandaException {
        String path = "/v3/accounts/" + accountID + "/transactions/stream";
        JSONObject payload = makeRequest("GET", path);
        if (
                payload.getJSONObject("transactions") != null &&
                        payload.getJSONObject("transactions").getJSONObject("transactions") != null) {

            for (int i = 0; i < payload.getJSONArray("transactions").length(); i++) {
                OandaTransaction oandaTransaction = null;
                oandaTransaction = new OandaTransaction();
                oandaTransaction.setTransactionId(payload.getJSONArray("transactions").getJSONObject(i).
                        get("transactionId").toString());
            }
        }
        return null;
    }

    public CandleDataSupplier getCandleDataSupplier(int i, String tradePair) {
        return new OandaCandleDataSupplier(i, tradePair);
    }



//            Note: This endpoint is served by the streaming URLs.

    public class OandaCandleDataSupplier extends CandleDataSupplier {
        public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        private static final int EARLIEST_DATA = 1422144000; // roughly the first trade

        public OandaCandleDataSupplier(int secondsPerCandle, String tradePair) {
            super(200, secondsPerCandle, tradePair, new SimpleIntegerProperty(-1));
        }

        @Override
        public Set<Integer> getSupportedGranularities() {
            // https://docs.pro.coinbase.com/#get-historic-rates
            return new TreeSet<>(Set.of(60, 300, 60 * 30, 900, 3600 * 4, 3600 * 2, 3600, 21600, 3600 * 24 * 7, 3600 * 12, 86400));
        }

        @Override
        public List<CandleData> getCandleData() {
            return null;
        }

        @Override
        public CompletableFuture<Optional<?>> fetchCandleDataForInProgressCandle(
                String tradePair, Instant currentCandleStartedAt, long secondsIntoCurrentCandle, int secondsPerCandle) {
//            String startDateString = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.ofInstant(
//                    currentCandleStartedAt, ZoneOffset.UTC));
            long idealGranularity = Math.max(10, secondsIntoCurrentCandle / 200);
            // Get the closest supported granularity to the ideal granularity.
//            int actualGranularity = getCandleDataSupplier(secondsPerCandle, tradePair).getSupportedGranularities().stream()
//                    .min(Comparator.comparingInt(i -> (int) Math.abs(i - idealGranularity)))
//                    .orElseThrow(() -> new NoSuchElementException("Supported granularities was empty!"));

            String x;
            String str;
            if (secondsPerCandle < 3600) {
                x = String.valueOf(secondsPerCandle / 60);
                str = "M";
            } else if (secondsPerCandle < 86400) {
                x = String.valueOf((secondsPerCandle / 3600));
                str = "H";
            } else if (secondsPerCandle < 604800) {
                x = "";//String.valueOf(secondsPerCandle / 86400);
                str = "D";
            } else if (secondsPerCandle < 2592000) {
                x = String.valueOf((secondsPerCandle / 604800));
                str = "W";
            } else {
                x = String.valueOf((secondsPerCandle * 7 / 2592000 / 7));
                str = "M";
            }


            String granularity = str + x;
            //   String uriStr = "https://api-fxtrade.oanda.com/v3/instruments/" + tradePair + "/candles?price=BA&from=2016-10-17T15%3A00%3A00.000000000Z&granularity=" + granularity;
            String uriStr = "https://api-fxtrade.oanda.com/v3/instruments/" + tradePair + "/candles?price=BA&from=2016-10-17T15%3A00%3A00.000000000Z&granularity=" + granularity;

            return HttpClient.newHttpClient().sendAsync(
                            HttpRequest.newBuilder()
                                    .uri(URI.create(uriStr)).GET().build(),
                            HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(response -> {
                        Log.info("Oanda response: ", response);
                        JsonNode res;
                        try {
                            res = OBJECT_MAPPER.readTree(response);
                        } catch (JsonProcessingException ex) {
                            throw new RuntimeException(ex);
                        }

                        if (res.isEmpty()) {
                            out.println("Oanda -->Empty response");
                            return Optional.empty();
                        }

                        JsonNode currCandle;
                        double  highSoFar=0;
                        double  lowSoFar=0;
                        double  volumeSoFar=0;
                        double lastTradePrice = 0;

                        int time = 0;
                        if (res.has("time")) {
                            String times = res.get("candles").get("time").toString();
                            time = (int) Instant.parse(times).getEpochSecond();
                        }
                        if (time +
                                secondsPerCandle > endTime.get()) {
                            ((ArrayNode) res).remove(0);
                        }
                        int openTime = 0;
                        double openPrice = 0;

                        if (res.has("candles")) {
                            currCandle = res.get("candles");
                            openPrice = -1;
                            if (currCandle.has("bid")) {

                                Iterator<JsonNode> it = currCandle.get("bid").elements();
                                out.println("bid " + it);
                                while (it.hasNext()) {
                                    JsonNode candle = it.next();
                                    out.println("Oanda JSON " + candle);
                                    Iterator<JsonNode> candleItr = it.next().elements();
                                    out.println("Oanda JSON " + candleItr);


                                    while (candleItr.hasNext()) {
                                        currCandle = candleItr.next();

                                        if (time < currentCandleStartedAt.getEpochSecond() ||
                                                time >= currentCandleStartedAt.getEpochSecond() +
                                                        secondsPerCandle) {
                                            out.println("currentTills " + time + " currentCandleStartedAt ");
                                            continue;
                                        } else {
                                                   lastTradePrice = currCandle.get(4).asDouble();
                                        }
                                        openPrice = currCandle.get(3).asDouble();

                                        if (currCandle.get(2).asDouble() > highSoFar) {
                                            highSoFar = currCandle.get(2).asDouble();
                                        }

                                        if (currCandle.get(1).asDouble() < lowSoFar) {
                                            lowSoFar = currCandle.get(1).asDouble();
                                        }

                                        volumeSoFar += currCandle.get(5).asDouble();

                                        out.println("Test me " + currCandle.get(0).toString() + " " + currCandle);
                                        out.println("Test me " + currCandle.get(2).toString() + " " + currCandle);

                                    }


                                }
                            }


                            openTime = (int) (currentCandleStartedAt.toEpochMilli() / 1000L);
                        }
                        int currentTill = (int) (currentCandleStartedAt.toEpochMilli() / 1000L) +
                                secondsPerCandle;
                        return Optional.of(new InProgressCandleData(openTime, openPrice, highSoFar, lowSoFar,
                                currentTill, lastTradePrice, volumeSoFar));


                    });
        }


        @Override
        public CompletableFuture<List<Trade>> fetchRecentTradesUntil(String tradePair, Instant stopAt) {
            Objects.requireNonNull(tradePair);
            Objects.requireNonNull(stopAt);

            if (stopAt.isAfter(Instant.now())) {
                return CompletableFuture.completedFuture(Collections.emptyList());
            }

            CompletableFuture<List<Trade>> futureResult = new CompletableFuture<>();

            // It is not easy to fetch trades concurrently because we need to get the "cb-after" header after each request.
            CompletableFuture.runAsync(() -> {
                IntegerProperty afterCursor = new SimpleIntegerProperty(-1);
                List<Trade> tradesBeforeStopTime = new ArrayList<>();
                for (int i = 0; !futureResult.isDone(); i++) {
                    String uriStr = "https://api-fxtrade.oanda.com/v3/account/"+accountID+"/trades?symbol=" + tradePair;

                    if (i != 0) {
                        uriStr += "?after=" + afterCursor.get();
                    }
                    try {
                        HttpRequest.Builder req = HttpRequest.newBuilder();
                        req.header("Accept", "application/json");
                        req.header("Content-Type", "application/json");
                        req.header("Authorization", api_key);
                        req.uri(URI.create(uriStr));

                        HttpResponse<String> response = HttpClient.newHttpClient().send(
                                req.build(),
                                HttpResponse.BodyHandlers.ofString());

                        Log.info("response headers: " , String.valueOf(response.headers()));
                        if (response.headers().firstValue("cb-after").isEmpty()) {
                            out.println("Oanda -->Empty response");
                            futureResult.completeExceptionally(new RuntimeException(
                                    "Oanda  trades response did not contain header \"cb-after\": " + response));

                        }

                        afterCursor.setValue(Integer.valueOf((response.headers().firstValue(" cb-after").get())));

                        JsonNode tradesResponse = OBJECT_MAPPER.readTree(response.body());
                        if (!tradesResponse.isArray()) {
                            out.println("Oanda  trades response is not an array");
                            futureResult.completeExceptionally(new RuntimeException(
                                    "Trades response was not an array!"));
                        }
                        if (tradesResponse.isEmpty()) {
                            out.println("Trades response was empty");
                            futureResult.completeExceptionally(new IllegalArgumentException("tradesResponse was empty"));
                        } else {


                            for (JsonNode j : tradesResponse) {

                                Instant time = Instant.from(ISO_INSTANT.parse(j.get("time").toString()));
                                out.println("time " + time);
                                if (time.compareTo(stopAt) <= 0) {
                                    futureResult.complete(tradesBeforeStopTime);

                                    break;
                                } else {
                                    tradesBeforeStopTime.add(new Trade(tradePair,
                                            DefaultMoney.ofFiat(j.get("price").toString(), tradePair.substring(3, tradePair.length() - 1)),
                                            DefaultMoney.ofCrypto(j.get("qty").toString(), tradePair.substring(0, 3)),
                                            Side.getSide(j.get("isBuyerMaker").toString()), j.get("id").asLong(), time));

                               out.println("tradesBeforeStopTime " + tradesBeforeStopTime);
                                }
                            }
                        }
                    } catch (IOException | InterruptedException ex) {
                        Log.error("ex: " + ex);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            return futureResult;
        }

        @Override
        public Future<List<CandleData>> get() {
            if (endTime.get() == -1) {
                endTime.set((int) (Instant.now().toEpochMilli() / 1000L));
            }

            String endDateString = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    .format(LocalDateTime.ofEpochSecond(endTime.get(), 0, ZoneOffset.UTC));
            int startTime = Math.max(endTime.get() - (numCandles * secondsPerCandle), EARLIEST_DATA);
            String startDateString = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    .format(LocalDateTime.ofEpochSecond(startTime, 0, ZoneOffset.UTC));

//
            Log.info("Start date: " + startDateString, news.toString())
            ;//
//                ;
            Log.info("End date: " + endDateString, news.toString());

            Log.info("TradePair " + String.valueOf(tradePair
            ).replace("/", ""), news.toString());
            Log.info("Second per Candle: " + secondsPerCandle, news.toString());
            String x, str ;

            Log.info("Start date: " + startDateString, news.toString());

            Log.info("End date: " + endDateString, news.toString());


            Log.info("Second per Candle: " + secondsPerCandle, news.toString());

            if (secondsPerCandle < 3600) {
                x = String.valueOf(secondsPerCandle / 60);
                str = "M";
            } else if (secondsPerCandle < 86400) {
                x = String.valueOf((secondsPerCandle / 3600));
                str = "H";
            } else if (secondsPerCandle < 604800) {
                x = "";//String.valueOf(secondsPerCandle / 86400);
                str = "D";
            } else if (secondsPerCandle < 2592000) {
                x = String.valueOf((secondsPerCandle / 604800));
                str = "W";
            } else {
                x = String.valueOf((secondsPerCandle * 7 / 2592000 / 7));
                str = "M";
            }


            String granularity = str + x;
            String uriStr = "https://api-fxtrade.oanda.com/v3/instruments/" + tradePair + "/candles?price=BA&from=2016-10-17T15%3A00%3A00.000000000Z&granularity=" + granularity;
//
//
            out.println("timeframe: " + granularity);


            if (startTime == EARLIEST_DATA) {
                // signal more data is false

                out.println("Starting Time: " + startTime + " is false");
                return CompletableFuture.completedFuture(Collections.emptyList());
            }


            HttpRequest.Builder req = HttpRequest.newBuilder();
            req.uri(URI.create(uriStr));

            req.header("Authorization", "Bearer " + api_key);
            return HttpClient.newHttpClient().sendAsync(
                            req.build(),
                            HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(response -> {
                        Log.info("Oanda us Response: " + response, news.toString());
                        JsonNode res;
                        try {
                            res = OBJECT_MAPPER.readTree(response);
                        } catch (JsonProcessingException ex) {
                            throw new RuntimeException(ex);
                        }
                        int time;

                            // Remove the current in-progress candle

                            if (res.has("candles")) {


                                time = Instant.from(ISO_INSTANT.parse(res.get("candles").get("time").toString())).getNano();

                                if (time +
                                        secondsPerCandle > endTime.get()) {
                                    ((ArrayNode) res).remove(0);
                                }
                                ArrayList<CandleData> candleData = new ArrayList<>();

                                for (JsonNode candle : res) {
                                    out.println("Oanda JSON " + candle);

                                    //        JSON [1632614400000,"42695.8400","43957.8200","40192.1600","43216.3600","1119.97070800",1632700799999,"47701882.7039",50948,"514.17724000","21953536.9128","0"]

                                    candleData.add(new CandleData(candle.get("o").asDouble(),
                                          candle.get("c").asDouble(),  // close price
                                           candle.get("h").asDouble(),  // high price
                                             candle.get("l").asDouble(),  // low price
                                            time,     // open time
                                            candle.get("volume").asDouble())   // volume
                                    );
                                    endTime.set(time);
                                    Log.info("Candle D" , String.valueOf(candleData));
                                }
                                candleData.sort(Comparator.comparingInt(CandleData::getOpenTime));
                                return candleData;
                            }else{
                                out.println("No candles " + res);
                            }
                            return Collections.emptyList();



                    });
        }




    }




}