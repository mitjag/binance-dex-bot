package si.babypanda.binance.dex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.domain.Candlestick;
import com.binance.dex.api.client.domain.CandlestickInterval;
import com.binance.dex.api.client.domain.Market;
import com.binance.dex.api.client.domain.Time;
import com.binance.dex.api.client.domain.TradePage;
import com.binance.dex.api.client.domain.request.TradesRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnalyzeCandle {
    
    private static final Logger log = LoggerFactory.getLogger(AnalyzeCandle.class);
    
    private static List<CandleStatistic> statistics = new ArrayList<>();
    
    public static void main(String[] args) throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        log.info("AnalyzeCandle v0.0.1");
        
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        Time time = client.getTime();
        
        List<String> coins = new ArrayList<String>();
        //List<String> coins = Arrays.asList("PND-943");
        //List<String> coins = Arrays.asList("IHH-D4E", "IFF-804", "III-25C", "PND-943", "NMSL-19D", "IGG-013", "ICC-6EF", "GCC-8F6", "ZEBRA-16D", "IDD-516");
        
        if (coins == null || coins.isEmpty()) {
            List<Market> markets = client.getMarkets(1000);
            for (Market market : markets) {
                if (!market.getQuoteAssetSymbol().equals("BNB")) {
                    continue;
                }
                String symbol = market.getBaseAssetSymbol() + "_BNB";
                                
                TradesRequest tradesRequest = new TradesRequest();
                // tradesRequest.setQuoteAsset("BNB");
                tradesRequest.setStart(time.getApTime().getMillis() - 60 * 60 * 1000); // 60 minutes
                tradesRequest.setSymbol(symbol);
                TradePage trades = client.getTrades(tradesRequest);
                if (!trades.getTrade().isEmpty()) {
                    candle(symbol);
                    Thread.sleep(200);
                }
            }
        } else {
            for (String coin : coins) {
                candle(coin + "_BNB");
            }
        }
        
        Collections.sort(statistics, new Comparator<CandleStatistic>() {

            @Override
            public int compare(CandleStatistic o1, CandleStatistic o2) {
                return o2.getSum().compareTo(o1.getSum());
            }
        });
        
        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(statistics);
        log.info("{}", s);
    }
    
    private static void candle(String symbol) throws InterruptedException {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        long oneDay = 86400000L;
        long startTime = 1551945600000L; // 07032019 08:00:00 UTC
        long endTime = startTime + oneDay; // 1552032000000L; // 08032019 08:00:00 UTC
        double sumSum = 0;
        for (int i = 7; i < 22; i++) {
            double sum = 0;
            List<Candlestick> candleStickBars = client.getCandleStickBars(symbol, CandlestickInterval.FIVE_MINUTES, 300, startTime, endTime);
            Thread.sleep(200);
            /*ObjectMapper om = new ObjectMapper();
            try {
                String string = om.writeValueAsString(candleStickBars);
                //Files.write(string.getBytes(), new File("candleStickBars." + symbol + "." + i + ".json"));
            } catch (IOException ex) {
                log.error("IOException ex: {}", ex.getMessage());
            }*/
            for (Candlestick candlestick : candleStickBars) {
                // log.info("{}", candlestick);
                double high = Double.parseDouble(candlestick.getHigh());
                double low = Double.parseDouble(candlestick.getLow());
                double delta = 0;
                if (high > 15 && low > 15) {
                    delta = 0;
                } else if (high > 15) {
                    delta = 0; //15 - low;
                } else {
                    delta = high - low;
                }
                sum += Math.min(delta, 10) * 3;
            }
            //log.info("symbol: {} i: {} sum: {}", symbol, i, sum);
            sumSum += sum;
            startTime += oneDay;
            endTime += oneDay;
        }
        log.info("symbol: {} sumSum: {}", symbol, sumSum);
        statistics.add(new CandleStatistic(symbol, sumSum));
    }
}
