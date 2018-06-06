package fingerprints;

import data.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FingerprintImage
{
    private BufferedImage imageData;

    private int width, height;

    private Directions directions;
    private FeaturesLookup featuresLookup;

    public Color getColor (int x, int y)
    {
        int rgb = imageData.getRGB(x, y);
        return new Color(rgb);
    }

    public boolean isMatch(FingerprintImage other)
    {
        ArrayList<Feature> otherFeatures = other.featuresLookup.getFeatures();

        transformFeatures(otherFeatures);

        return isMatch(otherFeatures);
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
                    directionsMap.setRGB((int)v.x, (int)v.y, 0);
                }
            }
        }

        ImageIO.write(directionsMap, "bmp", new File("directions.bmp"));
    }

    public FingerprintImage (BufferedImage img)
    {
        initImage(img);
        toGreyscale();
        applyFilter();

        DirectionCalculator calculator = new DirectionCalculator(imageData);
        directions = calculator.calculate();

        ImageBorders borders = new ImageBorders(imageData);
        featuresLookup = new FeaturesLookup(img, directions, borders);

        transform = new TransformationTable(width, height);
    }

    private TransformationTable transform;

    private final double angleTolerance = Math.PI / 10;
    private final double distanceTolerance = 1.2;

    private void transformFeatures(ArrayList<Feature> otherFeatures)
    {
        for(Feature fThis : featuresLookup.getFeatures()) {
            for(Feature fOther : otherFeatures) {
                for (Double angleOffset : transform.getAngles()) {
                    if (!areAnglesMatch(fOther.angle + angleOffset, fThis.angle)) {
                        continue;
                    }

                    GridPoint offset = getOffset(fThis.point, fOther.point, angleOffset);
                    transform.vote(offset, angleOffset);
                }
            }
        }

        GridPoint pointOffset = transform.getMaxPoint();
        double angleOffset = transform.getMaxTheta();
        for(Feature fOther : otherFeatures) {
            fOther.point.x += pointOffset.x;
            fOther.point.y += pointOffset.y;
            fOther.angle += angleOffset;
        }

        System.out.println (String.format("Transformation applied: offset = %s, angle = %s", pointOffset, angleOffset));
    }

    private boolean isMatch(ArrayList<Feature> otherFeatures)
    {
        int featuresMatched = 0;
        for(Feature fThis : featuresLookup.getFeatures()) {
            for(Feature fOther : otherFeatures) {
                if (fOther.isMatched) {
                    continue;
                }

                if (areAnglesMatch(fThis.angle, fOther.angle) && fThis.isCloseTo(fOther, distanceTolerance)) {
                    if (++featuresMatched >= 12) {
                        return true;
                    }

                    fOther.isMatched = true;
                    break;
                }
            }
        }

        System.out.println(String.format("Match failed. Features matched: %d", featuresMatched));

        return false;
    }

    private boolean areAnglesMatch(double a, double b)
    {
        return Math.abs(a - b) <= angleTolerance ||
                Math.abs(a + Math.PI - b) <= angleTolerance;
    }


    private GridPoint getOffset (GridPoint p1, GridPoint p2, double angle)
    {
        double offsetX = p1.x - (Math.cos(angle) * p2.x - Math.sin(angle) * p2.y);
        double offsetY = p1.y - (Math.sin(angle) * p2.x + Math.cos(angle) * p2.y);

        return transform.samplePoint(offsetX, offsetY);
    }

    private void initImage(BufferedImage img)
    {
        if (img == null) {
            throw new IllegalArgumentException();
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
                Color c = new Color (channel, channel, channel);
                imageData.setRGB(width, height, c.getRGB());
            }
        }
    }

    private void applyFilter()
    {
        double[][] buffer = new double[width - 2][height - 2];
        double[] window = new double[9];

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                window[0] = getColorBrightness(x - 1, y - 1);
                window[1] = getColorBrightness(x - 1, y);
                window[2] = getColorBrightness(x - 1, y + 1);
                window[3] = getColorBrightness(x , y - 1);
                window[4] = getColorBrightness(x , y );
                window[5] = getColorBrightness(x , y + 1);
                window[6] = getColorBrightness(x + 1, y - 1);
                window[7] = getColorBrightness(x + 1, y);
                window[8] = getColorBrightness(x + 1, y + 1);

                Arrays.sort(window);

                buffer[x - 1][y - 1] = window[4];
            }
        }

        for (int x = 0; x < width - 2; x++) {
            for (int y = 0; y < height - 2; y++) {
                int colorValue = (int) (buffer[x][y] * 255);
                Color c = new Color(colorValue, colorValue, colorValue);

                imageData.setRGB(x + 1, y + 1, c.getRGB());
            }
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

        private HashMap<GridPoint, Integer> points;
        private int[] angleVotes;

        private final double angleStep = Math.PI / 12;
        private final int gridStep = 15;

        public GridPoint getMaxPoint()
        {
            int maxVotes = 0;
            GridPoint votedPoint = new GridPoint(0, 0);

            for(Map.Entry<GridPoint, Integer> entry : points.entrySet()) {
                if (maxVotes < entry.getValue()) {
                    votedPoint = entry.getKey();
                    maxVotes = entry.getValue();
                }
            }

            return votedPoint;
        }

        public double getMaxTheta()
        {
            int maxVotes = 0;
            double votedAngle = 0;

            for (int i = 0; i < angleVotes.length; i++) {
                int votes = angleVotes[i];
                if (maxVotes < votes) {
                    votedAngle = i * angleStep;
                    maxVotes = votes;
                }
            }

            return votedAngle;
        }

        public GridPoint samplePoint (double x, double y)
        {
            x = Math.round(x);
            y = Math.round(y);

            double minDistance = Double.MAX_VALUE;
            GridPoint result = new GridPoint(0, 0);
            for(GridPoint p : points.keySet()) {
                double distance = Math.abs(x - p.x) + Math.abs(y - p.y);
                if (distance < minDistance) {
                    minDistance = distance;
                    result = p;
                }
            }

            return result;
        }

        public Iterable<Double> getAngles()
        {
            ArrayList<Double> angleValues = new ArrayList<>(angleVotes.length);
            for (int i = 0; i < angleValues.size(); i++) {
                angleValues.set(i, i * angleStep);
            }

            return angleValues;
        }

        public void vote (GridPoint point, double theta)
        {
            points.put(point, points.get(point) + 1);

            GridPoint neighbour = new GridPoint(0, 0);
            for (int i = -1; i <= 1; i++) {
                neighbour.x = point.x + i * gridStep;
                for (int j = -1; j <= 1; j++) {
                    neighbour.y = point.y + j * gridStep;

                    if (neighbour.x >= -horizontalBound &&
                            neighbour.x <= horizontalBound &&
                            neighbour.y >= -verticalBound &&
                            neighbour.y <= verticalBound) {
                        points.put(neighbour, points.get(neighbour) + 1);
                    }
                }


                int angleIndex = (int) (theta / angleStep);
                angleVotes[angleIndex] += 1;

                double neighbourIndex = angleIndex + i;
                if (neighbourIndex >= 0 && neighbourIndex < angleVotes.length) {
                    angleVotes[angleIndex] += 1;
                }
            }
        }

        public TransformationTable(int imageWidth, int imageHeight)
        {
            this.horizontalBound = imageWidth / 2;
            this.verticalBound = imageHeight / 2;

            points = new HashMap<>();
            for (int x = -horizontalBound; x <= horizontalBound; x += gridStep) {
                for (int y = -verticalBound; y < verticalBound; y += gridStep) {
                    points.put(new GridPoint(x, y), 0);
                }
            }

            angleVotes = new int[(int) (angleBound / angleStep)];
        }
    }
}
