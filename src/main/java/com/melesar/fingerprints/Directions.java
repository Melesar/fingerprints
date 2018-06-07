package com.melesar.fingerprints;

class Directions
{
    private double[][] angles;

    public void addDirection (int x, int y, double angle)
    {
        angles[x][y] = angle;
    }

    public double getDirection(int x, int y)
    {
        return angles[x][y];
    }

    Directions(int width, int height)
    {
        angles = new double[width][height];
    }
}
