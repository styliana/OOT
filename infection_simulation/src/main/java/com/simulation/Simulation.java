package com.simulation;

import com.simulation.memento.SimulationMemento;
import com.simulation.state.*;
import com.vector.Vector2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Główna klasa symulacji.
 * Działa jako Originator dla wzorca Memento.
 */
public class Simulation {
    private List<Individual> individuals;
    private double simulationTime;
    private final double areaWidth;
    private final double areaHeight;
    private final double initialImmunityChance; // NOWE POLE
    private static final Random rand = new Random();

    public static final double TIME_STEP = 1.0 / 25.0;

    // ZAKTUALIZOWANY KONSTRUKTOR
    public Simulation(double width, double height, int initialPopulation, double initialImmunityChance) {
        this.areaWidth = width;
        this.areaHeight = height;
        this.simulationTime = 0;
        this.initialImmunityChance = initialImmunityChance; // ZAPISZ SZANSĘ
        this.individuals = new ArrayList<>();

        for (int i = 0; i < initialPopulation; i++) {
            individuals.add(createRandomIndividual(true));
        }
    }

    /**
     * Tworzy nowego osobnika (na granicy lub losowo)
     */
    private Individual createRandomIndividual(boolean isInitial) {
        Vector2D startPosition;
        if (isInitial) {
            // Populacja początkowa startuje losowo na całym obszarze
            startPosition = new Vector2D(rand.nextDouble() * areaWidth, rand.nextDouble() * areaHeight);
        } else {
            // *** POPRAWIONA LOGIKA ***
            // Nowi osobnicy wkraczają na losowej granicy (zgodnie z PDF)
            double x, y;
            int boundary = rand.nextInt(4); // 0=góra, 1=dół, 2=lewo, 3=prawo

            if (boundary == 0) { // góra
                x = rand.nextDouble() * areaWidth;
                y = areaHeight;
            } else if (boundary == 1) { // dół
                x = rand.nextDouble() * areaWidth;
                y = 0;
            } else if (boundary == 2) { // lewo
                x = 0;
                y = rand.nextDouble() * areaHeight;
            } else { // prawo
                x = areaWidth;
                y = rand.nextDouble() * areaHeight;
            }
            startPosition = new Vector2D(x, y);
            // *** KONIEC POPRAWIONEJ LOGIKI ***
        }

        // --- NOWA LOGIKA STANU POCZĄTKOWEGO ---
        HealthState initialState;

        // Wymaganie: Sprawdź, czy osobnik ma odporność początkową
        if (rand.nextDouble() < this.initialImmunityChance) {
            initialState = new ResistantState(); // Startuje jako Niebieski
        } else {
            // Jeśli nie jest odporny, jest wrażliwy...

            // Wymaganie: Sprawdź, czy wkraczający jest zakażony (10% szans)
            // Dotyczy tylko wkraczających (nie populacji początkowej)
            if (!isInitial && rand.nextDouble() < 0.10) {
                initialState = new SusceptibleInfectedState(); // Startuje jako Czerwony/Pomarańczowy
            } else {
                // W przeciwnym razie jest zdrowy i wrażliwy
                initialState = new SusceptibleHealthyState(); // Startuje jako Zielony
            }
        }
        // ----------------------------------------

        return new Individual(startPosition, initialState);
    }

    /**
     * Główna metoda pętli, wywoływana przez Timer.
     */
    public void step() {
        simulationTime += TIME_STEP;

        for (Individual ind : individuals) {
            ind.update(TIME_STEP);
        }

        checkTransmissions();

        for (Individual ind : individuals) {
            ind.handleBoundaries(areaWidth, areaHeight);
        }

        individuals.removeIf(Individual::hasLeftArea);

        if (rand.nextDouble() < 0.01) {
            individuals.add(createRandomIndividual(false));
        }
    }

    /**
     * Sprawdza pary osobników pod kątem możliwej transmisji.
     */
    private void checkTransmissions() {
        for (int i = 0; i < individuals.size(); i++) {
            for (int j = i + 1; j < individuals.size(); j++) {
                Individual indA = individuals.get(i);
                Individual indB = individuals.get(j);

                double distance = indA.getPosition().subtract(indB.getPosition()).abs();

                if (distance <= 2.0) {
                    tryInfect(indA, indB, TIME_STEP);
                    tryInfect(indB, indA, TIME_STEP);
                }
            }
        }
    }

    /**
     * Pomocnicza metoda sprawdzająca, czy 'source' może zarazić 'target'.
     */
    private void tryInfect(Individual source, Individual target, double timeStep) {
        if (source.getCurrentState() instanceof SusceptibleInfectedState &&
                target.getCurrentState() instanceof SusceptibleHealthyState) {

            target.trackProximityContact(source, timeStep);
        }
    }

    // --- Gettery dla GUI ---
    public List<Individual> getIndividuals() { return individuals; }
    public double getWidth() { return areaWidth; }
    public double getHeight() { return areaHeight; }
    public double getSimulationTime() { return simulationTime; }

    // --- Metody Wzorca Memento ---

    public SimulationMemento save() {
        return new SimulationMemento(this.individuals, this.simulationTime);
    }

    public void restore(SimulationMemento memento) {
        this.individuals = memento.getClonedIndividuals();
        this.simulationTime = memento.getSavedTime();
    }
}