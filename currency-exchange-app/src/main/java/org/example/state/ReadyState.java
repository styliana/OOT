package org.example.state;

import org.example.ExchangeApp;

public class ReadyState extends AppState {
    public ReadyState(ExchangeApp app){ super(app); }

    @Override
    public void handle() {
        System.out.println("Dane kursów załadowane.");
    }
}
