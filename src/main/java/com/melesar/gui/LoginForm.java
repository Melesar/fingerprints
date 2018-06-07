package com.melesar.gui;

import com.melesar.fingerprints.IO.FingerprintWriter;
import com.melesar.fingerprints.IO.Identificator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame
{
    private JPanel loginPanel;
    private JPanel content;
    private JPanel buttons;
    private JButton registerButton;
    private JButton loginButton;

    private Identificator identificator;
    private FingerprintWriter fingerprintWriter;

    public LoginForm() throws HeadlessException
    {
        super("Login");

        setContentPane(content);
        pack();

        loginButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (identificator.identityExists()) {
                    goNext();
                }
            }
        });

        registerButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fingerprintWriter.write();
                goNext();
            }
        });
    }

    private void goNext()
    {

    }

    public void setIdentificator(Identificator identificator)
    {
        this.identificator = identificator;
    }

    public void setFingerprintWriter(FingerprintWriter fingerprintWriter)
    {
        this.fingerprintWriter = fingerprintWriter;
    }
}
