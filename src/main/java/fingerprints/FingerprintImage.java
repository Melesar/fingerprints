package fingerprints;

import data.Vector2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FingerprintImage
{
    private BufferedImage imageData;

    private int width, height;

    private Directions directions;
    private DirectionCalculator calculator;
    private FeaturesLookup featuresLookup;

    public Color getColor (int x, int y)
    {
        int rgb = imageData.getRGB(x, y);
        return new Color(rgb);
    }

    public double getDirection(int x, int y)
    {
        return directions.getDirection(x, y);
    }

    public void drawGrid()
    {
        featuresLookup.showGrid();
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

        calculator = new DirectionCalculator(imageData);
        directions = calculator.calculate();

        ImageBorders borders = new ImageBorders(imageData);
        featuresLookup = new FeaturesLookup(img, directions, borders);
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

    private double getColorBrightness(int x, int y)
    {
        Color color = new Color(imageData.getRGB(x, y));
        return Utilites.getColorBrightness(color);
    }
}
