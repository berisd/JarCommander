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
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class QuitAction extends CustomAction {

    public QuitAction(ApplicationContext context) {
        super(context);
        putValue(Action.NAME, "Quit");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);
        putValue(Action.ACTION_COMMAND_KEY, "quit");
        putValue(Action.SHORT_DESCRIPTION, "Quit");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Alt+F4 already bound
    }
}
