package si.babypanda.binance.dex;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;

public class AirDrop {
    
    private static final Logger log = LoggerFactory.getLogger(AirDrop.class);
    
    private static Random random;
    
    public static void main(String[] args) throws InterruptedException {
        log.info("AirDrop v0.0.1");
        random = new Random(System.currentTimeMillis());
        
        String coin = "PND-943";
        if (args.length >= 1) {
            coin = args[0];
            log.info("coin: {}", coin);
        }
        
        WalletManager walletManager = new WalletManager();
        Wallet w20 = walletManager.init(20);
        
        Account account = walletManager.account(w20);
        log.info("account: {}", account);
        Thread.sleep(100);
        
        // PND 50-100
        // BNB 0.02
                
        TradeManager tradeManager = new TradeManager(coin, w20);
        boolean balance = false;
        boolean transfer = true;
        for (int i = 1; i < 60000; i++) {
            log.info("i: {}", i);
            Wallet c = walletManager.create(i);
            if (balance) {
                Account accountC = walletManager.account(c);
                log.info("accountC: {}", accountC);
                Thread.sleep(100);
            }
            
            if (transfer) {
                tradeManager.transferCoin(c.getAddress(), 50 + random.nextInt(50));
                Thread.sleep(100);
                tradeManager.transferBNB(c.getAddress(), 0.02);
                Thread.sleep(100);
            }
        }
    }
}
