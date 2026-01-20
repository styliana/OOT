package org.example;

import java.util.ArrayList;
import java.util.List;

public class MHTTracker {
    private List<Track> tracks = new ArrayList<>();

    // Maksymalna odległość (w odchyleniach standardowych), 
    // aby uznać dopasowanie za możliwe (bramkowanie).
    private static final double GATE_THRESHOLD = 3.0;

    /**
     * Główna metoda aktualizująca śledzenie
     */
    public void updateTracks(List<Blob> measurements) {
        // 1. Predykcja dla wszystkich istniejących ścieżek 
        for (Track t : tracks) {
            t.predict();
        }

        // 2. Asocjacja (Budowanie macierzy kosztów)
        // Szukamy najlepszego dopasowania Blob <-> Track
        // W pełnym MHT budowalibyśmy tutaj drzewo hipotez [cite: 126]

        List<Track> matchedTracks = new ArrayList<>();
        List<Blob> matchedBlobs = new ArrayList<>();

        for (Track t : tracks) {
            Blob bestBlob = null;
            double maxProbability = -1.0;

            for (Blob b : measurements) {
                if (matchedBlobs.contains(b)) continue; // Ten blob jest już zajęty

                // Obliczamy prawdopodobieństwo dopasowania [cite: 72-74]
                double prob = calculateGaussianProbability(t, b);

                // Sprawdzamy czy to najlepszy kandydat i czy mieści się w "bramce"
                if (prob > maxProbability && isInsideGate(t, b)) {
                    maxProbability = prob;
                    bestBlob = b;
                }
            }

            if (bestBlob != null) {
                t.update(bestBlob);
                matchedTracks.add(t);
                matchedBlobs.add(bestBlob);
            } else {
                t.missedFrames++;
            }
        }

        // 3. Zarządzanie życiem ścieżek

        // A. Usuwanie martwych ścieżek
        tracks.removeIf(t -> t.missedFrames > 5);

        // B. Tworzenie nowych ścieżek dla nieprzypisanych blobów
        for (Blob b : measurements) {
            if (!matchedBlobs.contains(b)) {
                // Nowa hipoteza: to jest nowy obiekt 
                tracks.add(new Track(b));
            }
        }
    }

    /**
     * KROK 1.3: Obliczanie prawdopodobieństwa z rozkładu normalnego (wzór 1) 
     * P(blob | track) = P(bx | tx) * P(by | ty)
     */
    private double calculateGaussianProbability(Track t, Blob b) {
        double probX = gaussian(b.meanX, t.x, t.sigmaX);
        double probY = gaussian(b.meanY, t.y, t.sigmaY);
        return probX * probY;
    }

    // Wzór matematyczny (1) z PDF
    private double gaussian(double x, double mu, double sigma) {
        if (sigma == 0) return 0; // Zabezpieczenie
        double exponent = -Math.pow(x - mu, 2) / (2 * Math.pow(sigma, 2));
        return (1.0 / (sigma * Math.sqrt(2 * Math.PI))) * Math.exp(exponent);
    }

    // Sprawdza czy blob jest wystarczająco blisko (Mahalanobis distance w uproszczeniu)
    private boolean isInsideGate(Track t, Blob b) {
        double dist = Math.sqrt(Math.pow(b.meanX - t.x, 2) + Math.pow(b.meanY - t.y, 2));
        // Dopuszczamy błąd 3*sigma (99.7% pewności)
        double limit = 3.0 * Math.max(t.sigmaX, t.sigmaY);
        return dist < limit;
    }

    public List<Track> getTracks() {
        return tracks;
    }
}