package org.example;

import java.util.LinkedList;
import java.util.List;

public class Track {
    private static int nextId = 1;
    public int id;

    public double x, y;
    public double vx, vy;
    public double sigmaX = 5.0, sigmaY = 5.0;
    public int missedFrames = 0;

    // Zmiana: LinkedList do przechowywania ogona (max 50 punktów)
    public List<Blob.Point> history = new LinkedList<>();

    public Track(Blob initialBlob) {
        this.id = nextId++;
        this.x = initialBlob.meanX;
        this.y = initialBlob.meanY;
        this.vx = 0;
        this.vy = 0;
        addToHistory();
    }

    public void predict() {
        x += vx;
        y += vy;
    }

    public void update(Blob blob) {
        double newVx = blob.meanX - this.x;
        double newVy = blob.meanY - this.y;

        this.x = blob.meanX;
        this.y = blob.meanY;

        double alpha = 0.5;
        this.vx = (1 - alpha) * this.vx + alpha * newVx;
        this.vy = (1 - alpha) * this.vy + alpha * newVy;

        this.sigmaX = Math.max(2.0, blob.stdDevX);
        this.sigmaY = Math.max(2.0, blob.stdDevY);

        addToHistory();
        missedFrames = 0;
    }

    private void addToHistory() {
        history.add(new Blob.Point((int)x, (int)y));
        if (history.size() > 50) { // Ogranicz długość ogona
            history.remove(0);
        }
    }
}