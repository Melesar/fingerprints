package com.melesar.fingerprints;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageBorders
{
    private final double brightnessThreshold = 0.9;

    private int[] leftBorder;
    private int[] rightBorder;

    private int width, height;

    public boolean isInside (int x, int y)
    {
        return leftBorder[y] <= x && rightBorder[y] >= x;
    }

    public boolean isCloseToBorder (int x, int y, double tolerance)
    {
        return x - leftBorder[y] <= tolerance ||
                rightBorder[y] - x <= tolerance;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public ImageBorders(BufferedImage image)
    {
        leftBorder = new int[image.getHeight()];
        rightBorder = new int[image.getHeight()];

        calculateBorder(image);
    }

    private void calculateBorder(BufferedImage image)
    {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        boolean isMinYSet = false;
        int minX = imageWidth, minY = imageHeight, maxX = 0, maxY = 0;
        for (int row = 0; row < imageHeight; row++) {
            leftBorder[row] = imageWidth / 2;
            rightBorder[row] = imageWidth / 2;

            boolean isDarkPixelFound = false;
            for (int column = 0; column < imageWidth / 2; column++) {
                if (getColorBrightness(image, column, row) >= brightnessThreshold) {
                    continue;
                }

                leftBorder[row] = column;

                if (column < minX) {
                    minX = column;
                }

                isDarkPixelFound = true;
                break;
            }

            for (int column = imageWidth - 1; column >= imageWidth / 2; column--) {
                if (getColorBrightness(image, column, row) >= brightnessThreshold) {
                    continue;
                }

                rightBorder[row] = column;

                if (column > maxX) {
                    maxX = column;
                }

                isDarkPixelFound = true;
                break;
            }

            if (!isDarkPixelFound && !isMinYSet) {
                minY = row;
            } else if (!isDarkPixelFound && isMinYSet) {
                maxY = row;
            } else if (isDarkPixelFound && !isMinYSet) {
                isMinYSet = true;
            }
        }

        width = maxX - minX;
        height = maxY - minY;
    }

    private double getColorBrightness(BufferedImage imageData, int x, int y)
    {
        Color color = new Color(imageData.getRGB(x, y));
        return Utilites.getColorBrightness(color);
    }
}
