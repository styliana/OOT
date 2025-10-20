package org.example.ui;

import org.example.ExchangeApp;
import org.example.model.ExchangeRate;
import org.example.model.ExchangeTable;
import org.example.util.Validators;

import java.util.Scanner;

public class ExchangeCurrencyCommand extends ConsoleInterface {
    public ExchangeCurrencyCommand(String parameter) {
        super(parameter);
    }

    @Override
    public void handle(String[] params) {
        if (params.length > 0 && "Z".equalsIgnoreCase(params[0])) {
            Scanner sc = new Scanner(System.in);
            ExchangeApp app = ExchangeApp.getInstance();
            ExchangeTable table = app.getExchangeTable();

            if (table == null) {
                System.out.println("Brak danych kursowych.");
                return;
            }

            String src, dest;
            double amount;

            // WALUTA ŹRÓDŁOWA
            do {
                System.out.print("Waluta źródłowa: ");
                src = sc.nextLine().trim().toUpperCase();
                if (!Validators.isCurrencyCode(src) || (!"PLN".equals(src) && table.getRate(src) == null)) {
                    System.out.println("Nie znaleziono waluty: " + src + ". Spróbuj ponownie.");
                    src = null;
                }
            } while (src == null);

            // WALUTA DOCELOWA
            do {
                System.out.print("Waluta docelowa: ");
                dest = sc.nextLine().trim().toUpperCase();
                if (!Validators.isCurrencyCode(dest) || (!"PLN".equals(dest) && table.getRate(dest) == null)) {
                    System.out.println("Nie znaleziono waluty: " + dest + ". Spróbuj ponownie.");
                    dest = null;
                }
            } while (dest == null);

            // KWOTA
            do {
                System.out.print("Kwota: ");
                String amtStr = sc.nextLine().trim();
                if (!Validators.isPositiveNumber(amtStr)) {
                    System.out.println("Nieprawidłowa kwota. Spróbuj ponownie.");
                    amount = -1;
                } else {
                    amount = Double.parseDouble(amtStr.replace(",", "."));
                }
            } while (amount <= 0);

            // Konwersja
            double result;
            if (src.equals(dest)) {
                System.out.printf("Wynik: %.2f %s%n", amount, dest);
            } else if ("PLN".equals(src)) {
                ExchangeRate destRate = table.getRate(dest);
                result = amount / destRate.getRate();
                System.out.printf("Wynik: %.4f %s (kurs: 1 %s = %.4f PLN)%n", result, dest, dest, destRate.getRate());
            } else if ("PLN".equals(dest)) {
                ExchangeRate srcRate = table.getRate(src);
                result = amount * srcRate.getRate();
                System.out.printf("Wynik: %.2f PLN (kurs: 1 %s = %.4f PLN)%n", result, src, srcRate.getRate());
            } else {
                ExchangeRate srcRate = table.getRate(src);
                ExchangeRate destRate = table.getRate(dest);
                double inPLN = amount * srcRate.getRate();
                result = inPLN / destRate.getRate();
                System.out.printf("Wynik: %.4f %s%n", result, dest);
                System.out.printf("(%.2f %s = %.2f PLN = %.4f %s)%n", amount, src, inPLN, result, dest);
            }
        }
        handleConclude(params);
    }
}
