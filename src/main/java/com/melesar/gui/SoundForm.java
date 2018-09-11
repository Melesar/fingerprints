package com.melesar.gui;

import javax.swing.*;

public class SoundForm extends JFrame
{
    private JPanel content;
    private JButton btnRecord;
    private JButton btnValidate;
    private JPanel buttons;
    private JLabel text;

    public static SoundForm showForm()
    {
        SoundForm instance = new SoundForm();
        instance.setContentPane(instance.content);
        instance.setLocationRelativeTo(null);
        instance.setSize(500, 150);
        instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        instance.setVisible(true);

        return instance;
    }
}
