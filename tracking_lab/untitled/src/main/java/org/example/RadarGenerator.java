package org.example;

import java.util.List;
import java.util.Random;

public class RadarGenerator {
    private int width;
    private int height;
    private Random random;

    public RadarGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.random = new Random();
    }

    // Metoda generuje klatkę na podstawie podanych pozycji "prawdziwych" celów
    public int[][] generateFrame(List<Blob.Point> trueTargets) {
        int[][] frame = new int[height][width];

        // 1. Szum tła
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                frame[y][x] = random.nextInt(50); // Szum 0-50
            }
        }

        // 2. Rysowanie celów w zadanych pozycjach
        if (trueTargets != null) {
            for (Blob.Point p : trueTargets) {
                drawTarget(frame, p.x, p.y);
            }
        }

        return frame;
    }

    private void drawTarget(int[][] frame, int cx, int cy) {
        // Rysujemy jasną plamę (blob)
        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                int px = cx + dx;
                int py = cy + dy;

                if (px >= 0 && px < width && py >= 0 && py < height) {
                    // Symulacja echa: wysoka wartość + trochę szumu
                    int signal = 200 + random.nextInt(56);
                    frame[py][px] = Math.min(255, signal);
                }
            }
        }
    }
}