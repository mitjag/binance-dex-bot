package si.babypanda.binance.dex;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.OrderBook;
import com.binance.dex.api.client.domain.OrderBookEntry;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class WarBot {
    
    private static final Logger log = LoggerFactory.getLogger(WarBot.class);
    
    private static List<String> excludeList = Arrays.asList("BMB-6AC_BNB", "PND-943_BNB", "SCM-CDF_BNB", "PRT-D95_BNB", "PRT-E98_BNB");
    private static List<String> poiList = Arrays.asList(
            "XXX77-EDC_BNB", "YYY78-BCD_BNB", "OCB-B95_BNB", "IHH-D4E_BNB", "IFF-804_BNB", "III-25C_BNB",
            "ZZZ79-3C4_BNB", "IGG-013_BNB", "IDD-516_BNB", "ICC-6EF_BNB", "IJJ-65E_BNB",
            "IEE-DCA_BNB", "IAA-C81_BNB", "IBB-8D3_BNB",
            "RC1-943_BNB", "MC1-3A8_BNB", "TC1-A29_BNB", "NC1-279_BNB", "LC1-69B_BNB",
            "XSF-B03");
    
    public static void main(String[] args) throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        log.info("WarBot v0.0.1");
        
        double amount = 0.2;
        double poiAmount = 30;
        
        while (true) {
            List<Statistic> activeMarkets;
            if (args.length >= 1) {
                amount = Double.parseDouble(args[0]);
                log.info("amount: {}", amount);
            }
            if (args.length >= 2) {
                poiAmount = Double.parseDouble(args[1]);
                log.info("poiAmount: {}", poiAmount);
            }
            activeMarkets = Analyze.activeMarkets();
            Thread.sleep(5000);
            WalletManager walletManager = new WalletManager();
            Wallet w2 = walletManager.init(2);
            
            BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
            for (int i = 0; i < 10; i++) {
                for (Statistic activeMarket : activeMarkets) {
                    boolean exclude = excludeList.contains(activeMarket.getSymbol());
                    // log.info("symbol: {} exclude: {} tradeSize: {} maxPrice: {} minPrice: {}
                    // delta: {}", activeMarket.getSymbol(), exclude, activeMarket.getTradeSize(),
                    // activeMarket.getMaxPrice(), activeMarket.getMinPrice(),
                    // activeMarket.getDelta());
                    if (exclude) {
                        continue;
                    }
                    OrderBook orderBook;
                    try {
                        orderBook = client.getOrderBook(activeMarket.getSymbol(), 5);
                        Thread.sleep(100);
                    } catch (RuntimeException ex) {
                        log.info("RuntimeException ex: {}", ex.getMessage());
                        Thread.sleep(500);
                        continue;
                    }
                    
                    OrderBookEntry ask = null;
                    OrderBookEntry bid = null;
                    if (!orderBook.getAsks().isEmpty()) {
                        ask = orderBook.getAsks().get(0);
                    }
                    if (!orderBook.getBids().isEmpty()) {
                        bid = orderBook.getBids().get(0);
                    }
                    double low;
                    double high;
                    if (ask != null && bid != null) {
                        double askPrice = Double.parseDouble(ask.getPrice());
                        double bidPrice = Double.parseDouble(bid.getPrice());
                        double delta = askPrice - bidPrice;
                        if (delta > 0.05) {
                            log.info("symbol: {} exclude: {} tradeSize: {} maxPrice: {} minPrice: {} delta: {}", activeMarket.getSymbol(), exclude,
                                    activeMarket.getTradeSize(), activeMarket.getMaxPrice(), activeMarket.getMinPrice(), activeMarket.getDelta());
                            log.info("ask: {} bid: {} delta: {}", ask.getPrice(), bid.getPrice(), delta);
                            TradeManager tradeManager = new TradeManager(activeMarket.getSymbol().substring(0, activeMarket.getSymbol().length() - 4), w2);
                            if (delta > 0.12) {
                                low = bidPrice + 0.01;
                                high = askPrice - 0.1;
                            } else {
                                low = bidPrice + 0.01;
                                high = askPrice - 0.01;
                            }
                            //high = bidPrice - 0.49;
                            if (bidPrice < 15) {
                                if (delta > 2.4) {
                                    low += 1.1;
                                    high -= 1.1;
                                } else if (delta > 1.4) {
                                    low += 0.5;
                                    high -= 0.5;
                                }
                                if (high > low) {                                    
                                    boolean poi = poiList.contains(activeMarket.getSymbol());
                                    tradeManager.buy(low, poi ? poiAmount : amount);
                                    Thread.sleep(100);
                                    tradeManager.sell(high, poi ? poiAmount : amount);
                                    Thread.sleep(100);
                                }
                            } else {
                                log.info("bid price to large");
                            }
                        }
                    } else if (ask == null && bid != null) {
                        log.info("symbol: {} ask == null", activeMarket.getSymbol());
                    } else if (ask != null && bid == null) {
                        log.info("symbol: {} bid == null", activeMarket.getSymbol());
                    } else {
                        log.info("symbol: {} ask == null && bid == null", activeMarket.getSymbol());
                    }
                    Thread.sleep(100);
                }
                Thread.sleep(30000);
            }
            Thread.sleep(60000);
        }
    }
}
