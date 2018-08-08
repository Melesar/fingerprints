package com.melesar.gui;

import com.melesar.fingerprints.Utilites;

import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FingerprintsForm extends JFrame
{
    private JPanel holder;
    private JPanel scroll;
    private JPanel content;
    private JScrollPane items;
    private JLabel referenceFingerprint;
    private JPanel imagePanel;
    private JPanel buttonsPanel;

    private static FingerprintsForm instance;

    public static FingerprintsForm run ()
    {
        if (instance != null) {
            return instance;
        }

        FingerprintsForm form = new FingerprintsForm();
        form.setContentPane(form.holder);
        form.setBounds(300, 300, 700, 500);
        form.setResizable(true);
        form.setVisible(true);

        instance = form;

        return instance;
    }

    private FingerprintsForm ()
    {

    }

    private void createUIComponents()
    {
        try {
            BufferedImage img = Utilites.loadResourceImage("101_1.tif");
            referenceFingerprint.setIcon(new ImageIcon(img));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
