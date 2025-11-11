package com.simulation.memento;

import com.simulation.Simulation;
import java.util.ArrayList;
import java.util.List;

/**
 * [cite_start]Opiekun (Caretaker) [cite: 53]
 * Przechowuje historię Pamiątek.
 */
public class SimulationCaretaker {
    private final List<SimulationMemento> mementos = new ArrayList<>();
    private final Simulation simulation; // Originator (Symulacja)

    public SimulationCaretaker(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Wywoływane przez przycisk "Zapisz".
     */
    public void saveState() {
        mementos.add(simulation.save());
        System.out.println("Zapisano stan symulacji. Liczba zapisów: " + mementos.size());
    }

    /**
     * Wywoływane przez przycisk "Wczytaj".
     */
    public void restoreLastState() {
        if (!mementos.isEmpty()) {
            // Pobieramy ostatnią pamiątkę
            SimulationMemento memento = mementos.get(mementos.size() - 1);
            simulation.restore(memento);
            System.out.println("Wczytano ostatni stan symulacji.");
        } else {
            System.out.println("Brak stanów do wczytania.");
        }
    }
}