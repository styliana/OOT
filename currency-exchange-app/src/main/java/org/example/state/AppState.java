package org.example.state;

import org.example.ExchangeApp;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public abstract class AppState {
    protected final ExchangeApp app;
    public AppState(ExchangeApp app){ this.app = app; }
    public void handle() { }
    public void downloadData() throws NoSuchAlgorithmException, KeyManagementException { }
}
