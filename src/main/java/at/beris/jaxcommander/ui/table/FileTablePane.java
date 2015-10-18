/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.action.ParamActionEvent;
import org.apache.log4j.Logger;

import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;

import static at.beris.jaxcommander.action.ActionCommand.*;

public class FileTablePane extends JScrollPane implements ActionListener {
    private final static Logger LOGGER = Logger.getLogger(FileTablePane.class);

    private Path path;
    private FileTable table;

    public FileTablePane() {
        super();
    }

    public FileTablePane(Path path) {
        super();
        this.path = path;
        table = new FileTable(path);
        getViewport().add(table);

        addMouseListener(new MouseListener());
    }

    public FileTable getTable() {
        return table;
    }

    public void refresh() {
        table.refresh();
    }

    public void setPath(Path path) {
        table.setPath(path);
    }

    public void scrollToRow(int rowIndex) {
        Rectangle rect = table.getCellRect(rowIndex, 0, true);
        Point pt = getViewport().getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        getViewport().scrollRectToVisible(rect);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("actionPerformed");

        if (e.getActionCommand().equals(SCROLL_TO_TOP)) {
            if (table.getRowCount() > 0) {
                scrollToRow(0);
                table.repaint();
            }
        } else if (e.getActionCommand().equals(SCROLL_TO_BOTTOM)) {
            if (table.getRowCount() > 0) {
                scrollToRow(table.getRowCount());
                table.repaint();
            }
        } else if (e.getActionCommand().equals(KEY_PRESSED)) {
            Integer keyCode = ((ParamActionEvent<Integer>) e).getParam();
            char keyChar = (char) Character.toLowerCase(keyCode);

            if ((keyChar == '.') || (keyChar >= 'a' && keyChar <= 'z')) {
                for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
                    File file = (File) table.getValueAt(rowIndex, 0);
                    String[] fileNameParts = file.toString().split(File.separator);
                    String fileName = fileNameParts[fileNameParts.length - 1];
                    if (fileName.toLowerCase().charAt(0) == Character.toLowerCase(keyCode)) {
                        Rectangle rect = table.getCellRect(rowIndex, 0, true);
                        Point pt = viewport.getViewPosition();
                        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
                        viewport.scrollRectToVisible(rect);
                        break;
                    }
                }
            }
        } else {
            ((ActionListener) this.getParent()).actionPerformed(e);
        }

    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            LOGGER.debug("mousePressed");
            ActionListener parent = (ActionListener) ((Component) e.getSource()).getParent();
            parent.actionPerformed(new ActionEvent(e.getSource(), e.getID(), SELECT_NAVIGATION_PANEL));
        }
    }
}
