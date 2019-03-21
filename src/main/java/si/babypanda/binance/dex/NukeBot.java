package si.babypanda.binance.dex;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;
import com.binance.dex.api.client.domain.Balance;
import com.binance.dex.api.client.domain.OrderBook;
import com.binance.dex.api.client.domain.OrderBookEntry;

public class NukeBot {
    
    private static final Logger log = LoggerFactory.getLogger(NukeBot.class);
    
    private static final String bnbCoin = "BNB";
    
    //private static double buyPrice = 8;
    //private static double sellPrice = 9.5;
    //private static double buyAmount = 40;
    //private static double budget = buyPrice * buyAmount + 5;
    
    private static int walletIndex = 30;

    private static final Map<String, NukeParameter> map = new HashMap<String, NukeParameter>() {
        private static final long serialVersionUID = 1L;
        {
            //put("80DASHOU-729", new NukeParameter(8.5, 9.4, 30));
            //put("81JIAN-3E8", new NukeParameter(8.5, 9.4, 30));
            //put("82XIYOU-34D", new NukeParameter(4, 4.5, 60));
            //put("83SHUIHU-AC4", new NukeParameter(8.5, 9.4, 30));
            //put("84SHEDAO-F6F", new NukeParameter(4.5, 4.99, 60));
            //put("85DUC-800", new NukeParameter(7.51, 7.9, 40));
            //put("86OK-B90", new NukeParameter(8.1, 8.49, 35));
            //put("87KEYI-248", new NukeParameter(7.31, 7.49, 40));
            //put("8888-E6D", new NukeParameter(6.51, 6.99, 40));
            //put("8989-4DC", new NukeParameter(6.51, 6.99, 40));
            //put("SEPT-137", new NukeParameter(11.3, 12.5, 25));
            //put("HEIMA5-F97", new NukeParameter(11.3, 12.5, 25));
            //put("LLL-915", new NukeParameter(8.81, 8.99, 30));
            //put("IAA-C81", new NukeParameter(10, 11, 15));
            //put("IBB-8DE", new NukeParameter(10, 11, 15));
            //put("ICC-6EF", new NukeParameter(10, 11, 15));
            //put("IDD-00E", new NukeParameter(10, 11, 15));
            //put("IDD-516", new NukeParameter(10, 11, 15));
            //put("IEE-DCA", new NukeParameter(10, 11, 15));
            //put("IFF-804", new NukeParameter(10, 11, 15));
            //put("IGG-013", new NukeParameter(10, 11, 15));
            //put("IHH-D4E", new NukeParameter(10, 11, 15));
            //put("III-25C", new NukeParameter(10, 11, 15));
            //put("IJJ-65E", new NukeParameter(10, 11, 15));
            //put("ILIS-B27", new NukeParameter(10, 11, 15));
            //put("ILL-88F", new NukeParameter(10, 11, 15));
            //put("ILIS-B27", new NukeParameter(10, 11, 15));
            //put("ILL-88F", new NukeParameter(10, 11, 15));
            put("XXX77-EDC", new NukeParameter(6, 14, 80, 0));
            put("YYY78-BCD", new NukeParameter(6, 14, 80, 0));
        }
    };
    
    public static void main(String[] args) throws InterruptedException {
        log.info("NukeBot v0.0.1");
        
        WalletManager walletManager = new WalletManager();
        int b = 10;
        while (true) {
            //BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
            int i = 0;
            double sumBNB = 0;
            double sumBalance = 0;
            for (String coin : map.keySet()) {
                int index = walletIndex + i;
                Wallet w = walletManager.create(index);
                try {
                    //Time time = client.getTime();
                    //List<Candlestick> candleStickBars = client.getCandleStickBars(coin + "_BNB", CandlestickInterval.FIVE_MINUTES, 20, time.getApTime().minusHours(1).getMillis(), time.getApTime().getMillis());
                    //log.info("candleStickBars: {}", candleStickBars);
                    double budget = 0;
                    
                    if (map.get(coin).getType() == 0) {
                        NukeStatistic ns = nuke(coin, w);
                        budget = map.get(coin).getBudget();
                        sumBNB += ns.getBnb();
                        sumBalance += ns.getBalance();
                    } else if (map.get(coin).getType() == 1) {
                        double trade = 10;
                        nukeTrade(coin, w, trade);
                        budget = 15 * trade;
                    }
                    
                    if (b == 0) {
                        balance(coin, w, budget);
                    }
                } catch (RuntimeException ex) {
                    log.error("RuntimeException ex: {}", ex.getMessage(), ex);
                    Thread.sleep(2000);
                }
                i++;
                Thread.sleep(1000);
            }
            if (b == 0) {
                b = 10;
            }
            b--;
            log.info("### SUM sumBNB: {} sumBalance: {} ###", sumBNB, sumBalance);
        }
    }
    
    private static Balance getBalance(Account account, String coin) {
        if (account.getBalances() == null || account.getBalances().isEmpty()) {
            return null;
        }
        for (Balance balance : account.getBalances()) {
            if (coin.equals(balance.getSymbol())) {
                return balance;
            }
        }
        return null;
    }
    
