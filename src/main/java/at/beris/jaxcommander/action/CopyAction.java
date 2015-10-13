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
import at.beris.jaxcommander.ui.NavigationPanel;
import at.beris.jaxcommander.ui.SessionPanel;
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
        NavigationPanel sourcePanel = sessionPanel.getSelectedNavigationPanel();
        NavigationPanel targetPanel;
        if (sessionPanel.getLeftNavigationPanel().equals(sourcePanel))
            targetPanel = sessionPanel.getRightNavigationPanel();
        else
            targetPanel = sessionPanel.getLeftNavigationPanel();

        List<File> sourcefileList = sourcePanel.getSelection();
        Path targetPath = targetPanel.getCurrentPath();

        for (File file : sourcefileList) {
            Path sourcePath = file.toPath();
            try {
                Files.copy(sourcePath, targetPath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }

    private class CopyTask extends SwingWorker<Void, Void> {
        Application application;

        public CopyTask(Application application) {
            this.application = application;
        }

        public void start() {
            //display start
            execute();
        }

        @Override
        protected Void doInBackground() throws Exception {
            return null;
        }

        @Override
        protected void done() {

        }
    }
}
