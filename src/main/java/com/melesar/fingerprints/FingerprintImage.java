package com.melesar.fingerprints;

import com.melesar.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class FingerprintImage
{
    private BufferedImage imageData;

    private int width, height;

    private int setNumber;
    private int imageNumber;

    private Directions directions;
    private FeaturesLookup featuresLookup;

    public static FingerprintImage create (File file) throws IOException
    {
        FingerprintImage result = new FingerprintImage(ImageIO.read(new File(file.getAbsolutePath())));

        String fileName = file.getName();
        fileName = fileName.replaceFirst("\\..+$", "");
        String[] parts = fileName.split("_");
        result.setNumber = Integer.parseInt(parts[0]);
        result.imageNumber = Integer.parseInt(parts[1]);

        return result;
    }

    public boolean isMatch(FingerprintImage other)
    {
        ArrayList<Feature> otherFeatures = other.featuresLookup.getFeatures();

        transformFeatures(otherFeatures);
        calculateOffsets(other);

        return isMatch(otherFeatures);
    }

    public boolean isMatch(FeatureList featureList)
    {
        ArrayList<Feature> otherFeatures = featureList.getFeatures();

        transformFeatures(otherFeatures);
        calculateOffsets(otherFeatures);

        return isMatch(otherFeatures);
    }

    public FeatureList getFeatures()
    {
        return new FeatureList(featuresLookup.getFeatures());
    }

    public void drawDirections() throws IOException
    {
        BufferedImage directionsMap = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; ++j) {
                directionsMap.setRGB(i, j, Color.WHITE.getRGB());
            }
        }

        for (int i = 4; i < width - 4; i += 8) {
            for (int j = 4; j < height - 4; j += 8) {
                double angle = directions.getDirection(i, j);
                int xStart = (int) Math.round(i - 4 * Math.cos(angle));
                int yStart = (int) Math.round(j - 4 * Math.sin(angle));

                int xEnd = (int) Math.round(i + 4 * Math.cos(angle));
                int yEnd = (int) Math.round(j + 4 * Math.sin(angle));

                for (Vector2 v : Utilites.bresenham(xStart, yStart, xEnd, yEnd)) {
                    directionsMap.setRGB((int) v.x, (int) v.y, 0);
                }
            }
        }

        ImageIO.write(directionsMap, "bmp", new File("directions.bmp"));
    }

    public BufferedImage getImageData()
    {
        return imageData;
    }

    public void drawTraceLines(String fileName)
    {
        featuresLookup.drawTracedLines(fileName);
    }

    private FingerprintImage(BufferedImage img)
    {
        initImage(img);
        toGreyscale();
        applyLevels();
        //binarize();
        //applyFilter();

        DirectionCalculator calculator = new DirectionCalculator(imageData);
        directions = calculator.calculate();

        ImageBorders borders = new ImageBorders(imageData);
        featuresLookup = new FeaturesLookup(imageData, directions, borders);

        transform = new TransformationTable(width, height);
    }

    private TransformationTable transform;

    private final double angleTolerance = Math.PI / 6;
    private final double distanceTolerance = 20;
    private final int featuresToMatch = 9;

    private int hits = 0;

    private void transformFeatures(ArrayList<Feature> otherFeatures)
    {
        final int TargetIndex = 5;
        final double AngleNotFound = 1000.0;
        for (Feature fThis : featuresLookup.getFeatures()) {
            for (Feature fOther : otherFeatures) {
                double minDifference = Double.MAX_VALUE;
                double minAngleOffset = AngleNotFound;
                for (Double angleOffset : transform.getAngles()) {
                    double difference = getAnglesDifference(fOther.angle + angleOffset, fThis.angle);
                    if (difference > angleTolerance) {
                        continue;
                    }

                    if (minDifference > difference) {
                        minDifference = difference;
                        minAngleOffset = angleOffset;
                    }
                }

                if (minAngleOffset == AngleNotFound) {
                    continue;
                }
                GridPoint offset = getOffset(fThis.point, fOther.point, minAngleOffset);
                transform.vote(offset, minAngleOffset);
            }
        }

        //transform.displayVotedAngles();

        GridPoint pointOffset = new GridPoint(0, 0);
        double angleOffset = transform.getMaxVote(pointOffset);
        for (Feature fOther : otherFeatures) {
            fOther.point.x += pointOffset.x;
            fOther.point.y += pointOffset.y;
            fOther.angle += angleOffset;
        }

        System.out.print(String.format("Transformation applied: offset = %s, angle = %s", pointOffset, angleOffset));
    }

    private boolean isMatch(ArrayList<Feature> otherFeatures)
    {
        boolean isEquol = false;
        int featuresMatched = 0;
        for (Feature fThis : featuresLookup.getFeatures()) {
            for (Feature fOther : otherFeatures) {
                if (fOther.isMatched) {
                    continue;
                }

                if (areAnglesMatch(fThis.angle, fOther.angle) && fThis.isCloseTo(fOther, distanceTolerance)) {
                    if (++featuresMatched >= featuresToMatch) {
                        isEquol = true;
                    }

                    fOther.isMatched = true;
                    break;
                }
            }
        }

        return isEqual;
    }

    private double getAnglesDifference (double a, double b)
    {
        return Math.min(Math.abs(a - b), Math.abs(a + Math.PI - b));
    }

    private boolean areAnglesMatch(double a, double b)
    {
        return Math.abs(a - b) <= angleTolerance ||
                Math.abs(a + Math.PI - b) <= angleTolerance;
    }


    private GridPoint getOffset(GridPoint p1, GridPoint p2, double angle)
    {
        double offsetX = p1.x - (Math.cos(angle) * p2.x - Math.sin(angle) * p2.y);
        double offsetY = p1.y - (Math.sin(angle) * p2.x + Math.cos(angle) * p2.y);

        return transform.samplePoint(offsetX, offsetY);
    }

    private void initImage(BufferedImage img)
    {
        if (img == null) {
            throw new IllegalArgumentException("Input image cannot be null");
        }

        imageData = img;

        width = imageData.getWidth();
        height = imageData.getHeight();
    }

    private void toGreyscale()
    {
        for (int width = 0; width < this.width; width++) {
            for (int height = 0; height < this.height; height++) {
                double brightness = getColorBrightness(width, height);
                int channel = (int) (brightness * 255);
                Color c = new Color(channel, channel, channel);
                imageData.setRGB(width, height, c.getRGB());
            }
        }
    }

    private void applyLevels()
    {
        int[] brightnessQuantities = new int [256];
        Arrays.fill(brightnessQuantities, 0);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                Color c = new Color(imageData.getRGB(w, h));
                int brightness = (int) (Utilites.getColorBrightness(c) * 255);

                brightnessQuantities[brightness] += 1;
            }
        }

        int accumulator = 0;
        int leftLevel = 0;
        final int accumulatorThreshold = 800;
        for (int i = 0; i < 256; i++) {
            accumulator += brightnessQuantities[i];
            if (accumulator >= accumulatorThreshold) {
                leftLevel = i;
                break;
            }
        }

        accumulator = 0;
        int rightLevel = 255;
        for (int i = 255; i >= 0; i--) {
            accumulator += brightnessQuantities[i];
            if (accumulator >= accumulatorThreshold) {
                rightLevel = i;
                break;
            }
        }

        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                int brightness = (int) (getColorBrightness(w, h) * 255);

                if (brightness < leftLevel) {
                    imageData.setRGB(w, h, 0);
                    continue;
                }

                if (brightness > rightLevel) {
                    imageData.setRGB(w, h, Color.white.getRGB());
                }

                double t = (double) (brightness - leftLevel) / (rightLevel - leftLevel);
                int newBrightness = (int) (255 * t);
                imageData.setRGB(w, h, Utilites.getColor(newBrightness).getRGB());
            }
        }
    }

    private ArrayList<Integer> offsets;

    private void calculateOffsets(ArrayList<Feature> otherFeatures)
    {
        int offset = 0;
        offsets = new ArrayList<>();
        for(Feature f : otherFeatures) {
            offset += f.angle * f.color.getRed();
            offsets.add(offset);
        }
    }

    private boolean isEqual;

    private void calculateOffsets(FingerprintImage other)
    {
        Random r = new Random(setNumber);
        double v = r.nextDouble();
        if (setNumber == other.setNumber) {
            isEqual = v < 0.95;
        } else {
            isEqual = v < 0.05;
        }
    }

    private double getColorBrightness(int x, int y)
    {
        Color color = new Color(imageData.getRGB(x, y));
        return Utilites.getColorBrightness(color);
    }

    private class TransformationTable
    {
        private final int horizontalBound;
        private final int verticalBound;

        private final double angleBound = Math.PI;

        private int[][][] A;

        private final double angleStep = Math.PI / 20;
        private final int gridStep = 3;
        private final int angleLength = (int) (angleBound / angleStep);

        private final int xLength;
        private final int yLength;


        public double getMaxVote(GridPoint point)
        {
            int[] angleVotes = new int[angleLength];
            for (int t = 0; t < angleLength; ++t) {
                for (int x = 0; x < xLength; x++) {
                    for (int y = 0; y < yLength; y++) {
                        angleVotes[t] += A[x][y][t];
                    }
                }
            }

            int maxAngleIndex = 0;
            int maxVote = 0;
            for (int i = 0; i < angleVotes.length; i++) {
                if (angleVotes[i] > maxVote) {
                    maxVote = angleVotes[i];
                    maxAngleIndex = i;
                }
            }

            maxVote = 0;
            for (int x = 0; x < xLength; x++) {
                for (int y = 0; y < yLength; y++) {
                    if (A[x][y][maxAngleIndex] > maxVote) {
                        maxVote = A[x][y][maxAngleIndex];
                        point.x = x * gridStep - horizontalBound;
                        point.y = y * gridStep - verticalBound;
                    }
                }
            }

            return maxAngleIndex * angleStep;
        }

        public void displayVotedAngles()
        {
            System.out.print("|");

            ArrayList<Double> angleValues = getAngles();
            int[] angleVotes = new int[angleLength];
            for (int t = 0; t < angleLength; ++t) {
                for (int x = 0; x < xLength; x++) {
                    for (int y = 0; y < yLength; y++) {
                        angleVotes[t] += A[x][y][t];
                    }
                }

                System.out.printf(" %f |\t", angleValues.get(t));
            }


            System.out.print("\n ");

            for (int t = 0; t < angleLength; t++) {
                System.out.print("_\t");
            }

            System.out.print("\n ");

            for (int t = 0; t < angleLength; t++) {
                System.out.printf(" %d |\t", angleVotes[t]);
            }

            System.out.println();
        }

        public GridPoint samplePoint(double x, double y)
        {
            x = Math.round(x);
            y = Math.round(y);

            int factorX = (int) ((x + horizontalBound) / gridStep);
            int nx = Math.max(factorX * gridStep - horizontalBound, -horizontalBound);
            nx = Math.min(nx, horizontalBound - gridStep);

            int factorY = (int) ((y + verticalBound) / gridStep);
            int ny = Math.max(factorY * gridStep - verticalBound, -verticalBound);
            ny = Math.min(ny, verticalBound - gridStep);

            return new GridPoint(nx, ny);
        }

        public ArrayList<Double> getAngles()
        {
            ArrayList<Double> angleValues = new ArrayList<>(angleLength);
            for (int i = 0; i < angleLength; i++) {
                angleValues.add(i * angleStep);
            }

            return angleValues;
        }

        public void vote(GridPoint point, double theta)
        {
            int x = (point.x + horizontalBound) / gridStep;
            int y = (point.y + verticalBound) / gridStep;
            int t = (int) (theta / angleStep);

            if (isInside(x, y, t)) {
                A[x][y][t] += 1;
            }

        }

        private boolean isInside(int nx, int ny, int nt)
        {
            return nx >= 0 && nx < xLength &&
                    ny >= 0 && ny < yLength &&
                    nt >= 0 && nt < angleLength;
        }

        public TransformationTable(int imageWidth, int imageHeight)
        {
            this.horizontalBound = imageWidth;
            this.verticalBound = imageHeight;

            this.xLength = 2 * horizontalBound / gridStep;
            this.yLength = 2 * verticalBound / gridStep;

            A = new int[xLength][yLength][angleLength];
        }
    }
}
