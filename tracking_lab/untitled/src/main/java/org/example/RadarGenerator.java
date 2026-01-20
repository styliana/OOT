package org.example;

import java.util.List;
import java.util.Random;

public class RadarGenerator {
    private int width, height;
    private Random random = new Random();

    public RadarGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int[][] generateFrame(List<Blob.Point> trueTargets) {
        int[][] frame = new int[height][width];

        // Generowanie szumu tła [cite: 13]
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                frame[y][x] = random.nextInt(40);
            }
        }

        // Rysowanie echa obiektów [cite: 9, 12]
        if (trueTargets != null) {
            for (Blob.Point p : trueTargets) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int px = p.x + dx;
                        int py = p.y + dy;
                        if (px >= 0 && px < width && py >= 0 && py < height) {
                            frame[py][px] = 210 + random.nextInt(45);
                        }
                    }
                }
            }
        }
        return frame;
    }
}