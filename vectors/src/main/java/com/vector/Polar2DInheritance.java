package com.vector;

import com.vector.interfaces.IPolar2D;

public class Polar2DInheritance extends Vector2D implements IPolar2D {

    public Polar2DInheritance(double x, double y) {
        super(x, y);
    }

    @Override
    public double getAngle() {
        return Math.toDegrees(Math.atan2(y, x));
    }

    @Override
    public double getAbs() {
        return super.abs();
    }
}