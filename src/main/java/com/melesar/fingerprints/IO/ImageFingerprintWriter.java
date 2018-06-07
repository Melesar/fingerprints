package com.melesar.fingerprints.IO;

import com.melesar.fingerprints.FeatureList;
import com.melesar.fingerprints.FingerprintImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ImageFingerprintWriter implements FingerprintWriter
{
    private Component parent;

    public ImageFingerprintWriter(Component parent)
    {
        this.parent = parent;
    }

    @Override
    public void write()
    {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setFileFilter(new ImageFileFilter());

        int chooseResult = fileChooser.showOpenDialog(parent);
        if (chooseResult != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            BufferedImage img = ImageIO.read(fileChooser.getSelectedFile());
            FingerprintImage fingerprintImage = new FingerprintImage(img);

            //Write features to file
            FingerprintsDatabase database = new FingerprintsDatabase();
            database.add(fingerprintImage.getFeatures());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
