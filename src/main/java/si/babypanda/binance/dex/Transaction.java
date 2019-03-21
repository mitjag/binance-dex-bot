package si.babypanda.binance.dex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    
    // {"txHash":"AF3E8A58A1FFF9FE62C503ED9C8A3F05850CA4A446BA195FAC6EB29D64531C56","blockHeight":1821810,"txType":"NEW_ORDER","timeStamp":1552646141512,"fromAddr":"tbnb1n6nm46cyzzt40ym9lnpgvdh73pnrhtnh69za8w","value":15.00000000,"txAsset":"III-25C","mappedTxAsset":"III","txQuoteAsset":"BNB","mappedTxQuoteAsset":"BNB","txFee":0.00000000,"txAge":542094,"orderId":"9EA7BAEB041097579365FCC28636FE88663BAE77-10","data":"{\"orderData\":{\"symbol\":\"III-25C_BNB\",\"orderType\":\"limit\",\"side\":\"buy\",\"price\":15.00000000,\"quantity\":1.00000000,\"timeInForce\":\"GTE\",\"orderId\":\"9EA7BAEB041097579365FCC28636FE88663BAE77-10\"}}","code":0,"log":"Msg
    // 0: ","confirmBlocks":0,"hasChildren":0},
    
    private Long blockHeight;
    private Integer code;
    private Long confirmBlocks;
    private String data;
    private String fromAddr;
    private String orderId;
    private String timeStamp;
    private String toAddr;
    private Long txAge;
    private String txAsset;
    private String mappedTxAsset;
    private String txQuoteAsset;
    private String mappedTxQuoteAsset;
    private String txFee;
    private String txHash;
    private String txType;
    private String value;
    
    public Long getBlockHeight() {
        return blockHeight;
    }
    
    public void setBlockHeight(Long blockHeight) {
        this.blockHeight = blockHeight;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public Long getConfirmBlocks() {
        return confirmBlocks;
    }
    
    public void setConfirmBlocks(Long confirmBlocks) {
        this.confirmBlocks = confirmBlocks;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public String getFromAddr() {
        return fromAddr;
    }
    
    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getTimeStamp() {
        return timeStamp;
    }
    
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public String getToAddr() {
        return toAddr;
    }
    
    public void setToAddr(String toAddr) {
        this.toAddr = toAddr;
    }
    
    public Long getTxAge() {
        return txAge;
    }
    
    public void setTxAge(Long txAge) {
        this.txAge = txAge;
    }
    
    public String getTxAsset() {
        return txAsset;
    }
    
    public void setTxAsset(String txAsset) {
        this.txAsset = txAsset;
    }
    
    public String getMappedTxAsset() {
        return mappedTxAsset;
    }
    
    public void setMappedTxAsset(String mappedTxAsset) {
        this.mappedTxAsset = mappedTxAsset;
    }
    
    public String getTxQuoteAsset() {
        return txQuoteAsset;
    }
    
    public void setTxQuoteAsset(String txQuoteAsset) {
        this.txQuoteAsset = txQuoteAsset;
    }
    
    public String getMappedTxQuoteAsset() {
        return mappedTxQuoteAsset;
    }
    
    public void setMappedTxQuoteAsset(String mappedTxQuoteAsset) {
        this.mappedTxQuoteAsset = mappedTxQuoteAsset;
    }
    
    public String getTxFee() {
        return txFee;
    }
    
    public void setTxFee(String txFee) {
        this.txFee = txFee;
    }
    
    public String getTxHash() {
        return txHash;
    }
    
    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
    
    public String getTxType() {
        return txType;
    }
    
    public void setTxType(String txType) {
        this.txType = txType;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
