package com.melesar.fingerprints;

import com.melesar.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class FeaturesLookup
{
    private final BufferedImage img;
    private final BufferedImage debugImage;
    private final Directions directionsMap;
    private final ImageBorders borders;

    private int imageWidth;
    private int imageHeight;

    private GridPoint[] grid;
    private boolean[][] visitedPoints;
    private ArrayList<Feature> features;
    private ArrayList<Feature> newFeatures;

    private boolean debugMode = true;
    private boolean showTracedLines = true;
    private boolean showFeatures = false;

    public ArrayList<Feature> getFeatures()
    {
        return new ArrayList<>(features);
    }

    FeaturesLookup(BufferedImage img, Directions directionsMap, ImageBorders borders)
    {
        this.img = img;
        imageWidth = img.getWidth();
        imageHeight = img.getHeight();

        visitedPoints = new boolean[imageWidth][imageHeight];
        features = new ArrayList<>();
        newFeatures = new ArrayList<>();

        this.borders = borders;
        this.debugImage = createDebugImage();
        this.directionsMap = directionsMap;

        createGrid();
        traceLines();
    }

    private void createGrid()
    {
//        final int gridStep = 15;
        final int gridStep = 8;

        int gridWidth = imageWidth / gridStep;
        int gridHeight = imageHeight / gridStep;

        grid = new GridPoint[gridWidth * gridHeight];

        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                GridPoint p = new GridPoint(i * gridStep, j * gridStep);
                grid[i * gridHeight + j] = p;
            }
        }
    }

    private void traceLines ()
    {
        GridPoint point = findStartingPoint();
        while (point != null) {
            ArrayList<Vector2> currentPath = new ArrayList<>();
            traceLine(point, 0, currentPath);
            traceLine(point, Math.PI, currentPath);

            markPathVisited(currentPath);
            point = findStartingPoint();
        }
    }

    public void drawTracedLines(String fileName)
    {
        if (!debugMode) {
            return;
        }

        try {
            ImageIO.write(debugImage, "bmp", new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Queue<Double> tracingAngles = new ArrayDeque<>();
    private final int MaxAngles = 3;

    private void traceLine(GridPoint start, double angleOffset, ArrayList<Vector2> currentPath)
    {
        final int traceStep = 2;
        double angle = angleOffset + directionsMap.getDirection(start.x, start.y);
        tracingAngles.clear();

        GridPoint sectionStart = start;
        boolean addNewFeatures = false;
        while (true) {
            GridPoint end = trace(sectionStart, angle, traceStep);

            if (end == null) {
                break;
            }

            addAngle(angle);
            end = getSectionMinimum(end);
            ArrayList<Vector2> newPathStep = Utilites.bresenham(sectionStart.x, sectionStart.y, end.x, end.y);

            if (shouldStop(newPathStep, currentPath)) {
                break;
            }

            currentPath.addAll(newPathStep);

            for (Vector2 p : newPathStep) {
                visitedPoints[(int) p.x][(int) p.y] = true;
            }

            addNewFeatures = true;
            sectionStart = end;
            angle = angleOffset + directionsMap.getDirection(sectionStart.x, sectionStart.y);

            if (!debugMode || !showTracedLines) {
                continue;
            }

            for (int i = 0; i < newPathStep.size(); i++) {
                Vector2 p = newPathStep.get(i);
                Color color = i > 0 ? Color.CYAN : Color.RED;
                debugImage.setRGB((int) p.x, (int) p.y, color.getRGB());
            }
        }

        if (addNewFeatures) {
            features.addAll(newFeatures);
            showFeatures(newFeatures);
        }

        newFeatures.clear();
    }

    private boolean shouldStop(ArrayList<Vector2> pathStep, ArrayList<Vector2> fullPath)
    {
        Vector2 endPoint = pathStep.get(pathStep.size() - 1);
        GridPoint end = new GridPoint((int) endPoint.x, (int) endPoint.y);

        if (!borders.isInside(end.x, end.y)) {
            return true;
        }

        double pathStepBrightness = getPathBrightness(pathStep);
        double fullPathBrightness = getPathBrightness(fullPath);
        if (pathStepBrightness > 0.9) {
        //if (pathStepBrightness - fullPathBrightness > 0.08) {
            registerFeature(end, Color.GREEN);
            return true;
        }

        /*double angle = directionsMap.getDirection(end.x, end.y);
        if (!compareAngle(angle)) {
//            registerFeature(end, Color.BLUE);
            return true;
        }*/


        if (visitedPoints[end.x][end.y]) {
            registerFeature(end, Color.RED);
            return true;
        }

        return false;
    }

    private double getPathBrightness(ArrayList<Vector2> path)
    {
        if (path.size() == 0) {
            return 1;
        }

        double pathBrightness = 0;
        for (Vector2 p : path) {
            GridPoint point = new GridPoint((int) p.x, (int) p.y);
            Color color = new Color(img.getRGB(point.x, point.y));
            pathBrightness += Utilites.getColorBrightness(color);
        }
        return pathBrightness / path.size();
    }

    private int startPointIndex = 0;

    private GridPoint findStartingPoint()
    {
        for (; startPointIndex < grid.length; ++startPointIndex) {
            GridPoint p = grid[startPointIndex];
            if (visitedPoints[p.x][p.y] || !borders.isInside(p.x, p.y) ||
                    Utilites.getColorBrightness(new Color(img.getRGB(p.x, p.y))) > 0.9) {
                continue;
            }

            p = getSectionMinimum(p);
            if (!visitedPoints[p.x][p.y] && borders.isInside(p.x, p.y)) {
                visitedPoints[p.x][p.y] = true;
                return p;
            }
        }

        return null;
    }

    private GridPoint getSectionMinimum(GridPoint center)
    {
        final int sectionHalfLength = 5;
        double sectionDirection = directionsMap.getDirection(center.x, center.y) + Math.PI / 2;
        GridPoint sectionStart = trace(center, sectionDirection, -sectionHalfLength);
        GridPoint sectionEnd = trace(center, sectionDirection, sectionHalfLength);

        if (sectionStart == null || sectionEnd == null) {
            return center;
        }

        ArrayList<Vector2> section = Utilites.bresenham(sectionStart.x, sectionStart.y, sectionEnd.x, sectionEnd.y);

        return getSectionMinimum(section, sectionDirection);
    }

    private GridPoint getSectionMinimum (ArrayList<Vector2> section, double direction)
    {
        final int parallelSections = 1;
        double[] brightnessArray = new double[section.size()];
        applyParallelSections(section, direction, parallelSections, brightnessArray);

        final int p = 2;
        applyWeightedMask(brightnessArray, p);

        GridPoint min = new GridPoint(0, 0);
        double minBrightness = brightnessArray[p];
        for (int i = p ; i < brightnessArray.length - p; i++) {
            double brightness = brightnessArray[i];
            if (brightness <= minBrightness) {
                minBrightness = brightness;
                min.x = (int) section.get(i).x;
                min.y = (int) section.get(i).y;
            }
        }

        return min;
    }

    private void applyParallelSections(ArrayList<Vector2> section, double direction, int parallelSections, double[] brightnessArray)
    {
        for (int sectionIndex = 0; sectionIndex < section.size(); sectionIndex++) {
            Vector2 p = section.get(sectionIndex);
            GridPoint current = new GridPoint((int) p.x, (int) p.y);
            int averageBrightness = 0;
            for (int i = 0; i < parallelSections; ++i) {
                int offset = i + 2;
                GridPoint forward = trace(current, direction, offset);
                GridPoint backward = trace(current, direction, -offset);

                if (forward != null) {
                    averageBrightness += new Color(img.getRGB(forward.x, forward.y)).getRed();
                }

                if (backward != null) {
                    averageBrightness += new Color(img.getRGB(backward.x, backward.y)).getRed();
                }
            }

            averageBrightness += new Color(img.getRGB(current.x, current.y)).getRed();
            averageBrightness /= 2 * parallelSections + 1;

            brightnessArray[sectionIndex] = averageBrightness;
        }
    }

    private final double[] weightedMask = {7.0/29, 5.0/29, 2.0/29, 1.0/29, 2.0/29, 5.0/29, 7.0/29};

    private void applyWeightedMask(double[] brightnessArray, int p)
    {
        double[] valuesChanged = new double[brightnessArray.length - 2*p];

        double factor = 1.0 / (2*p + 1);
        for (int k = p; k < brightnessArray.length - p; k++) {
            for (int v = -p; v <= p; v++) {
                valuesChanged[k - p] += weightedMask[p + v] * brightnessArray[k + v];
            }
            valuesChanged[k - p] *= factor;
        }

        for (int k = p; k < brightnessArray.length - p; k++) {
            brightnessArray[k] = valuesChanged[k - p];
        }
    }

    private void markPathVisited(Iterable<Vector2> path)
    {
        final int range = 3;
        for(Vector2 p : path) {
            GridPoint point = new GridPoint((int)p.x, (int)p.y);

            for (int i = -range; i < range; i++) {
                for (int j = -range; j < range; j++) {
                    int x = point.x + i;
                    int y = point.y + j;

                    if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
                        visitedPoints[x][y] = true;
                    }
                }
            }
        }
    }

    private void registerFeature(GridPoint point, Color color)
    {
        final double distanceTolerance = 15;

//        double featureAngle = directionsMap.getDirection(point.x, point.y);
//        featureAngle += featureAngle < 0 ? 2 * Math.PI : 0;
//        Feature newFeature = new Feature(point, featureAngle, color);

        Feature newFeature = new Feature(point, directionsMap.getDirection(point.x, point.y), color);

        if (borders.isCloseToBorder(newFeature.point.x, newFeature.point.y, distanceTolerance)) {
            return;
        }

        for (int i = 0; i < features.size(); i++) {
            if (features.get(i).isCloseTo(newFeature, distanceTolerance)) {
                features.remove(i);
                return;
            }
        }

        newFeatures.add(newFeature);
    }

    private void showFeatures(ArrayList<Feature> features)
    {
        if (!debugMode || !showFeatures) {
            return;
        }

        for (Feature f : features) {
            GridPoint point = f.point;
            for (int i = -3; i <= 3; i++) {
                setColor(point.x + i, point.y + 3, f.color);
                setColor(point.x + i, point.y - 3, f.color);
            }

            for (int i = -3; i <= 3; i++) {
                setColor(point.x + 3, point.y + i, f.color);
                setColor(point.x - 3, point.y + i, f.color);
            }
        }
    }

    private void setColor(int x, int y, Color color)
    {
        if (x < 0 || x >= debugImage.getWidth() || y < 0 || y >= debugImage.getHeight()) {
            return;
        }

        debugImage.setRGB(x, y, color.getRGB());
    }

    private GridPoint trace (GridPoint start, double angle, int step)
    {
        int x = (int) (start.x + step * Math.cos(angle));
        int y = (int) (start.y + step * Math.sin(angle));

        if (x < 0 || x >= imageWidth || y < 0 || y >= imageHeight) {
            return null;
        }

        return new GridPoint(x, y);
    }

    private void addAngle(double angle)
    {
        if (tracingAngles.size() == MaxAngles) {
            tracingAngles.poll();
        }

        tracingAngles.offer(angle);
    }

    private boolean compareAngle(double angle)
    {
//        final double angleThreshold = Math.PI / 5;
//        double lastAngle = tracingAngles.peek();
//        return Math.abs(lastAngle - angle) <= angleThreshold ||
//                Math.abs(lastAngle - angle + Math.PI) <= angleThreshold;
        if (tracingAngles.size() < MaxAngles) {
            return true;
        }

        double angleSum = 0;
        for (double a : tracingAngles) {
            angleSum += a;
        }

        double averageAngle = angleSum / MaxAngles;
        final double angleThreshold = Math.PI / 5;

        return Math.abs(averageAngle - angle) <= angleThreshold ||
                Math.abs(averageAngle - angle + Math.PI) <= angleThreshold;
    }

    private BufferedImage createDebugImage()
    {
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; ++j) {
                image.setRGB(i, j, img.getRGB(i, j));
            }
        }

        return image;
    }
}
