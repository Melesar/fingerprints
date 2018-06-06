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
        BufferedImage figerprintImage = ImageIO.read(imageUrl);

        FingerprintImage originalImage = new FingerprintImage(figerprintImage);
        FingerprintImage compareImage = new FingerprintImage(figerprintImage);

        boolean isMatch = originalImage.isMatch(compareImage);

        System.out.println(isMatch);
    }
}