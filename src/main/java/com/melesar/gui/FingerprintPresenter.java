package com.melesar.gui;

import com.melesar.fingerprints.FingerprintImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class FingerprintPresenter
{
    private JPanel content;
    private JLabel mainImage;
    private JPanel indicatorImage;

    private Icon icon;

    private final Color BACKGROUND_COLOR = new Color(50, 50,50);

    public void update(boolean isMatch)
    {
        Color col = isMatch ? Color.GREEN : Color.RED;
        indicatorImage.setBackground(col);
        indicatorImage.setForeground(col);
    }

    public Icon getIcon()
    {
        return icon;
    }

    public JPanel getContent()
    {
        return content;
    }

    public FingerprintPresenter(FingerprintImage model)
    {
        this.icon = new ImageIcon(model.getImageData());

        buildLayout();
    }

    private void buildLayout()
    {
        content = new JPanel(new BorderLayout());
        content.setBackground(BACKGROUND_COLOR);

        mainImage = new JLabel();
        mainImage.setSize(250, 300);
        mainImage.setIcon(icon);
        content.add(mainImage, BorderLayout.CENTER);

        indicatorImage = new JPanel();
        indicatorImage.setSize(50, 50);
        content.add(indicatorImage, BorderLayout.EAST);
    }
}
