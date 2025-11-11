package com.simulation.state;

import com.simulation.Individual;
import java.awt.Color;

/**
 * Interfejs Wzorca Stan (State)
 * Definiuje zachowanie osobnika w zależności od jego stanu zdrowia.
 */
public interface HealthState {

    /**
     * Wywoływane w każdym kroku symulacji.
     * @param context Osobnik (Kontekst), którego stan aktualizujemy.
     * @param timeStep Czas, jaki upłynął od ostatniego kroku (w sekundach).
     */
    void update(Individual context, double timeStep);

    /**
     * Metoda wywoływana, gdy zdrowy osobnik znajdzie się w zasięgu zakażonego.
     * @param context Osobnik (Kontekst), który może zostać zarażony.
     * @param infectedBy Osobnik, który zaraża.
     * @param timeStep Czas trwania kontaktu w tym kroku.
     */
    void trackContact(Individual context, Individual infectedBy, double timeStep);

    /**
     * Zwraca kolor do wizualizacji.
     */
    Color getColor();

    /**
     * Tworzy głęboką kopię tego obiektu stanu.
     */
    HealthState deepCopy();
}