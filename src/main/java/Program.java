import com.melesar.fingerprints.IO.FingerprintWriter;
import com.melesar.fingerprints.IO.Identificator;
import com.melesar.fingerprints.IO.ScannerFingerprintIdentificator;
import com.melesar.fingerprints.IO.ScannerFingerprintWriter;
import com.melesar.gui.LoginForm;

import java.io.IOException;

public class Program
{
    public static void main(String[] args) throws IOException
    {
        LoginForm form = new LoginForm();
        FingerprintWriter fingerprintWriter = new ScannerFingerprintWriter();
        Identificator identificator = new ScannerFingerprintIdentificator();

        form.setFingerprintWriter(fingerprintWriter);
        form.setIdentificator(identificator);
        form.setVisible(true);

//        URL imageUrl1 = Program.class.getClassLoader().getResource("fingerprint_1.jpg");
//        URL imageUrl2 = Program.class.getClassLoader().getResource("fingerprint_1.jpg");
//
//        BufferedImage fingerprintImage1 = ImageIO.read(imageUrl1);
//        BufferedImage fingerprintImage2 = ImageIO.read(imageUrl2);
//
//        FingerprintImage originalImage = new FingerprintImage(fingerprintImage1);
//        FingerprintImage compareImage = new FingerprintImage(fingerprintImage2);
//
//        boolean isMatch = originalImage.isMatch(compareImage);
//
//        System.out.println(isMatch);
    }
}