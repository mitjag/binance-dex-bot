package si.babypanda.binance.dex;

import java.io.Serializable;

public class CandleStatistic implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String symbol;
    
    private Double sum;
    
    public CandleStatistic() {
    }
    
    public CandleStatistic(String symbol, Double sum) {
        super();
        this.symbol = symbol;
        this.sum = sum;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public Double getSum() {
        return sum;
    }
    
    public void setSum(Double sum) {
        this.sum = sum;
    }
}
