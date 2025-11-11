package com.vector;

import com.vector.interfaces.ISpherical3D;
import com.vector.interfaces.IVector;

public class Vector3DInheritance extends Vector2D implements ISpherical3D {
    private final double z;

    public Vector3DInheritance(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    @Override
    public double abs() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    @Override
    public double[] getComponents() {
        return new double[]{x, y, z};
    }

    // Nadpisana metoda iloczynu skalarnego
    @Override
    public double cdot(IVector vector) {
        double[] componentsOther = vector.getComponents();
        double result = 0;
        int minDimensions = Math.min(this.getComponents().length, componentsOther.length);
        for (int i = 0; i < minDimensions; i++) {
            result += this.getComponents()[i] * componentsOther[i];
        }
        return result;
    }

    // Metoda iloczynu wektorowego
    public IVector cross(IVector vector) {
        double[] other = vector.getComponents();
        double otherZ = (other.length > 2) ? other[2] : 0;

        double newX = this.y * otherZ - this.z * other[1];
        double newY = this.z * other[0] - this.x * otherZ;
        double newZ = this.x * other[1] - this.y * other[0];

        return new Vector3DInheritance(newX, newY, newZ);
    }

    // Metody dla współrzędnych sferycznych
    @Override
    public double getRadius() {
        return this.abs();
    }

    @Override
    public double getTheta() {
        return Math.toDegrees(Math.acos(z / getRadius()));
    }

    @Override
    public double getPhi() {
        return Math.toDegrees(Math.atan2(y, x));
    }
}