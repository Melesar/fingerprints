package com.melesar.fingerprints;

import com.melesar.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Utilites
{
    static ArrayList<Vector2> bresenham(int x, int y, int x2, int y2)
    {
        int w = x2 - x;
        int h = y2 - y;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
        if (w < 0) { dx1 = -1; } else if (w > 0) { dx1 = 1; }
        if (h < 0) { dy1 = -1; } else if (h > 0) { dy1 = 1; }
        if (w < 0) { dx2 = -1; } else if (w > 0) { dx2 = 1; }

        int longest = Math.abs(w);
        int shortest = Math.abs(h);

        if (!(longest > shortest)) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h < 0) { dy2 = -1; } else if (h > 0) { dy2 = 1; }
            dx2 = 0;
        }

        ArrayList<Vector2> res = new ArrayList<>(longest + 1);
        int numerator = longest >> 1;
        for (int i = 0; i <= longest; i++) {
            res.add(new Vector2(x, y));
            numerator += shortest;
            if (!(numerator < longest)) {
                numerator -= longest;
                x += dx1;
                y += dy1;
            } else {
                x += dx2;
                y += dy2;
            }
        }

        return res;
    }

    private static final double brightnessMultiplier = (double) 1 / 3 / 255;

    public static double getColorBrightness(Color color)
    {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        return (double) (r + g + b) * brightnessMultiplier;
    }

    static Color getColor(int brightness)
    {
        return new Color(brightness, brightness, brightness);
    }
}
