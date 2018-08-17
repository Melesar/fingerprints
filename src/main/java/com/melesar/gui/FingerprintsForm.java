package com.melesar.gui;

import com.melesar.fingerprints.FingerprintImage;
import com.melesar.fingerprints.Utilites;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class FingerprintsForm extends JFrame implements MouseListener, ActionListener
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
                presenter.setClickListener(this);

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

        scroll = new JScrollPane(jList);
        holder.add(scroll, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND_COLOR);
        holder.add(content, BorderLayout.CENTER);

        mainImage = new JLabel();
        mainImage.setSize(250, 300);
        content.add(mainImage);


        JButton cmpButton = new JButton("Compare");
        cmpButton.setSize(100, 70);
        cmpButton.setBackground(BACKGROUND_COLOR);
        cmpButton.setForeground(new Color(200, 200, 200));
        cmpButton.addActionListener(this);
        content.add(cmpButton, BorderLayout.SOUTH);
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
    public void mouseClicked(MouseEvent e)
    {
        FingerprintPresenter presenter = (FingerprintPresenter) e.getSource();
        int index = fingerprintList.indexOf(presenter);
        selectModel(index);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        compareFingerprints();
    }

    @Override
    public void mousePressed(MouseEvent e)
    {

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }
}
