/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import at.beris.jarcommander.ApplicationContext;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ShowAboutDialogAction extends CustomAction {

    public ShowAboutDialogAction(ApplicationContext context) {
        super(context);
        putValue(Action.NAME, "About");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        putValue(Action.ACTION_COMMAND_KEY, "copy");
        putValue(Action.SHORT_DESCRIPTION, "COPY");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog((JMenuItem) e.getSource(), "Jar Commander" + System.lineSeparator() + "(C) 2015 Bernd Riedl", "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
