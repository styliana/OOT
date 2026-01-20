package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlobExtractor {

    public List<Blob> extract(int[][] image, int threshold, int width, int height) {
        int[][] labels = new int[height][width];
        int nextLabel = 1;

        // Tablica dla Union-Find (max przypuszczenie liczby obiektów)
        int[] parent = new int[width * height / 2];
        for (int i = 0; i < parent.length; i++) parent[i] = i;

        // --- Pierwsze przejście (First Pass) ---
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (image[y][x] <= threshold) continue;

                // Sprawdzanie sąsiadów (4-sąsiedztwo: lewo i góra)
                int left = (x > 0) ? labels[y][x - 1] : 0;
                int top = (y > 0) ? labels[y - 1][x] : 0;

                if (left == 0 && top == 0) {
                    labels[y][x] = nextLabel++;
                } else if (left != 0 && top == 0) {
                    labels[y][x] = left;
                } else if (left == 0 && top != 0) {
                    labels[y][x] = top;
                } else {
                    // Konflikt - zapisujemy równoważność
                    labels[y][x] = Math.min(left, top);
                    union(parent, left, top);
                }
            }
        }

        // --- Drugie przejście (Second Pass) i grupowanie ---
        Map<Integer, Blob> blobMap = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int label = labels[y][x];
                if (label > 0) {
                    // Rozwiązanie konfliktu (znalezienie korzenia)
                    int root = find(parent, label);
                    labels[y][x] = root; // opcjonalna aktualizacja mapy etykiet

                    blobMap.putIfAbsent(root, new Blob(root));
                    blobMap.get(root).addPoint(x, y);
                }
            }
        }

        // Filtrowanie i liczenie statystyk
        List<Blob> result = new ArrayList<>();
        for (Blob b : blobMap.values()) {
            if (b.points.size() > 2) { // Ignorujemy mały szum (<3 px)
                b.calculateStatistics();
                result.add(b);
            }
        }
        return result;
    }

    // Metody Union-Find
    private int find(int[] parent, int i) {
        if (parent[i] == i) return i;
        return parent[i] = find(parent, parent[i]);
    }

    private void union(int[] parent, int i, int j) {
        int rootI = find(parent, i);
        int rootJ = find(parent, j);
        if (rootI != rootJ) {
            parent[rootJ] = rootI;
        }
    }
}