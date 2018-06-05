package fingerprints;

import data.Vector2;

import java.util.Map;
import java.util.TreeMap;

class Directions
{
    private final int width;
    private final int height;
    private double[][] angles;
    //private TreeMap<Vector2, Double> directionsMap;

    public void addDirection (int x, int y, double angle)
    {
        Vector2 vect = new Vector2(x, y);
        angles[x][y] = angle;
//        directionsMap.put(vect, angle);
    }

    public double getDirection(int x, int y)
    {
        return angles[x][y];
//        Vector2 vector = new Vector2(x, y);
//        Vector2 previousVector = new Vector2();
//        Vector2 previousVector = directionsMap.firstKey();
//        for(Map.Entry<Vector2, Double> entry : directionsMap.entrySet()) {
//            if (vector.compareTo(previousVector) <= 0) {
//                return directionsMap.get(previousVector);
//            }
//
//            previousVector = entry.getKey();
//        }
//
//        return directionsMap.lastEntry().getValue();
    }

    Directions(int width, int height)
    {
        this.width = width;
        this.height = height;
        angles = new double[width][height];
//        directionsMap = new TreeMap<>();
    }
}
