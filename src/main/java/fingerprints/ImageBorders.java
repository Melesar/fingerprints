package fingerprints;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageBorders
{
    private final double brightnessThreshold = 0.9;

    private int[] leftBorder;
    private int[] rightBorder;

    public boolean isInside (int x, int y)
    {
        return leftBorder[y] <= x && rightBorder[y] >= x;
    }

    public ImageBorders(BufferedImage image)
    {
        leftBorder = new int[image.getHeight()];
        rightBorder = new int[image.getHeight()];

        calculateBorder(image);
    }

    private void calculateBorder(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int row = 0; row < height; row++) {
            leftBorder[row] = width / 2;
            rightBorder[row] = width / 2;

            for (int column = 0; column < width / 2; column++) {
                if (getColorBrightness(image, column, row) < brightnessThreshold) {
                    leftBorder[row] = column;
                    break;
                }
            }

            for (int column = width - 1; column >= width / 2; column--) {
                if (getColorBrightness(image, column, row) < brightnessThreshold) {
                    rightBorder[row] = column;
                    break;
                }
            }
        }
    }

    private double getColorBrightness(BufferedImage imageData, int x, int y)
    {
        Color color = new Color(imageData.getRGB(x, y));
        return Utilites.getColorBrightness(color);
    }
}
