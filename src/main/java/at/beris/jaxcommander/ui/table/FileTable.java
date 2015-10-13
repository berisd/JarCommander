/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.FileDefaultComparator;
import at.beris.jaxcommander.action.ParamActionEvent;
import org.apache.log4j.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static at.beris.jaxcommander.action.ActionCommand.*;

public class FileTable extends JTable {
    private final static Logger LOGGER = Logger.getLogger(FileTablePane.class);

    private TableRowSorter<TableModel> rowSorter;
    private Path path;

    public FileTable() {
        super();
    }

    public FileTable(Path path) {
        super();

        this.path = path;

        setModel(new PathTableModel(path));
        getColumnModel().getColumn(0).setCellRenderer(new FileRenderer());
        getColumnModel().getColumn(1).setCellRenderer(new DateRenderer());
        getColumnModel().getColumn(2).setCellRenderer(new FileSizeRenderer());

        addMouseListener(new FileTableMouseListener());

        addKeyListener(new CustomerKeyListener());

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), SCROLL_TO_TOP);
        getActionMap().put(SCROLL_TO_TOP, new FileTableAction(SCROLL_TO_TOP));
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), SCROLL_TO_BOTTOM);
        getActionMap().put(SCROLL_TO_BOTTOM, new FileTableAction(SCROLL_TO_BOTTOM));
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_DIRECTORY);
        getActionMap().put(ENTER_DIRECTORY, new FileTableAction(ENTER_DIRECTORY));
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), NAVIGATE_PATH_UP);
        getActionMap().put(NAVIGATE_PATH_UP, new FileTableAction(NAVIGATE_PATH_UP));

        rowSorter = new TableRowSorter<>(getModel());
        setRowSorter(rowSorter);

        rowSorter.setComparator(0, new FileDefaultComparator());

        List<RowSorter.SortKey> sortKeys
                = new ArrayList<>();

        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeys);

    }

    public void refresh() {
        ((PathTableModel) getModel()).setPath(path);
        rowSorter.sort();
        repaint();
    }

    public void setPath(Path path) {
        this.path = path;
        ((PathTableModel) getModel()).setPath(path);
        rowSorter.sort();
        getSelectionModel().clearSelection();
        repaint();
    }

    private class FileTableAction extends AbstractAction {
        public FileTableAction(String actionCommandKey) {
            putValue(Action.ACTION_COMMAND_KEY, actionCommandKey);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.debug("actionPerformed");
            FileTable table = (FileTable) e.getSource();
            ((ActionListener) table.getParent().getParent()).actionPerformed(e);
        }
    }

    private class CustomerKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            LOGGER.debug("keyPressed");
            super.keyPressed(e);

            FileTable table = (FileTable) e.getSource();
            FileTablePane pane = (FileTablePane) table.getParent().getParent();
            pane.actionPerformed(new ParamActionEvent<>(e.getSource(), e.getID(), KEY_PRESSED, e.getKeyCode()));
        }
    }
}
