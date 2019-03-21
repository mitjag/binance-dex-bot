package si.babypanda.binance.dex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.BinanceDexApiClientGenerator;

public class AnalyzeTransaction {
    
    private static final Logger log = LoggerFactory.getLogger(AnalyzeTransaction.class);
    
    public static void main(String[] args) throws InterruptedException {
        log.info("AnalyzeTransaction v0.0.1");
        
        /*BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        TransactionsRequest transactionsRequest = new TransactionsRequest();
        
        //transactionsRequest.setAddress("");
        //transactionsRequest.setBlockHeight(1783727L);
        //transactionsRequest.setEndTime(endTime);
        //transactionsRequest.setLimit(1000);
        //transactionsRequest.setOffset(0);
        //transactionsRequest.setSide(side);
        //transactionsRequest.setStartTime(startTime);
        transactionsRequest.setTxAsset("III-25C");
        //transactionsRequest.setTxType(txType);
        
        Long maxBlockHeight = 3041611L;
        
        TransactionPage transactions = client.getTransactions(transactionsRequest);
        Thread.sleep(1000);
        log.info("total: {}", transactions.getTotal());
        for (Transaction transaction : transactions.getTx()) {
            log.info("txType: {}", transaction.getTxType());
        }*/
                

        //https://testnet-explorer.binance.org/api/v1/txs?page=-1&rows=10&txAsset=III-25C
        
        BinanceExplorerApi binanceExplorerApi = BinanceDexApiClientGenerator.createService(BinanceExplorerApi.class, "https://testnet-explorer.binance.org/");

        long countNewOrder = 0;
        long count = 0;
        long txNums = 1;
        int page = 1;

        while (count < txNums) {
            TransactionPage transactionPage = getTransactions(binanceExplorerApi, "III-25C", page, 100);
            //log.info("total: {}", transactionPage.getTxNums());
            Thread.sleep(500);
            txNums = transactionPage.getTxNums();
            for (Transaction transaction : transactionPage.getTxArray()) {
                if (transaction.getTxType().equals("NEW_ORDER")) {
                    countNewOrder ++;
                }
                //log.info("txType: {}", transaction.getTxType());
                count++;
            }
            page++;
            log.info("countNewOrder: {} count: {} txNums: {} txArray.size: {}", countNewOrder, count, txNums, transactionPage.getTxArray().size());
        }
    }
    
    
    private static TransactionPage getTransactions(BinanceExplorerApi binanceExplorerApi, String txAsset, Integer page, Integer rows) {
        return BinanceDexApiClientGenerator.executeSync(binanceExplorerApi.getTxs(txAsset, page, rows));
    }
}
