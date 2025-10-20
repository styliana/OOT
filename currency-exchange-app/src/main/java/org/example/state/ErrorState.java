package org.example.state;

import org.example.ExchangeApp;

public class ErrorState extends AppState {
    private final Exception ex;

    public ErrorState(ExchangeApp app, Exception ex){ super(app); this.ex = ex; }

    @Override
    public void handle() {
        System.err.println("Błąd aplikacji: " + ex.getMessage());
        ex.printStackTrace(System.err);
    }
}
