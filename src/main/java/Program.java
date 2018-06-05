import data.Vector2;
import fingerprints.FingerprintImage;
import fingerprints.Utilites;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Program
{
    public static void main(String[] args) throws IOException
    {
        URL imageUrl = Program.class.getClassLoader().getResource("fingerprint.jpg");
        BufferedImage originalImage = ImageIO.read(imageUrl);

        FingerprintImage fingerprintImage = new FingerprintImage(originalImage);
    }
}