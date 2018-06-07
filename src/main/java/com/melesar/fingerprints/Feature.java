package com.melesar.fingerprints;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;

public class Feature implements Serializable
{
    public GridPoint point;
    public double angle;

    public transient Color color;
    public transient boolean isMatched;

    public boolean isCloseTo (Feature other, double tolerance)
    {
        return point.isCloseTo(other.point, tolerance);
    }

    public Feature(GridPoint point, double angle, Color color)
    {
        this.point = point;
        this.angle = angle;
        this.color = color;
        this.isMatched = false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return Double.compare(feature.angle, angle) == 0 &&
                Objects.equals(point, feature.point);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(point, angle);
    }
}
