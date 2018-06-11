package com.melesar.fingerprints.IO;

import com.melesar.fingerprints.FeatureList;
import com.melesar.fingerprints.FingerprintImage;

import java.io.IOException;

public class ScannerFingerprintIdentificator implements Identificator
{
    private ScannerDevice device;

    @Override
    public boolean identityExists()
    {
        try {
            FingerprintImage fingerprint = new FingerprintImage(device.scan());
            FingerprintsDatabase database = new FingerprintsDatabase();
            for(FeatureList list : database.read()) {
                if (fingerprint.isMatch(list)) {
                    return true;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ScannerFingerprintIdentificator()
    {
        device = ScannerDevice.instance();
    }
}
