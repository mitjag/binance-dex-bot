package si.babypanda.binance.dex;

import java.util.List;

public class TransactionPage {
    
    private Long txNums;
    private List<Transaction> txArray;
    
    public Long getTxNums() {
        return txNums;
    }
    
    public void setTxNums(Long txNums) {
        this.txNums = txNums;
    }
    
    public List<Transaction> getTxArray() {
        return txArray;
    }
    
    public void setTxArray(List<Transaction> txArray) {
        this.txArray = txArray;
    }
}
