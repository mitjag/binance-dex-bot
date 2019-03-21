package si.babypanda.binance.dex;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.domain.Market;
import com.binance.dex.api.client.domain.OrderBook;
import com.binance.dex.api.client.domain.Time;
import com.binance.dex.api.client.domain.Trade;
import com.binance.dex.api.client.domain.TradePage;
import com.binance.dex.api.client.domain.request.TradesRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

public class Analyze {
    
    private static final Logger log = LoggerFactory.getLogger(Analyze.class);
    
    public static void main(String[] args) throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        log.info("Analyze v0.0.1");
        
        List<Statistic> activeMarkets = activeMarkets();
        ObjectMapper om = new ObjectMapper();
        try {
            String json = om.writeValueAsString(activeMarkets);
            log.info("{}", json);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = "activeMarkets_" + sdf.format(new Date()) + ".json";
            Files.write(json.getBytes(), new File(filename));
        } catch (IOException ex) {
            log.error("JsonProcessingException ex: {}", ex.getMessage(), ex);
        }
    }
    
    public static List<Statistic> activeMarkets() throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        Time time = client.getTime();
        
        /*
         * List<Token> tokens = client.getTokens(1000);
         * log.info("tokens.size: {}", tokens.size());
         * //client.getMarkets(1000);
         * for (Token token : tokens) {
         * //System.out.println("###########################################");
         * log.info("name: {} symbol: {}", token.getName(), token.getSymbol());
         * //OrderBook orderBook = client.getOrderBook(token.getSymbol() + "_BNB", 5);
         * //System.out.println(orderBook);
         * }
         */
        
        List<Statistic> activeMarkets = new ArrayList<>();
        List<Market> markets = client.getMarkets(1000);
        //ObjectMapper om = new ObjectMapper();
        //List<Market> markets = om.readValue(new File("markets.json"), new TypeReference<List<Market>>() {});
        log.info("markets.size: {}", markets.size());
        Thread.sleep(100);
        for (Market market : markets) {
            try {
                if (!market.getQuoteAssetSymbol().equals("BNB")) {
                    continue;
                }
                log.info("### {} ###", market.getBaseAssetSymbol());
                log.info("quoteAssetSymbol: {} baseAssetSymbol: {} price: {} tick: {} lot: {}", market.getQuoteAssetSymbol(), market.getBaseAssetSymbol(),
                        market.getPrice(), market.getTickSize(), market.getLotSize());
                String symbol = market.getBaseAssetSymbol() + "_BNB";
                OrderBook orderBook = client.getOrderBook(symbol, 5);
                // OrderBook orderBook = client.getOrderBook("PND-943_BNB", 5);
                Thread.sleep(50);
                
                TradesRequest tradesRequest = new TradesRequest();
                // tradesRequest.setQuoteAsset("BNB");
                tradesRequest.setStart(time.getApTime().getMillis() - 10 * 60 * 1000); // minutes
                tradesRequest.setSymbol(symbol);
                TradePage trades = client.getTrades(tradesRequest);
                // double max = trades.getTrade().stream().max(Comparator.comparing(t -> {
                // Double.parseDouble(t.getPrice()); } ));
                if (!trades.getTrade().isEmpty()) {
                    Optional<Trade> max = trades.getTrade().stream().max(Comparator.comparing(
                            Trade::getPrice, (p1, p2) -> {
                                return Double.compare(Double.parseDouble(p1), Double.parseDouble(p2));
                            }));
                    Optional<Trade> min = trades.getTrade().stream().min(Comparator.comparing(
                            Trade::getPrice, (p1, p2) -> {
                                return Double.compare(Double.parseDouble(p1), Double.parseDouble(p2));
                            }));
                    double delta = Double.parseDouble(max.get().getPrice()) - Double.parseDouble(min.get().getPrice());
                    log.info("{}", orderBook);
                    log.info("trades.trade.size: {} max: {} min: {} delta: {} ", trades.getTrade().size(), max.get().getPrice(), min.get().getPrice(), delta);
                    if (Double.parseDouble(min.get().getPrice()) <= 15) {
                        activeMarkets.add(new Statistic(symbol, trades.getTrade().size(), max.get().getPrice(), min.get().getPrice(), delta));
                    }
                } else {
                    log.info("trades.trade.size: {}", trades.getTrade().size());
                }
                /*
                 * for (Trade trade : trades.getTrade()) {
                 * log.info("trade quoteAsset: {} symbol: {} price: {} quantity: {} time: {}",
                 * trade.getQuoteAsset(), trade.getSymbol(), trade.getPrice(),
                 * trade.getQuantity(), trade.getTime());
                 * }
                 */
            } catch (RuntimeException ex) {
                log.error("RuntimeException ex: {}", ex.getMessage());
            }
            Thread.sleep(50);
        }
        activeMarkets.forEach(a -> log.info("symbol: {} tradeSize: {} maxPrice: {} minPrice: {} delta: {}", a.getSymbol(), a.getTradeSize(), a.getMaxPrice(),
                a.getMinPrice(), a.getDelta()));
        return activeMarkets;
    }
}
