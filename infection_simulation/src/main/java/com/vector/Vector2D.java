package com.vector;

import com.vector.interfaces.IVector;

public class Vector2D implements IVector {
    protected final double x;
    protected final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2D other) {
        this.x = other.x;
        this.y = other.y;
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

    /**
     * Zwraca nowy wektor będący wynikiem odejmowania (this - other).
     */
    public Vector2D subtract(IVector other) {
        double[] otherComp = other.getComponents();
        double otherX = (otherComp.length > 0) ? otherComp[0] : 0;
        double otherY = (otherComp.length > 1) ? otherComp[1] : 0;
        return new Vector2D(this.x - otherX, this.y - otherY);
    }

    /**
     * Zwraca nowy wektor będący wynikiem dodawania (this + other).
     */
    public Vector2D add(IVector other) {
        double[] otherComp = other.getComponents();
        double otherX = (otherComp.length > 0) ? otherComp[0] : 0;
        double otherY = (otherComp.length > 1) ? otherComp[1] : 0;
        return new Vector2D(this.x + otherX, this.y + otherY);
    }
}