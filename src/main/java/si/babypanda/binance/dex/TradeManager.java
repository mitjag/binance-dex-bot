package si.babypanda.binance.dex;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiException;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.OrderSide;
import com.binance.dex.api.client.domain.OrderType;
import com.binance.dex.api.client.domain.TimeInForce;
import com.binance.dex.api.client.domain.TransactionMetadata;
import com.binance.dex.api.client.domain.broadcast.NewOrder;
import com.binance.dex.api.client.domain.broadcast.TransactionOption;
import com.binance.dex.api.client.domain.broadcast.Transfer;

public class TradeManager {
    
    private static final Logger log = LoggerFactory.getLogger(TradeManager.class);
    
    private String coin;
    private String symbol;
    private Wallet wallet;
    private DecimalFormat decimalFormat;
    
    public TradeManager(String coin, Wallet wallet) {
        this.coin = coin;
        this.symbol = coin + "_BNB";
        this.wallet = wallet;
        
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        decimalFormat = new DecimalFormat("0.00000000", otherSymbols);
        decimalFormat.setGroupingUsed(false);
    }
    
    public void buy(double price, double quantity) {
        buy(decimalFormat.format(price), decimalFormat.format(quantity));
    }
    
    public void buy(String price, String quantity) {
        try {
            trade(OrderSide.BUY, price, quantity);
        } catch (NoSuchAlgorithmException | IOException ex) {
            log.error("buy ex: {}", ex.getMessage(), ex);
        }
    }
    
    public void sell(double price, double quantity) {
        // sell(String.format("%.8f", price), String.format("%.3f", quantity));
        sell(decimalFormat.format(price), decimalFormat.format(quantity));
    }
    
    public void sell(String price, String quantity) {
        try {
            trade(OrderSide.SELL, price, quantity);
        } catch (NoSuchAlgorithmException | IOException ex) {
            log.error("sell ex: {}", ex.getMessage(), ex);
        }
    }
    
    private void trade(OrderSide side, String price, String quantity) throws NoSuchAlgorithmException, IOException {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        
        NewOrder no = new NewOrder();
        no.setTimeInForce(TimeInForce.GTE);
        no.setOrderType(OrderType.LIMIT);
        no.setSide(side);
        no.setPrice(price);
        no.setQuantity(quantity);
        no.setSymbol(symbol);
        
        TransactionOption options = TransactionOption.DEFAULT_INSTANCE;
        try {
            List<TransactionMetadata> resp = client.newOrder(no, wallet, options, true);
            log.info("trade side: {} price: {} quantity: {} resp: {}", side, price, quantity, resp.get(0));
        } catch (BinanceDexApiException ex) {
            log.error("trade BinanceDexApiException ex: {}", ex.getMessage(), ex);
        }
    }
    
    public void transferCoin(String toAddress, double amount) {
        try {
            transfer(wallet.getAddress(), toAddress, coin, decimalFormat.format(amount));
        } catch (NoSuchAlgorithmException | IOException ex) {
            log.error("transferCoin ex: {}", ex.getMessage(), ex);
        }
    }
    
    public void transferBNB(String toAddress, double amount) {
        try {
            transfer(wallet.getAddress(), toAddress, "BNB", decimalFormat.format(amount));
        } catch (NoSuchAlgorithmException | IOException ex) {
            log.error("transferBNB ex: {}", ex.getMessage(), ex);
        }
    }
    
    private void transfer(String fromAddress, String toAddress, String coin, String amount) throws NoSuchAlgorithmException, IOException {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        
        Transfer transfer = new Transfer();
        transfer.setFromAddress(fromAddress);
        transfer.setToAddress(toAddress);
        transfer.setCoin(coin);
        transfer.setAmount(amount);
        
        TransactionOption options = TransactionOption.DEFAULT_INSTANCE;
        int retry = 8;
        while (retry != 0) {
            try {
                List<TransactionMetadata> resp = client.transfer(transfer, wallet, options, true);
                log.info("transfer fromAddress: {} toAddress: {} coin: {} amount: {} resp: {}", fromAddress, toAddress, coin, amount, resp.get(0));
                break;
            } catch (BinanceDexApiException ex) {
                log.error("transfer retry: {} BinanceDexApiException ex: {}", retry, ex.getMessage(), ex);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                retry--;
            }
        }
    }
}
