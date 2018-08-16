package com.melesar.gui;

import com.melesar.fingerprints.Utilites;

import javax.swing.*;
import java.net.URL;

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
        form.createUIComponents();

        instance = form;

        return instance;
    }

    private FingerprintsForm ()
    {

    }

    private void createUIComponents()
    {
        URL resource = Utilites.class.getResource("images/101_1.tif");
        System.out.println(new ImageIcon(resource));
        referenceFingerprint.setBounds(0, 0, 150,150);
        referenceFingerprint.setIcon(new ImageIcon(resource));
    }
}
