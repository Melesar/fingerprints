package com.melesar.fingerprints.IO;

import com.melesar.fingerprints.FeatureList;
import com.melesar.fingerprints.FingerprintImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageFingerprintIdentificator implements Identificator
{
    private Component parent;

    public ImageFingerprintIdentificator(Component parent)
    {
        this.parent = parent;
    }

    @Override
    public boolean identityExists()
    {
        FingerprintImage fingerprint = chooseFingerprint();

        if (fingerprint == null) {
            return false;
        }

        try {
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

    private FingerprintImage chooseFingerprint()
    {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileFilter(new ImageFileFilter());

        int chooseResult = fileChooser.showOpenDialog(parent);
        if (chooseResult != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        try {
            return FingerprintImage.create(fileChooser.getSelectedFile());
        } catch (IOException e) {
            return null;
        }
    }
}
