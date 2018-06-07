package com.melesar;

import java.util.Objects;

public class Vector2 implements Comparable<Vector2>
{
    public double x, y;

    public Vector2()
    {
        x = y = 0;
    }

    public Vector2 normalized()
    {
        double magnitude = magnitude();
        return new Vector2(x / magnitude, y / magnitude);
    }

    public double magnitude()
    {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2 add (Vector2 other)
    {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 multiply(double value)
    {
        return new Vector2 (x * value, y * value);
    }

    public Vector2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return Double.compare(vector2.x, x) == 0 &&
                Double.compare(vector2.y, y) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(x, y);
    }

    @Override
    public String toString()
    {
        return String.format("(%s, %s)", x, y);
    }

    @Override
    public int compareTo(Vector2 o)
    {
        if (this.x > o.x) {
            return 1;
        } else {
            return Double.compare(this.y, o.y);
        }
    }
}
