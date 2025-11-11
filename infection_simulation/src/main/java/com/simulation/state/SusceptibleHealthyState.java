package com.simulation.state;

import com.simulation.Individual;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Stan: WRAŻLIWY I ZDROWY
 * Może zostać zakażony.
 */
public class SusceptibleHealthyState implements HealthState {

    private Map<Integer, Double> contactTracker;

    public SusceptibleHealthyState() {
        this.contactTracker = new HashMap<>();
    }

    private SusceptibleHealthyState(Map<Integer, Double> contactTracker) {
        this.contactTracker = new HashMap<>(contactTracker);
    }

    @Override
    public void update(Individual context, double timeStep) {
        contactTracker.entrySet().removeIf(entry -> entry.getValue() < 0.1);
    }

    @Override
    public void trackContact(Individual context, Individual infectedBy, double timeStep) {
        if (!(infectedBy.getCurrentState() instanceof SusceptibleInfectedState)) {
            return;
        }
        SusceptibleInfectedState sourceState = (SusceptibleInfectedState) infectedBy.getCurrentState();

        int sourceId = infectedBy.getId();
        double currentContactTime = contactTracker.getOrDefault(sourceId, 0.0);
        currentContactTime += timeStep;
        contactTracker.put(sourceId, currentContactTime);

        if (currentContactTime >= 3.0) {

            double infectionChance = sourceState.isSymptomatic() ? 1.0 : 0.5;

            if (Math.random() < infectionChance) {
                context.setState(new SusceptibleInfectedState());
            } else {
                contactTracker.put(sourceId, 0.0);
            }
        }
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }

    @Override
    public HealthState deepCopy() {
        return new SusceptibleHealthyState(this.contactTracker);
    }
}