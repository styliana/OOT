package com.simulation.state;

import com.simulation.Individual;
import java.awt.Color;
import java.util.Random;

/**
 * Stan: ZAKAŻONY (objawowy lub bezobjawowy)
 */
public class SusceptibleInfectedState implements HealthState {

    private double infectionDuration;
    private final double recoveryTime;
    private final boolean isSymptomatic;
    private static final Random rand = new Random();

    public SusceptibleInfectedState() {
        this.infectionDuration = 0;
        this.recoveryTime = 20.0 + (rand.nextDouble() * 10.0);
        this.isSymptomatic = rand.nextBoolean();
    }

    private SusceptibleInfectedState(double infectionDuration, double recoveryTime, boolean isSymptomatic) {
        this.infectionDuration = infectionDuration;
        this.recoveryTime = recoveryTime;
        this.isSymptomatic = isSymptomatic;
    }

    @Override
    public void update(Individual context, double timeStep) {
        infectionDuration += timeStep;
        if (infectionDuration >= recoveryTime) {
            context.setState(new ResistantState());
        }
    }

    @Override
    public void trackContact(Individual context, Individual infectedBy, double timeStep) {
    }

    public boolean isSymptomatic() {
        return isSymptomatic;
    }

    @Override
    public Color getColor() {
        return isSymptomatic ? Color.RED : Color.ORANGE;
    }

    @Override
    public HealthState deepCopy() {
        return new SusceptibleInfectedState(this.infectionDuration, this.recoveryTime, this.isSymptomatic);
    }
}