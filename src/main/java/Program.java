import com.melesar.fingerprints.FingerprintImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Program
{
    public static void main(String[] args) throws IOException
    {
//        final String fileName = "C:\\Users\\Serg\\Desktop\\DB4_B\\101_1.tif";
//        FingerprintImage f = new FingerprintImage(ImageIO.read(new File(fileName)));
        compareFingerprints();
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
            FingerprintImage reference = new FingerprintImage(ImageIO.read(images[referenceIndex]));

            drawTraceLines(tracesFolder, images[referenceIndex], reference);

            for (int i = referenceIndex + 1; i < referenceIndex + setLength; i++) {
                System.out.print(i - referenceIndex + ". ");
                FingerprintImage cmp = new FingerprintImage(ImageIO.read(images[i]));

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