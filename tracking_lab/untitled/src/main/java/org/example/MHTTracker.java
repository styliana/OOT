package org.example;

import java.util.ArrayList;
import java.util.List;

public class MHTTracker {
    private List<Track> tracks = new ArrayList<>();

    // Bramka ograniczająca przestrzeń poszukiwań asocjacji
    private static final double GATE_THRESHOLD = 2.0;

    public void updateTracks(List<Blob> measurements) {
        // 1. Przewidywanie przyszłych pozycji
        for (Track t : tracks) {
            t.predict();
        }

        List<Blob> availableBlobs = new ArrayList<>(measurements);

        // 2. Przypisanie pomiarów do hipotez (zachłanne dopasowanie prawdopodobieństwa)
        for (Track t : tracks) {
            Blob bestBlob = null;
            double maxProb = -1.0;

            for (Blob b : availableBlobs) {
                // Wykorzystanie funkcji gęstości prawdopodobieństwa (PDF)
                double prob = calculateGaussianProbability(t, b);

                if (prob > maxProb && isInsideGate(t, b)) {
                    maxProb = prob;
                    bestBlob = b;
                }
            }

            if (bestBlob != null) {
                t.update(bestBlob);
                availableBlobs.remove(bestBlob);
            } else {
                t.missedFrames++;
            }
        }

        // 3. Odrzucenie mało prawdopodobnych ścieżek
        tracks.removeIf(t -> t.missedFrames > 10);

        // 4. Inicjalizacja nowych hipotez z nieprzypisanych pomiarów
        for (Blob b : availableBlobs) {
            tracks.add(new Track(b));
        }
    }

    private double calculateGaussianProbability(Track t, Blob b) {
        // Implementacja wzoru na rozkład normalny (Gaussa)
        double pX = (1.0 / (t.sigmaX * Math.sqrt(2 * Math.PI))) * Math.exp(-Math.pow(b.meanX - t.x, 2) / (2 * Math.pow(t.sigmaX, 2)));
        double pY = (1.0 / (t.sigmaY * Math.sqrt(2 * Math.PI))) * Math.exp(-Math.pow(b.meanY - t.y, 2) / (2 * Math.pow(t.sigmaY, 2)));
        return pX * pY;
    }

    private boolean isInsideGate(Track t, Blob b) {
        double dist = Math.sqrt(Math.pow(b.meanX - t.x, 2) + Math.pow(b.meanY - t.y, 2));
        return dist < (GATE_THRESHOLD * Math.max(t.sigmaX, t.sigmaY));
    }

    public List<Track> getTracks() { return tracks; }
}