    private static NukeStatistic nuke(String coin, Wallet w) throws InterruptedException {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        Account account = client.getAccount(w.getAddress());
        Balance bnb = getBalance(account, bnbCoin);
        Balance balance = getBalance(account, coin);
        log.info("### {}: {} BNB: {} ", coin, balance, bnb);
        if (bnb == null) {
            log.error("bnb NULL");
            Thread.sleep(1000);
            return new NukeStatistic(0, 0);
        }
        // BUY ORDER [main] INFO si.babypanda.binance.dex.NukeBot - BNB:
        // Balance[symbol=BNB,free=0.98435004,locked=0.01439996,frozen=0.00000000]
        // 83SHUIHU-AC4: null
        // [main] INFO si.babypanda.binance.dex.NukeBot - BNB:
        // Balance[symbol=BNB,free=0.96944413,locked=0.01439996,frozen=0.00000000]
        // 83SHUIHU-AC4:
        // Balance[symbol=83SHUIHU-AC4,free=0.00100000,locked=0.00000000,frozen=0.00000000]
        // SELL [main] INFO si.babypanda.binance.dex.NukeBot - BNB:
        // Balance[symbol=BNB,free=0.96944413,locked=0.01439996,frozen=0.00000000]
        // 83SHUIHU-AC4:
        // Balance[symbol=83SHUIHU-AC4,free=0.00000000,locked=0.00100000,frozen=0.00000000]
        
        if (balance != null) {
            double balanceFree = Math.floor(100 * Double.parseDouble(balance.getFree())) / (double)100;
            if (balanceFree > 0.01) {
                TradeManager tradeManager = new TradeManager(coin, w);
                log.info("sellPrice: {} balanceFree: {}", map.get(coin).getSellPrice(), balanceFree);
                tradeManager.sell(map.get(coin).getSellPrice(), balanceFree);
                Thread.sleep(200);
            } else {
                log.info("no balanceFree");
            }
        }
        
        double bnbLocked = Double.parseDouble(bnb.getLocked());
        if (map.get(coin).getBuyAmount() * map.get(coin).getBuyPrice() - bnbLocked < 0.01) {
            log.info("bnbLocked");
        } else {
            boolean locked = false;
            if (balance != null) {
                double balanceLocked = Double.parseDouble(balance.getLocked());
                if (balanceLocked >= map.get(coin).getBuyAmount()) {
                    log.info("balanceLocked");
                    locked = true;
                }
            }
            if (!locked) {
                TradeManager tradeManager = new TradeManager(coin, w);
                double delta = map.get(coin).getBuyAmount() * map.get(coin).getBuyPrice() - bnbLocked;
                double amount = Math.floor(100 * delta / map.get(coin).getBuyPrice()) / (double)100;
                double free = Math.floor(100 * Double.parseDouble(bnb.getFree()) / map.get(coin).getBuyPrice()) / (double)100;
                amount = Math.min(amount, free);
                log.info("buyPrice: {} amount: {}", map.get(coin).getBuyPrice(), amount);
                if (amount > 0.001) {
                    tradeManager.buy(map.get(coin).getBuyPrice(), amount);
                }
            }
        }
        return new NukeStatistic(
                bnb != null ? Double.parseDouble(bnb.getFree()) + Double.parseDouble(bnb.getLocked()) : 0,
                balance != null ? Double.parseDouble(balance.getFree()) + Double.parseDouble(balance.getLocked()) : 0);
    }
    
    private static void balance(String coin, Wallet w, double budget) throws InterruptedException {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        WalletManager walletManager = new WalletManager();
        Wallet target = walletManager.init(17);
        Account account = client.getAccount(w.getAddress());
        Thread.sleep(100);
        Balance bnb = getBalance(account, bnbCoin);
        log.info("BNB: {}", bnb.getFree());
        double free = Double.parseDouble(bnb.getFree());
        if (free > budget) {
            TradeManager tradeManager = new TradeManager("BNB", w);
            tradeManager.transferBNB(target.getAddress(), free - budget);
            Thread.sleep(100);
        }
    }
    
    private static void nukeTrade(String coin, Wallet wallet, double amount) throws InterruptedException {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        OrderBook orderBook;
        String symbol = coin + "_BNB";
        try {
            orderBook = client.getOrderBook(symbol, 5);
            Thread.sleep(100);
        } catch (RuntimeException ex) {
            log.info("RuntimeException ex: {}", ex.getMessage());
            Thread.sleep(500);
            return;
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
                log.info("coin: {} ask: {} bid: {} delta: {}", coin, ask.getPrice(), bid.getPrice(), delta);
                TradeManager tradeManager = new TradeManager(coin, wallet);
                if (delta > 0.12) {
                    low = bidPrice + 0.01;
                    high = askPrice - 0.1;
                } else {
                    low = bidPrice + 0.01;
                    high = askPrice - 0.01;
                }
                //high = bidPrice - 0.49;
                if (bidPrice < 15) {
                    if (delta > 1.4) {
                        low += 0.5;
                        high -= 0.5;
                    }
                    tradeManager.buy(low, amount);
                    Thread.sleep(100);
                    tradeManager.sell(high, amount);
                    Thread.sleep(100);
                } else {
                    log.info("bid price to large");
                }
            }
        } else if (ask == null && bid != null) {
            log.info("coin: {} ask == null", coin);
        } else if (ask != null && bid == null) {
            log.info("coin: {} bid == null", coin);
        } else {
            log.info("coin: {} ask == null && bid == null", coin);
        }
    }
}
