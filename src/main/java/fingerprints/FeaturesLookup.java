package fingerprints;

import data.Vector2;

import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class FeaturesLookup
{
    private class GridPoint
    {
        int x, y;
        boolean isVisited;

        public GridPoint(int x, int y)
        {
            this.x = x;
            this.y = y;

            isVisited = false;
        }
    }

    private final BufferedImage img;
    private final BufferedImage debugImage;
    private final Directions directionsMap;
    private final ImageBorders borders;

    private int imageWidth;
    private int imageHeight;

    private int gridWidth, gridHeight;

    private GridPoint[] grid;
    private boolean[][] visitedPoints;

    private final int GridStep = 15;

    FeaturesLookup(BufferedImage img, Directions directionsMap, ImageBorders borders)
    {
        this.img = img;
        imageWidth = img.getWidth();
        imageHeight = img.getHeight();
        visitedPoints = new boolean[imageWidth][imageHeight];

        this.borders = borders;
        this.debugImage = createDebugImage();
        this.directionsMap = directionsMap;

        createGrid();
        traceLines();
    }

    public void showGrid()
    {
        ColorModel cm = img.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = img.copyData(null);
        BufferedImage gridImage = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        for (GridPoint p : grid) {
            gridImage.setRGB(p.x, p.y, Color.CYAN.getRGB());
        }

        try {
            ImageIO.write(gridImage, "bmp", new File("grid.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGrid()
    {
        gridWidth = imageWidth / GridStep;
        gridHeight = imageHeight / GridStep;

        grid = new GridPoint[gridWidth * gridHeight];

        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                GridPoint p = new GridPoint(i * GridStep, j * GridStep);
                grid[i * gridHeight + j] = p;
            }
        }
    }



    private void traceLines ()
    {
        GridPoint point = findStartingPoint();
        while (point != null) {
            traceLine(point, 0);
            traceLine(point, Math.PI);

            point = findStartingPoint();
        }

        try {
            ImageIO.write(debugImage, "bmp", new File("trace.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Queue<Double> tracingAngles = new ArrayDeque<>();
    private final int MaxAngles = 3;

    private void traceLine(GridPoint start, double angleOffset)
    {
        final int traceStep = 5;
        double angle = angleOffset + directionsMap.getDirection(start.x, start.y);

        GridPoint sectionStart = start;
        while (true) {
            GridPoint end = trace(sectionStart, angle, traceStep);

            if (end == null) {
                break;
            }

            addAngle(angle);
            end = getSectionMinimum(end);
            Vector2[] path = Utilites.bresenham(sectionStart.x, sectionStart.y, end.x, end.y);

            if (shouldStop(path)) {
                break;
            }

            sectionStart = end;
            angle = angleOffset + directionsMap.getDirection(sectionStart.x, sectionStart.y);

            for(Vector2 p : path) {
                visitedPoints[(int)p.x][(int)p.y] = true;
            }

            for (int i = 0; i < path.length; i++) {
                Vector2 p = path[i];
                Color color = i > 0 ? Color.CYAN : Color.RED;
                debugImage.setRGB((int) p.x, (int) p.y, color.getRGB());
            }
        }
    }

    private boolean shouldStop(Vector2[] path)
    {
        Vector2 endPoint = path[path.length - 1];
        if (!borders.isInside((int) endPoint.x, (int) endPoint.y)) {
            return true;
        }

        double pathBrightness = 0;
        for (Vector2 p : path) {
            GridPoint point = new GridPoint((int) p.x, (int) p.y);
            Color color = new Color(img.getRGB(point.x, point.y));
            pathBrightness += Utilites.getColorBrightness(color);
        }

        if (pathBrightness / path.length > 0.8) {
            return true;
        }

        double angle = directionsMap.getDirection((int) endPoint.x, (int) endPoint.y);
        if (!compareAngle(angle)) {
            return true;
        }

        GridPoint end = new GridPoint((int) endPoint.x, (int) endPoint.y);
        if (visitedPoints[end.x][end.y]) {
            return true;
        }

        return false;
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
        final int sectionHalfLength = 8;
        double sectionDirection = directionsMap.getDirection(center.x, center.y) + Math.PI / 2;
        GridPoint sectionStart = trace(center, sectionDirection, -sectionHalfLength);
        GridPoint sectionEnd = trace(center, sectionDirection, sectionHalfLength);

        if (sectionStart == null || sectionEnd == null) {
            return center;
        }

        Vector2[] section = Utilites.bresenham(sectionStart.x, sectionStart.y, sectionEnd.x, sectionEnd.y);

        return getSectionMinimum(section, sectionDirection);
    }

    private GridPoint getSectionMinimum (Vector2[] section, double direction)
    {
        final int parallelSections = 1;
        double[] brightnessArray = new double[section.length];
        applyParallelSections(section, direction, parallelSections, brightnessArray);

        final int p = 3;
        applyWeightedMask(brightnessArray, p);

        GridPoint min = new GridPoint(0, 0);
        double minBrightness = brightnessArray[p];
        for (int i = p ; i < brightnessArray.length - p; i++) {
            double brightness = brightnessArray[i];
            if (brightness <= minBrightness) {
                minBrightness = brightness;
                min.x = (int) section[i].x;
                min.y = (int) section[i].y;
            }
        }

        return min;
    }

    private void applyParallelSections(Vector2[] section, double direction, int parallelSections, double[] brightnessArray)
    {
        for (int sectionIndex = 0; sectionIndex < section.length; sectionIndex++) {
            Vector2 p = section[sectionIndex];
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
        double angleSum = 0;
        for (double a : tracingAngles) {
            angleSum += a;
        }

        double averageAngle = angleSum / MaxAngles;
        final double angleThreshold = Math.PI / 2.5;
//
        return Math.abs(averageAngle - angle) < angleThreshold;
//        return Math.abs(tracingAngles.peek() - angle) < angleThreshold;
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
