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
import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import at.beris.jarcommander.ui.table.FileTable;
import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ExecuteFileAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(ExecuteFileAction.class);

    public ExecuteFileAction(ApplicationContext context) {
        super(context);

        keyStrokeString = "ENTER";
        putValue(Action.NAME, "Execute File");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_ENTER);
        putValue(Action.SHORT_DESCRIPTION, "Execute File");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("ExecuteFileAction");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel navigationPanel = sessionPanel.getSelectedNavigationPanel();
        FileTable fileTable = navigationPanel.getFileTable();

        int firstSelectedRowIndex = fileTable.getSelectedRow();

        if (firstSelectedRowIndex >= 0) {
            IFile file = (IFile) fileTable.getModel().getValueAt(fileTable.convertRowIndexToModel(firstSelectedRowIndex), 0);
            if (file.isDirectory()) {
                navigationPanel.changeDirectory(file.toPath());
            } else {
                navigationPanel.executeFile(file);
            }
        }
    }
}
