package com.melesar.gui;

import com.melesar.fingerprints.FingerprintImage;
import com.melesar.fingerprints.Utilites;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class FingerprintsForm extends JFrame
{
    private JPanel holder;
    private JScrollPane scroll;
    private JPanel content;

    private DefaultListModel<FingerprintPresenter> fingerprintList;
    private ArrayList<FingerprintImage> fingerprintModels;

    private static FingerprintsForm instance;

    public static FingerprintsForm run ()
    {
        if (instance != null) {
            return instance;
        }

        FingerprintsForm form = new FingerprintsForm();
        form.setContentPane(form.holder);
        form.setLocationRelativeTo(null);
        form.setSize(500, 700);
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.setVisible(true);

        instance = form;

        return instance;
    }

    private FingerprintsForm ()
    {
        loadData();
        createUIComponents();
    }

    private void loadData()
    {
        InputStream stream = Utilites.class.getResourceAsStream("images");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        fingerprintList = new DefaultListModel<>();
        fingerprintModels = new ArrayList<>();

        try {
            String resource;
            while((resource = reader.readLine()) != null) {
                URL url = Utilites.class.getResource(String.format("images/%s", resource));
                FingerprintImage fingerprintImage = FingerprintImage.create(new File(url.toURI()));
                FingerprintPresenter presenter = new FingerprintPresenter(fingerprintImage);

                fingerprintList.addElement(presenter);
                fingerprintModels.add(fingerprintImage);
            }
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    private void createUIComponents()
    {
        JList<FingerprintPresenter> jList = new JList<>(fingerprintList);
        jList.setFixedCellHeight(30);
        jList.setFixedCellWidth(150);

        scroll = new JScrollPane(jList);
        holder.add(scroll, BorderLayout.WEST);

        content = new JPanel(new FlowLayout());
        holder.add(content, BorderLayout.CENTER);

        content.add(new Button("Button1"));
        content.add(new Button("Button2"));
    }
}
