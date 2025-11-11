package com.simulation.memento;

import com.simulation.Individual;
import java.util.ArrayList;
import java.util.List;

/**
 * Pamiątka (Memento)
 * Przechowuje "migawkę" stanu symulacji.
 * (Zmieniono z 'record' na 'class' dla kompatybilności z Javą 8).
 */
public class SimulationMemento {

    private final List<Individual> savedIndividuals;
    private final double savedTime;

    /**
     * Konstruktor, który zapewnia GŁĘBOKĄ KOPIĘ listy osobników.
     */
    public SimulationMemento(List<Individual> individuals, double time) {
        this.savedIndividuals = new ArrayList<>();
        for (Individual ind : individuals) {
            this.savedIndividuals.add(new Individual(ind));
        }
        this.savedTime = time;
    }

    /**
     * Zwraca GŁĘBOKĄ KOPIĘ zapisanych osobników, aby chronić stan pamiątki.
     */
    public List<Individual> getClonedIndividuals() {
        List<Individual> clonedList = new ArrayList<>();
        for (Individual ind : this.savedIndividuals) {
            clonedList.add(new Individual(ind));
        }
        return clonedList;
    }

    public double getSavedTime() {
        return savedTime;
    }

    // Ten getter nie jest używany, ale dodaję dla kompletności
    public List<Individual> getSavedIndividuals() {
        return savedIndividuals;
    }
}