package org.example.state;

import org.example.ExchangeApp;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class InitState extends AppState {
    public InitState(ExchangeApp app){ super(app); }

    @Override
    public void downloadData() throws NoSuchAlgorithmException, KeyManagementException {
        DownloadingState ds = new DownloadingState(app);
        app.setState(ds);
        ds.downloadData();
    }
}
