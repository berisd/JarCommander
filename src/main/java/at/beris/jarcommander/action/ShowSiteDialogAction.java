/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.ui.SiteManagerDialog;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ShowSiteDialogAction extends CustomAction {
    public static final ActionType KEY = ActionType.SHOW_SITE_DIALOG;

    public ShowSiteDialogAction() {
        super();

        keyStrokeString = "ctrl S";
        putValue(Action.NAME, "Sites");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        putValue(Action.ACTION_COMMAND_KEY, "showSites");
        putValue(Action.SHORT_DESCRIPTION, "Manage sites");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Application application = (Application) SwingUtilities.getRoot((Component) e.getSource());
        new SiteManagerDialog(application, true).setVisible(true);
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
