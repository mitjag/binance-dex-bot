package si.babypanda.binance.dex;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.OrderBook;
import com.binance.dex.api.client.domain.OrderBookEntry;

public class Bot {
    
    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    
    // public static final String COIN = "PND-943";
    // public static final String SYMBOL = "PND-943_BNB";
    
    private static Random random;
    
    public static void main(String[] args) throws InterruptedException {
        log.info("DEX BOT v0.0.1");
        random = new Random(System.currentTimeMillis());
        
        WalletManager walletManager = new WalletManager();        
        String coin = "";
        double amount = 0.01;
        double price = 1.1;
        double increase = 0.1;
        double min = 1.2;
        double max = 1.4;
        long sleep = 30000;
        int parts = 5;
        double partsAmount = 0.001;
        boolean inc = true;
        
        if (args.length != 9) {
            log.info("args: coin price increase min max sleep parts partsAmount amount");
            return;
        }
        if (args.length >= 1) {
            coin = args[0];
            log.info("coin: {}", coin);
        }
        if (args.length >= 2) {
            price = Double.parseDouble(args[1]);
            log.info("price: {}", price);
        }
        if (args.length >= 3) {
            increase = Double.parseDouble(args[2]);
            log.info("increase: {}", increase);
        }
        if (args.length >= 4) {
            min = Double.parseDouble(args[3]);
            log.info("min: {}", min);
        }
        if (args.length >= 5) {
            max = Double.parseDouble(args[4]);
            log.info("max: {}", max);
        }
        if (args.length >= 6) {
            sleep = Long.parseLong(args[5]);
            log.info("sleep: {}", sleep);
        }
        if (args.length >= 7) {
            parts = Integer.parseInt(args[6]);
            //amount = 2 * parts * amount;
            log.info("parts: {}", parts);
        }
        if (args.length >= 8) {
            partsAmount = Double.parseDouble(args[7]);
            log.info("partsAmount: {}",partsAmount);
        }
        if (args.length >= 9) {
            amount = Double.parseDouble(args[8]);
            log.info("amount: {}",amount);
        }
        
        Wallet w1 = walletManager.init(1);
        Wallet w19 = walletManager.init(19);
        while (true) {
            TradeManager tradeManager1 = new TradeManager(coin, w1);
            TradeManager tradeManager19 = new TradeManager(coin, w19);
            log.info("loop price: {}", price);
            double randomAmount = amount + amount * random.nextInt(2);
            tradeManager19.sell(0.5, randomAmount);
            Thread.sleep(100);
            tradeManager19.buy(15, randomAmount);
            Thread.sleep(300);
            //fakeTrade(coin, tradeManager1, tradeManager19, parts, partsAmount);
            Thread.sleep(sleep - 100);
            // double randomIncrease = increase * (1 + random.nextInt(10) / (double)100);
            if (inc) {
                price += increase; // + randomIncrease;
            } else {
                price -= increase; // - randomIncrease;
            }
            if (price > max) {
                inc = false;
                log.info("price > max");
                price = max; // - randomIncrease;
            }
            if (price < min) {
                inc = true;
                log.info("price < min");
                price = min; // + randomIncrease;
            }
            /*
             * if (price > 15) {
             * price = 15 - randomIncrease;
             * } else if (price < 0.01) {
             * price = 0.01 + randomIncrease;
             * }
             */
        }
    }
    
    private static void fakeTrade(String coin, TradeManager sell, TradeManager buy, Integer partsBase, double partsAmount) throws InterruptedException {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        String symbol = coin + "_BNB";
        OrderBook orderBook;
        try {
            orderBook = client.getOrderBook(symbol, 5);
            Thread.sleep(100);
            
            OrderBookEntry ask = null;
            OrderBookEntry bid = null;
            if (!orderBook.getAsks().isEmpty()) {
                ask = orderBook.getAsks().get(0);
            }
            if (!orderBook.getBids().isEmpty()) {
                bid = orderBook.getBids().get(0);
            }
            if (ask != null && bid != null) {
                double askPrice = Double.parseDouble(ask.getPrice());
                double bidPrice = Double.parseDouble(bid.getPrice());
                if (askPrice <= 15 && bidPrice <= 15) {
                    double delta = askPrice - bidPrice;
                    if (delta > 0.049) {
                        int parts = partsBase + random.nextInt(2);
                        double deltaPart = Math.round(100 * delta / (double) parts) / (double) 100;
                        log.info("fake symbol: {} ask: {} bid: {} delta: {} parts: {} deltaPart: {}", symbol, ask.getPrice(), bid.getPrice(), delta, parts, deltaPart);
                        for (int i = 0; i < parts; i++) {
                            double price = bidPrice + i * deltaPart;
                            if (parts % 2 == 0) {
                                buy.buy(price, partsAmount);
                            } else {
                                sell.sell(price, partsAmount);
                            }
                            Thread.sleep(100);
                        }
                    }
                }
            }
        } catch (RuntimeException ex) {
            log.info("RuntimeException ex: {}", ex.getMessage());
            Thread.sleep(500);
        }
    }
}
