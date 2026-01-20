package org.example;

import java.util.ArrayList;
import java.util.List;

public class Blob {
    public int id;
    public List<Point> points = new ArrayList<>();

    // Statystyki pozycyjne (rozkład normalny)
    public double meanX, meanY;
    public double stdDevX, stdDevY;

    public Blob(int id) {
        this.id = id;
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }

    // Oblicza parametry rozkładu normalnego (średnia i odchylenie)
    public void calculateStatistics() {
        if (points.isEmpty()) return;

        double sumX = 0, sumY = 0;
        for (Point p : points) {
            sumX += p.x;
            sumY += p.y;
        }
        meanX = sumX / points.size();
        meanY = sumY / points.size();

        double varSumX = 0, varSumY = 0;
        for (Point p : points) {
            varSumX += Math.pow(p.x - meanX, 2);
            varSumY += Math.pow(p.y - meanY, 2);
        }

        stdDevX = Math.sqrt(varSumX / points.size());
        stdDevY = Math.sqrt(varSumY / points.size());
    }

    @Override
    public String toString() {
        return String.format("Blob #%d: Środek[%.1f, %.1f] StdDev[%.1f, %.1f] Pikseli: %d",
                id, meanX, meanY, stdDevX, stdDevY, points.size());
    }

    // Klasa pomocnicza (można ją wynieść do osobnego pliku, ale tu pasuje)
    public static class Point {
        public int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
    }
}