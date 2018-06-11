package com.melesar.fingerprints;

import java.io.Serializable;
import java.util.Objects;

public class GridPoint implements Serializable
{
    public int x, y;
    transient boolean isVisited;

    public GridPoint(int x, int y)
    {
        this.x = x;
        this.y = y;

        isVisited = false;
    }

    public boolean isCloseTo (GridPoint other, double tolerance)
    {
        double x = this.x - other.x;
        double y = this.y - other.y;
        double distance = Math.sqrt(x * x + y * y);

        return distance <= tolerance;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridPoint point = (GridPoint) o;
        return x == point.x &&
                y == point.y;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }

    @Override
    public String toString()
    {
        return String.format("(%d, %d)", x, y);
    }
}
