package com.melesar.gui;

import com.melesar.audio.RecordingManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SoundForm extends JFrame
{
    private JPanel content;
    private JButton btnRecord;
    private JButton btnValidate;
    private JLabel text;

    private RecordingManager recordingManager;

    public static SoundForm showForm()
    {
        SoundForm instance = new SoundForm();
        instance.setContentPane(instance.content);
        instance.setLocationRelativeTo(null);
        instance.setSize(500, 150);
        instance.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        instance.setVisible(true);

        return instance;
    }

    public SoundForm() throws HeadlessException
    {
        recordingManager = new RecordingManager();
        setListeners();
    }

    private void setListeners()
    {
        btnRecord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                text.setText("Voice recording started");
                boolean recordSuccessfull = recordingManager.recordReference();
                String outputText = recordSuccessfull ? "Voice has been recorded" : "Error recording voice";
                text.setText(outputText);
            }
        });

        btnValidate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                text.setText("Voice recording started");
                boolean isMatch = recordingManager.compareSample();
                String outputText = isMatch ? "Voice matches reference" : "Voice doesn't match";
                text.setText(outputText);
            }
        });
    }
}
