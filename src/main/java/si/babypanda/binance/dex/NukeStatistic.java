package si.babypanda.binance.dex;

import java.io.Serializable;

public class NukeStatistic implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private double bnb;
    
    private double balance;
    
    public NukeStatistic() {
    }
    
    public NukeStatistic(double bnb, double balance) {
        this.bnb = bnb;
        this.balance = balance;
    }
    
    public double getBnb() {
        return bnb;
    }
    
    public void setBnb(double bnb) {
        this.bnb = bnb;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
