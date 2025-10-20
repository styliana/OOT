package org.example.util;

public class Validators {

    // Sprawdza, czy podany string to dodatnia liczba (akceptuje ',' jako separator dziesiętny)
    public static boolean isPositiveNumber(String s) {
        if (s == null || s.isEmpty()) return false;
        try {
            double v = Double.parseDouble(s.replace(",", "."));
            return v > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Sprawdza, czy podany string to kod waluty ISO (3 litery)
    public static boolean isCurrencyCode(String s) {
        return s != null && s.matches("^[A-Za-z]{3}$");
    }

    // Dodatkowo można zrobić helper do menu – tylko T/Z/0
    public static boolean isMenuOption(String s) {
        return s != null && (s.equalsIgnoreCase("T") || s.equalsIgnoreCase("Z") || s.equals("0"));
    }
}
