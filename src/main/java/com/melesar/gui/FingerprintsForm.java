package com.melesar.gui;

import com.melesar.fingerprints.FingerprintImage;
import com.melesar.fingerprints.Utilites;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class FingerprintsForm extends JFrame implements ActionListener, ListSelectionListener
{
    private JPanel holder;
    private JLabel mainImage;
    private JScrollPane scroll;

    private DefaultListModel<FingerprintPresenter> fingerprintList;
    private ArrayList<FingerprintImage> fingerprintModels;
    private FingerprintImage currentModel;

    private static FingerprintsForm instance;

    private final Color BACKGROUND_COLOR = new Color(50, 50,50);

    public static FingerprintsForm run ()
    {
        if (instance != null) {
            return instance;
        }

        FingerprintsForm form = new FingerprintsForm();
        form.setContentPane(form.holder);
        form.setLocationRelativeTo(null);
        form.setSize(800, 700);
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.setVisible(true);

        instance = form;

        return instance;
    }

    private FingerprintsForm ()
    {
        loadData();
        createUIComponents();
        selectModel(0);
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
        jList.setFixedCellHeight(300);
        jList.setFixedCellWidth(300);
        jList.setCellRenderer(new FingerprintPresenterDrawer());
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.addListSelectionListener(this);

        scroll = new JScrollPane(jList);
        holder.add(scroll, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND_COLOR);
        holder.add(content, BorderLayout.CENTER);

        mainImage = new JLabel();
        mainImage.setSize(250, 300);
        content.add(mainImage);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        content.add(buttonsPanel, BorderLayout.SOUTH);

        JButton cmpButton = new JButton("Compare");
        cmpButton.setSize(100, 70);
        cmpButton.setBackground(BACKGROUND_COLOR);
        cmpButton.setForeground(new Color(200, 200, 200));
        cmpButton.addActionListener(this);
        buttonsPanel.add(cmpButton);

        JButton soundButton = new JButton("Sound");
        soundButton.setSize(100, 70);
        soundButton.setBackground(BACKGROUND_COLOR);
        soundButton.setForeground(new Color(200, 200, 200));
        soundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                SoundForm.showForm();
            }
        });
        buttonsPanel.add(soundButton);
    }

    private void compareFingerprints()
    {
        for (int i = 0; i < fingerprintModels.size(); i++) {
            FingerprintImage fingerprint = fingerprintModels.get(i);
            boolean isMatch = fingerprint.isMatch(currentModel);
            fingerprintList.getElementAt(i).update(isMatch);
        }

        scroll.updateUI();
    }

    private void selectModel(int index)
    {
        currentModel = fingerprintModels.get(index);
        mainImage.setIcon(fingerprintList.elementAt(index).getIcon());
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        compareFingerprints();
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        selectModel(e.getFirstIndex());
    }
}
