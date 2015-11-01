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
import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import at.beris.jarcommander.ui.table.FileTable;
import at.beris.jarcommander.ui.table.FileTablePane;
import org.apache.log4j.Logger;

import javax.swing.JViewport;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;

public class ScrollToLetterInFileTablePaneAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(ScrollToLetterInFileTablePaneAction.class);

    public ScrollToLetterInFileTablePaneAction(ApplicationContext context) {
        super(context);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("ScrollToLetterInFileTablePaneAction");
        SessionPanel sessionPanel = (SessionPanel) context.getSessionsPanel().getSelectedComponent();
        NavigationPanel navigationPanel = sessionPanel.getSelectedNavigationPanel();
        FileTable fileTable = navigationPanel.getFileTable();
        FileTablePane fileTablePane = navigationPanel.getFileTablePane();
        JViewport viewport = fileTablePane.getViewport();

        Character keyChar = (Character) ((ParamActionEvent) e).getParam();

        for (int rowIndex = 0; rowIndex < fileTable.getRowCount(); rowIndex++) {
            JFile file = (JFile) fileTable.getValueAt(rowIndex, 0);
            String[] fileNameParts = file.toString().split(File.separator);
            String fileName = fileNameParts[fileNameParts.length - 1];
            if (fileName.toLowerCase().charAt(0) == Character.toLowerCase(keyChar)) {
                Rectangle rect = fileTable.getCellRect(rowIndex, 0, true);
                Point pt = viewport.getViewPosition();
                rect.setLocation(rect.x - pt.x, rect.y - pt.y);
                viewport.scrollRectToVisible(rect);
                break;
            }
        }
    }
}
