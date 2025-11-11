package com.simulation.state;

import com.simulation.Individual;
import java.awt.Color;

/**
 * [cite_start]Stan: ODPORNY [cite: 11]
 * Osobnik nie może się zarazić.
 */
public class ResistantState implements HealthState {

    @Override
    public void update(Individual context, double timeStep) {
        // Odporny, nic nie robi
    }

    @Override
    public void trackContact(Individual context, Individual infectedBy, double timeStep) {
        // Jest odporny, ignoruje kontakt
    }

    @Override
    public Color getColor() {
        return Color.BLUE; // Niebieski = odporny
    }

    @Override
    public HealthState deepCopy() {
        return new ResistantState();
    }
}