package org.example.ui;

import org.example.ExchangeApp;
import org.example.model.ExchangeRate;
import org.example.model.ExchangeTable;

import java.util.Iterator;

public class DisplayTableCommand extends ConsoleInterface {
    public DisplayTableCommand(String parameter){ super(parameter); }

    @Override
    public void handle(String[] params){
        if(params.length > 0 && "T".equalsIgnoreCase(params[0])){
            ExchangeApp app = ExchangeApp.getInstance();
            ExchangeTable table = app.getExchangeTable();
            if(table == null) {
                System.out.println("Brak załadowanych kursów.");
            } else {
                System.out.println("Kursy (" + table.getId() + "):");
                Iterator<ExchangeRate> it = table.iterator();
                while(it.hasNext()) {
                    System.out.println(it.next());
                }
            }
        }
        handleConclude(params);
    }
}
