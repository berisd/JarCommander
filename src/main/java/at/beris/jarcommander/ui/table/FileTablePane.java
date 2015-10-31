/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.SelectNavigationPanelAction;
import org.apache.log4j.Logger;

import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileTablePane extends JScrollPane {
    private final static Logger LOGGER = Logger.getLogger(FileTablePane.class);

    private FileTable table;

    public FileTablePane() {
        super();
        table = new FileTable();
        getViewport().add(table);

        addMouseListener(new MouseListener());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                table.layoutColumns();
            }
        });
    }

    public FileTable getTable() {
        return table;
    }

    public void scrollToRow(int rowIndex) {
        Rectangle rect = table.getCellRect(rowIndex, 0, true);
        Point pt = getViewport().getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        getViewport().scrollRectToVisible(rect);
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            LOGGER.debug("mousePressed");
            Component parent = ((Component) e.getSource()).getParent();
            new SelectNavigationPanelAction().actionPerformed(new ActionEvent(parent, e.getID(), ActionType.SELECT_NAVIGATION_PANEL.toString()));
        }
    }
}
