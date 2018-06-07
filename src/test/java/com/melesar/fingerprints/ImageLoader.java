package com.melesar.fingerprints;

import org.junit.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageLoader
{
    public static BufferedImage getFingerprintImage()
    {
        try {
            URL imageUrl = ImageLoader.class.getClassLoader().getResource("test.png");
            return ImageIO.read(imageUrl);
        } catch (IllegalArgumentException e) {
            Assert.fail("Failed to load test image");
        } catch (IOException e) {
            Assert.fail("Failed to load test image");
        }

        return null;
    }
}
