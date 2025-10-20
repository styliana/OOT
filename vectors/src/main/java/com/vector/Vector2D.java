package com.vector;

import com.vector.interfaces.IVector;

public class Vector2D implements IVector {
    protected final double x;
    protected final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public double abs() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    @Override
    public double cdot(IVector vector) {
        double[] components = vector.getComponents();
        double result = 0;
        // Mnożenie skalarnie tylko dla tylu wymiarów, ile ma mniejszy wektor
        int minDimensions = Math.min(this.getComponents().length, components.length);
        for (int i = 0; i < minDimensions; i++) {
            result += this.getComponents()[i] * components[i];
        }
        return result;
    }

    @Override
    public double[] getComponents() {
        return new double[]{x, y};
    }
}