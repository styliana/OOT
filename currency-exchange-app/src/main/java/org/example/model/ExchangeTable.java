package org.example.model;

import java.util.*;

public class ExchangeTable implements Iterable<ExchangeRate> {
    private final String id;
    private final Date timestamp;
    private final List<ExchangeRate> rates = new ArrayList<>();

    public ExchangeTable(String id, Date timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public void addRate(ExchangeRate r) { rates.add(r); }

    public ExchangeRate getRate(String code) {
        for (ExchangeRate r : rates) {
            if (r.getId().equalsIgnoreCase(code)) return r;
        }
        return null;
    }

    public List<ExchangeRate> getRates() { return Collections.unmodifiableList(rates); }

    public String getId() { return id; }
    public Date getTimestamp() { return timestamp; }

    @Override
    public Iterator<ExchangeRate> iterator() { return rates.iterator(); }
}
