package com.vector;

import com.vector.interfaces.ISpherical3D;
import com.vector.interfaces.IVector;

public class Vector3DDecorator implements IVector, ISpherical3D {
    private final IVector srcVector; // Przechowuje wektor 2D
    private final double z;

    public Vector3DDecorator(double x, double y, double z) {
        this.srcVector = new Vector2D(x, y);
        this.z = z;
    }

    @Override
    public double abs() {
        double[] components = srcVector.getComponents();
        return Math.sqrt(Math.pow(components[0], 2) + Math.pow(components[1], 2) + Math.pow(z, 2));
    }

    @Override
    public double[] getComponents() {
        double[] components2D = srcVector.getComponents();
        return new double[]{components2D[0], components2D[1], z};
    }

    @Override
    public double cdot(IVector vector) {
        double[] thisComponents = this.getComponents();
        double[] otherComponents = vector.getComponents();
        double result = 0;
        int minDimensions = Math.min(thisComponents.length, otherComponents.length);
        for (int i = 0; i < minDimensions; i++) {
            result += thisComponents[i] * otherComponents[i];
        }
        return result;
    }

    public IVector cross(IVector vector) {
        double[] thisComp = this.getComponents();
        double[] otherComp = vector.getComponents();
        double otherZ = (otherComp.length > 2) ? otherComp[2] : 0;

        double newX = thisComp[1] * otherZ - this.z * otherComp[1];
        double newY = this.z * otherComp[0] - thisComp[0] * otherZ;
        double newZ = thisComp[0] * otherComp[1] - thisComp[1] * otherComp[0];

        return new Vector3DDecorator(newX, newY, newZ);
    }

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
        double[] components = srcVector.getComponents();
        return Math.toDegrees(Math.atan2(components[1], components[0]));
    }
}