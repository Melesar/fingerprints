package com.melesar.fingerprints.IO;

import com.melesar.fingerprints.FingerprintImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScannerFingerprintWriter implements FingerprintWriter
{
    private ScannerDevice device;

    @Override
    public void write()
    {
        try {
            BufferedImage img = device.scan();
            FingerprintImage fingerprintImage = new FingerprintImage(img);

            //Write features to file
            FingerprintsDatabase database = new FingerprintsDatabase();
            database.add(fingerprintImage.getFeatures());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ScannerFingerprintWriter()
    {
        device = ScannerDevice.instance();
    }
}
