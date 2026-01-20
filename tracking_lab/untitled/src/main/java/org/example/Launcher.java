package org.example;

public class Launcher {
    public static void main(String[] args) {
        // To jest obejście problemu. Wywołujemy main z RadarGUI z innej klasy,
        // która NIE dziedziczy po javafx.application.Application.
        RadarGUI.main(args);
    }
}