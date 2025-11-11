package com.simulation;

import com.simulation.state.HealthState;
import com.simulation.state.ResistantState;
import com.simulation.state.SusceptibleHealthyState;
import com.vector.Vector2D;
import java.util.Random;

/**
 * Klasa Osobnika.
 * Działa jako Kontekst (Context) dla wzorca Stan.
 */
public class Individual {

    private static int idCounter = 0;
    private final int id;
    private Vector2D position;
    private Vector2D velocity;
    private HealthState currentState;

    private boolean hasLeftArea = false;
    private static final Random rand = new Random();

    public Individual(Vector2D position, HealthState initialState) {
        this.id = idCounter++;
        this.position = position;
        this.currentState = initialState;
        this.velocity = createRandomVelocity();
    }

    /**
     * Konstruktor kopiujący (NIEZBĘDNY dla wzorca Pamiątka).
     */
    public Individual(Individual other) {
        this.id = other.id;
        this.position = new Vector2D(other.position);
        this.velocity = new Vector2D(other.velocity);
        this.currentState = other.currentState.deepCopy();
        this.hasLeftArea = other.hasLeftArea;
    }

    /**
     * Główna metoda aktualizująca stan osobnika, wywoływana przez Pętlę Symulacji.
     * @param timeStep czas kroku (np. 1/25 sekundy)
     */
    public void update(double timeStep) {
        currentState.update(this, timeStep);

        Vector2D velocityStep = new Vector2D(velocity.getX() * timeStep, velocity.getY() * timeStep);
        this.position = this.position.add(velocityStep);

        // --- NOWA LOGIKA: Losowa zmiana prędkości  ---
        // Dajemy 0.5% szansy na losową zmianę prędkości w każdym kroku
        if (rand.nextDouble() < 0.005) {
            this.velocity = createRandomVelocity();
        }
        // --------------------------------------------------
    }

    /**
     * Metoda wywoływana przez pętlę symulacji, gdy ten osobnik jest blisko zakażonego.
     */
    public void trackProximityContact(Individual infectedBy, double timeStep) {
        this.currentState.trackContact(this, infectedBy, timeStep);
    }

    /**
     * Tworzy losową prędkość (kierunek i szybkość)
     */
    private Vector2D createRandomVelocity() {
        double maxSpeed = 2.5;
        double speed = rand.nextDouble() * maxSpeed;
        double angleRadians = rand.nextDouble() * 2 * Math.PI;

        double vx = speed * Math.cos(angleRadians);
        double vy = speed * Math.sin(angleRadians);
        return new Vector2D(vx, vy);
    }

    /**
     * Logika dotarcia do granicy.
     */
    public void handleBoundaries(double width, double height) {
        boolean atBoundary = position.getX() <= 0 || position.getX() >= width ||
                position.getY() <= 0 || position.getY() >= height;

        if (atBoundary) {
            if (rand.nextDouble() < 0.5) {
                this.hasLeftArea = true;
            } else {
                this.velocity = new Vector2D(-velocity.getX(), -velocity.getY());

                if (position.getX() <= 0) position = new Vector2D(1, position.getY());
                if (position.getX() >= width) position = new Vector2D(width - 1, position.getY());
                if (position.getY() <= 0) position = new Vector2D(position.getX(), 1);
                if (position.getY() >= height) position = new Vector2D(position.getX(), height - 1);
            }
        }
    }

    // --- Gettery i Settery ---

    public int getId() { return id; }
    public Vector2D getPosition() { return position; }
    public HealthState getCurrentState() { return currentState; }
    public boolean hasLeftArea() { return hasLeftArea; }

    public void setState(HealthState newState) {
        this.currentState = newState;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.id == ((Individual) obj).id;
    }
}