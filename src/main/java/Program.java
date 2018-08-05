import com.melesar.fingerprints.FingerprintImage;
import com.melesar.fingerprints.Utilites;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class Program
{
    public static void main(String[] args) throws IOException
    {
        InputStream in = Program.class.getResourceAsStream("");
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        String resource;

        while ((resource = bf.readLine()) != null) {
            System.out.println(resource);
        }

        //compareFingerprints();
    }

    private static void histogram() throws IOException
    {
        final String fileName = "C:\\Users\\Serg\\Desktop\\DB4_B\\104_6.tif";
        BufferedImage img = ImageIO.read(new File(fileName));

        int width = img.getWidth();
        int height = img.getHeight();
        int totalPixels = width * height;

        int[] brightnessQuantities = new int [256];
        Arrays.fill(brightnessQuantities, 0);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                Color c = new Color(img.getRGB(w, h));
                int brightness = (int) (Utilites.getColorBrightness(c) * 255);

                brightnessQuantities[brightness] += 1;
            }
        }

        final int histogramHeight = 100;
        BufferedImage histogram = new BufferedImage(256, histogramHeight, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < histogram.getWidth(); i++) {
            int quantity = brightnessQuantities[i];
            for (int j = 0; j < Math.min(histogramHeight, quantity); j++) {
                histogram.setRGB(i, histogramHeight - j - 1, Color.white.getRGB());
            }
        }

        final double threshold = 100;
        double accumulator = 0;
        for (int i = 0; i < brightnessQuantities.length; i++) {
            accumulator += brightnessQuantities[i];
            if (accumulator >= threshold) {
                drawHistogramLine(histogram, i);
                return;
            }
        }

        ImageIO.write(histogram, "bmp", new File("histogram.bmp"));
    }

    private static void drawHistogramLine(BufferedImage histogram, int brightnessLevel)
    {
        final int lineWidth = 6;
        for (int h = 0; h < histogram.getHeight(); h++) {
            for (int i = -lineWidth; i <= lineWidth; i++) {
                int w = brightnessLevel + i;
                if (w < 0 || w >= histogram.getWidth()) {
                    continue;
                }
                histogram.setRGB(brightnessLevel + i, h, Color.red.getRGB());
            }
        }
    }

    private static void compareFingerprints() throws IOException
    {
        final String folderName = "C:\\Users\\Serg\\Desktop\\DB4_B";
        final File folder = new File(folderName);
        final File tracesFolder = new File(folderName + "\\traces");
        tracesFolder.mkdir();

        File[] images = folder.listFiles();
        for (int baseSetIndex = 0; baseSetIndex < 10; ++baseSetIndex) {
            System.out.println("Set #" + (baseSetIndex + 1));
            final int setLength = 8;
            final int referenceIndex = baseSetIndex * setLength;
            FingerprintImage reference = FingerprintImage.create(images[referenceIndex]);

            drawTraceLines(tracesFolder, images[referenceIndex], reference);

            for (int i = referenceIndex + 1; i < referenceIndex + setLength; i++) {
                System.out.print(i - referenceIndex + ". ");
                FingerprintImage cmp = FingerprintImage.create(images[i]);

                drawTraceLines(tracesFolder, images[i], cmp);

                if (cmp.isMatch(reference)) {
                    System.out.println(" Matched");
                }
            }

            System.out.println();
        }
    }

    private static void drawTraceLines(File tracesFolder, File f, FingerprintImage fingerprint)
    {
        String traceFileName = getFilenameWithoutExtension(tracesFolder, f);
        fingerprint.drawTraceLines(traceFileName);
    }

    private static String getFilenameWithoutExtension(File folder, File f)
    {
        String fileName = f.getName().replaceFirst("[.][^.]+$", "");
        fileName = String.format("%s\\%s_trace.bmp", folder.getAbsolutePath(), fileName);
        return fileName;
    }
}