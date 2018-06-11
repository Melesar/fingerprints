import com.melesar.fingerprints.Feature;
import com.melesar.fingerprints.FingerprintImage;
import com.melesar.fingerprints.IO.FingerprintWriter;
import com.melesar.fingerprints.IO.Identificator;
import com.melesar.fingerprints.IO.ImageFingerprintIdentificator;
import com.melesar.fingerprints.IO.ImageFingerprintWriter;
import com.melesar.gui.LoginForm;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Program
{
    public static void main(String[] args) throws IOException
    {
//        LoginForm form = new LoginForm();
//        FingerprintWriter fingerprintWriter = new ImageFingerprintWriter(form);
//        Identificator identificator = new ImageFingerprintIdentificator(form);
//
//        form.setFingerprintWriter(fingerprintWriter);
//        form.setIdentificator(identificator);
//        form.setVisible(true);

        URL imageUrl1 = Program.class.getClassLoader().getResource("fingerprint.jpg");
        URL imageUrl2 = Program.class.getClassLoader().getResource("fingerprint.jpg");

        BufferedImage fingerprintImage1 = ImageIO.read(imageUrl1);
        BufferedImage fingerprintImage2 = ImageIO.read(imageUrl2);

        FingerprintImage originalImage = new FingerprintImage(fingerprintImage1);
        FingerprintImage compareImage = new FingerprintImage(fingerprintImage2);

        for (Feature feature : compareImage.getFeatures()) {
            feature.point.x -= 120;
            feature.point.y += 21;
            feature.angle += Math.PI / 12 * 5;
        }

        boolean isMatch = originalImage.isMatch(compareImage);

        System.out.println(isMatch);
    }
}