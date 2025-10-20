package org.example.model;

public class ExchangeRate {
    private String currency;
    private String code;
    private double mid;

    public ExchangeRate(String currency, String code, double mid) {
        this.currency = currency;
        this.code = code;
        this.mid = mid;
    }

    public String getId() { return code; }
    public String getName() { return currency; }
    public double getRate() { return mid; }
    public int getMultiplier() { return 1; }

    @Override
    public String toString() {
        return String.format("%s (%s): %.4f PLN", currency, code, mid);
    }
}
