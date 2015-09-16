/*
 * This file is part of JXCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;

public class Application extends JFrame implements Runnable {
    GridBagLayout layout;

    public Application() {
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setTitle("JaxCommander");

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new CustomWindowAdapter());

        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 1, true);

        layout = new GridBagLayout();
        setLayout(layout);

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        JLabel label1 = new JLabel("Hier ist der Toolbar");
        label1.setBorder(lineBorder);
        add(label1, c);

        c.gridy = 1;
        c.gridwidth = 1;
        add(new JaxTable(), c);

        c.gridx = 1;
        c.gridwidth = 1;
        add(new JaxTable(), c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;

        JLabel label2 = new JLabel("Hier ist der Statusbar");
        label2.setBorder(lineBorder);
        add(label2, c);

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Application());
    }

    public void run() {
        setVisible(true);
    }

    public void quit() {
        if (JOptionPane.showConfirmDialog(null,
                "Are you sure to quit?", "Quit JXCommander",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    class CustomWindowAdapter extends WindowAdapter {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            quit();
        }
    }
}
