package si.babypanda.binance.dex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;

public class Transfer {
    
    private static final Logger log = LoggerFactory.getLogger(Transfer.class);
    
    public static void main(String[] args) throws InterruptedException {
        log.info("Transfer v0.0.1");
        
        // String coin = "PND-943_BNB";
        //String coin = "BEY-8C6";
        //String coin = "MEME-93C";
        //String coin = "ZEBRA-16D";
        //double amount = 229.1;
        //String coin = "NMSL-19D";
        //double amount = 229.1;
        //String coin = "000-EF6";
        //String coin = "OCB-B95";
        //String coin = "UCX-CC8";
        //String coin = "GCC-8F6";
        //double amount = 229.1;
        //String coin = "IHH-D4E";
        //double amount = 0.0944;
        //String coin = "IFF-804";
        //double amount = 0.0944;
        //String coin = "III-25C";
        //double amount = 0.0944;
        //String coin = "ZZZ79-3C4";
        //double amount = 0.2;
        //String coin = "IGG-013";
        //double amount = 0.0944;
        //String coin = "IDD-516";
        //double amount = 0.0944;
        String coin = "ICC-6EF";
        double amount = 0.0944;
        //IJJ-65E
        //IBB-8DE
        //IAA-C81
        //IEE-DCA
        if (args.length >= 2) {
            coin = args[0];
            amount = Double.parseDouble(args[1]);
        }
        log.info("coin: {} amount: {}", coin, amount);
        
        WalletManager walletManager = new WalletManager();
        Wallet w17 = walletManager.init(17);
        
        Account account = walletManager.account(w17);
        log.info("account: {}", account);
        Thread.sleep(100);
        
        boolean balance = false;
        boolean transfer = true;
        for (int i = 1; i < 60000; i++) {
            log.info("i: {}", i);
            Wallet c = walletManager.create(i);
            TradeManager tradeManager = new TradeManager(coin, c);
            if (balance) {
                Account accountC = walletManager.account(c);
                log.info("accountC: {}", accountC);
                Thread.sleep(150);
            }
            
            if (transfer) {
                tradeManager.transferCoin(w17.getAddress(), amount);
                Thread.sleep(250);
            }
        }
    }
}
