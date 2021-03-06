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
import at.beris.jarcommander.ui.SiteManagerDialog;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ShowSiteDialogAction extends CustomAction {

    public ShowSiteDialogAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "ctrl S";
        putValue(Action.NAME, "Sites");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        putValue(Action.SHORT_DESCRIPTION, "Manage sites");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new SiteManagerDialog(context, Dialog.ModalityType.APPLICATION_MODAL).setVisible(true);
    }
}
