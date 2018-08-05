package com.melesar.fingerprints;

import com.melesar.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;

class DirectionCalculator
{
    private BufferedImage imageData;

    private final int Alpha = 10;

    public Directions calculate()
    {
        int width = imageData.getWidth();
        int height = imageData.getHeight();

        Directions directionMap = new Directions(width, height);
        for (int i = 1; i < width - Alpha - 1; i += Alpha) {
            for (int j = 1; j < height - Alpha - 1; j += Alpha) {
                calculateDirectionWindow(i, j, directionMap);
            }
        }

        return directionMap;
    }

    private void calculateDirectionWindow (int startX, int startY, Directions directionMap)
    {
        double A = 0, B = 0, C = 0;
        for (int x = startX; x < startX + Alpha; x++) {
            for (int y = startY; y < startY + Alpha; y++) {
                double a1 = getColorBrightness(x + 1, y + 1);
                double a2 = getColorBrightness(x - 1, y + 1);
                double a3 = getColorBrightness(x - 1, y - 1);
                double a4 = getColorBrightness(x + 1, y - 1);

                double a = (-a1 + a2 + a3 - a4) * 0.25;
                double b = (-a1 - a2 + a3 + a4) * 0.25;

                A += a * a;
                B += b * b;
                C += a * b;
            }
        }

        Vector2 t = new Vector2();
        if (C != 0) {
            double v = (B - A) / (2 * C);

            t.y = v - Math.signum(C) * Math.sqrt(v * v + 1);
            t.x = 1;
        } else if (A <= B) {
            t.x = 1;
        } else if (A > B) {
            t.y = 1;
        }

        double angle = t.x != 0 ? Math.atan(t.y / t.x) : Math.PI * 0.5;

        for (int x = startX; x < startX + Alpha; x++) {
            for (int y = startY; y < startY + Alpha; y++) {
                directionMap.addDirection(x, y, angle);
            }
        }
    }

    private double getColorBrightness(int x, int y)
    {
        Color color = new Color(imageData.getRGB(x, y));
        return (double) color.getRed() / 255;
    }

    DirectionCalculator(BufferedImage imageData)
    {
        this.imageData = imageData;
    }
}
