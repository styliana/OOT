package org.example;

import org.example.io.*;
import org.example.model.*;
import org.example.state.*;
import org.example.ui.*;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ExchangeApp {
    private static ExchangeApp instance;

    private ExchangeTable exchangeTable;
    private IRemoteRepository repository;
    private AppState state;

    private ExchangeApp() {
        this.repository = new RESTNBP();
        this.state = new InitState(this);
    }

    public static synchronized ExchangeApp getInstance() {
        if (instance == null) instance = new ExchangeApp();
        return instance;
    }

    public ExchangeTable getExchangeTable() { return exchangeTable; }
    public void setExchangeTable(ExchangeTable table) { this.exchangeTable = table; }
    public void setState(AppState state) { this.state = state; }

    public void downloadRepoData() throws NoSuchAlgorithmException, KeyManagementException { state.downloadData(); }

    public byte[] fetchRemote(String url) throws Exception { return repository.fetch(url); }

    public void run() {
        ConsoleInterface menu = new MenuCommand("");
        ConsoleInterface display = new DisplayTableCommand("T");
        ConsoleInterface exchange = new ExchangeCurrencyCommand("Z");
        ConsoleInterface exit = new ExitCommand("0");

        menu.setNext(display);
        display.setNext(exchange);
        exchange.setNext(exit);

        try {
            setState(new InitState(this));
            downloadRepoData();
        } catch (Exception e) {
            setState(new ErrorState(this, e));
            state.handle();
        }

        menu.handle(new String[]{});
    }
}
