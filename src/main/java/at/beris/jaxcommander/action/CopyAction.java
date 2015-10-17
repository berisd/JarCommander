/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.action;

import at.beris.jaxcommander.Application;
import at.beris.jaxcommander.task.CopyTask;
import at.beris.jaxcommander.ui.NavigationPanel;
import at.beris.jaxcommander.ui.SessionPanel;
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class CopyAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(CopyAction.class);
    public static final ActionType KEY = ActionType.COPY;

    public CopyAction() {
        super();

        keyStrokeString = "F5";
        putValue(Action.NAME, "Copy");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
        putValue(Action.ACTION_COMMAND_KEY, "copy");
        putValue(Action.SHORT_DESCRIPTION, "Copy");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Copy");
        Application application = (Application) SwingUtilities.getRoot((Component) e.getSource());

        SessionPanel sessionPanel = application.getSessionPanel();
        final NavigationPanel sourcePanel = sessionPanel.getSelectedNavigationPanel();
        final NavigationPanel targetPanel;
        if (sessionPanel.getLeftNavigationPanel().equals(sourcePanel))
            targetPanel = sessionPanel.getRightNavigationPanel();
        else
            targetPanel = sessionPanel.getLeftNavigationPanel();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CopyTask copyTask = new CopyTask(sourcePanel.getSelection(), targetPanel);
                copyTask.setVisible(true);
                copyTask.startWorker();
            }
        });
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
