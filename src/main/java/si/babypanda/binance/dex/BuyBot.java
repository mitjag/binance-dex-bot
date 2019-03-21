package si.babypanda.binance.dex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;

public class BuyBot {
    
    private static final Logger log = LoggerFactory.getLogger(BuyBot.class);
    
    public static void main(String[] args) throws InterruptedException {
        log.info("BuyBot v0.0.1");
       
        String coin = "PND-943";
        if (args.length >= 1) {
            coin = args[0];
            log.info("coin: {}", coin);
        }
        
        WalletManager walletManager = new WalletManager();
        
        boolean balance = false;
        boolean buy = true;
        
        double amount = 0.001;
        double price = 0.3;
        for (int i = 25000; i < 37500; i++) {
            log.info("i: {}", i);
            int offset = 12500;
            Wallet c1 = walletManager.create(i);
            Wallet c2 = walletManager.create(offset + i);
            TradeManager tradeManager1 = new TradeManager(coin, c1);
            TradeManager tradeManager2 = new TradeManager(coin, c2);
            
            if (balance) {
                Account accountC1 = walletManager.account(c1);
                Thread.sleep(100);
                Account accountC2 = walletManager.account(c2);
                Thread.sleep(100);
                log.info("accountC1: {}", accountC1);
                log.info("accountC2: {}", accountC2);
            }
            
            if (buy) {
                tradeManager1.sell(price, amount);
                Thread.sleep(400);
                tradeManager2.buy(price, amount);
                Thread.sleep(600);
            }
        }
    }
}
