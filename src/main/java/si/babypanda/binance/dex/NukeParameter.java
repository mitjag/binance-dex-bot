package si.babypanda.binance.dex;

import java.io.Serializable;

public class NukeParameter implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private double buyPrice;
    
    private double sellPrice;
    
    private double buyAmount;
    
    private double budget;
    
    private int type;
    
    public NukeParameter() {
    }
    
    public NukeParameter(double buyPrice, double sellPrice, double buyAmount, int type) {
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.buyAmount = buyAmount;
        budget = buyPrice * buyAmount + 5;
        this.setType(type);
    }
    
    public double getBuyPrice() {
        return buyPrice;
    }
    
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }
    
    public double getSellPrice() {
        return sellPrice;
    }
    
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    public double getBuyAmount() {
        return buyAmount;
    }
    
    public void setBuyAmount(double buyAmount) {
        this.buyAmount = buyAmount;
    }
    
    public double getBudget() {
        return budget;
    }
    
    public void setBudget(double budget) {
        this.budget = budget;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
}
