package com.vector;

import com.vector.interfaces.IPolar2D;
import com.vector.interfaces.IVector;

public class Polar2DAdapter implements IVector, IPolar2D {
    private final Vector2D srcVector;

    public Polar2DAdapter(Vector2D srcVector) {
        this.srcVector = srcVector;
    }

    @Override
    public double getAngle() {
        return Math.toDegrees(Math.atan2(srcVector.getY(), srcVector.getX()));
    }

    @Override
    public double getAbs() {
        return srcVector.abs();
    }

    @Override
    public double abs() {
        return srcVector.abs();
    }

    @Override
    public double cdot(IVector vector) {
        return srcVector.cdot(vector);
    }

    @Override
    public double[] getComponents() {
        return srcVector.getComponents();
    }
}