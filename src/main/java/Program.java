import com.melesar.fingerprints.IO.FingerprintWriter;
import com.melesar.fingerprints.IO.Identificator;
import com.melesar.fingerprints.IO.ImageFingerprintIdentificator;
import com.melesar.fingerprints.IO.ImageFingerprintWriter;
import com.melesar.gui.LoginForm;

import java.io.IOException;

public class Program
{
    public static void main(String[] args)
    {
        LoginForm form = new LoginForm();
        FingerprintWriter fingerprintWriter = new ImageFingerprintWriter(form);
        Identificator identificator = new ImageFingerprintIdentificator(form);

        form.setFingerprintWriter(fingerprintWriter);
        form.setIdentificator(identificator);
        form.setVisible(true);
    }
}