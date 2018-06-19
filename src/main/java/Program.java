import com.melesar.fingerprints.FingerprintImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class Program
{
    public static void main(String[] args) throws IOException
    {
        final String folderName = "C:\\Users\\Serg\\Desktop\\DB4_B";
        final File folder = new File(folderName);
        final File tracesFolder = new File(folderName + "\\traces");
        tracesFolder.mkdir();

        File[] images = folder.listFiles();
        FingerprintImage reference = new FingerprintImage(ImageIO.read(images[0]));
        for (int i = 1; i < 8; i++) {
            System.out.println(i + ".");
            FingerprintImage cmp = new FingerprintImage(ImageIO.read(images[i]));
            if (cmp.isMatch(reference)) {
                System.out.println("Matched");
            }

            System.out.println();
            System.out.println();
        }
    }

    private static void drawTraceLines(File tracesFolder, File f, FingerprintImage fingerprint)
    {
        String traceFileName = f.getName().replaceFirst("[.][^.]+$", "");
        traceFileName = String.format("%s\\%s_trace.bmp", tracesFolder.getAbsolutePath(), traceFileName);
        fingerprint.drawTraceLines(traceFileName);
    }
}