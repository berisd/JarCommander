/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.FileDefaultComparator;
import at.beris.jarcommander.action.ActionCommand;
import at.beris.jarcommander.action.ParamActionEvent;
import at.beris.jarcommander.action.SwitchNavigationPanelAction;
import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.path.JPath;
import org.apache.log4j.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static at.beris.jarcommander.action.ActionCommand.*;

public class FileTable extends JTable {
    private final static Logger LOGGER = Logger.getLogger(FileTablePane.class);

    private TableRowSorter<TableModel> rowSorter;
    private JPath path;

    public FileTable() {
        super();

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setModel(new PathTableModel());
        setSelectionModel(new FileTableSelectionModel(this));

        getColumnModel().getColumn(0).setCellRenderer(new FileNameRenderer());
        getColumnModel().getColumn(1).setCellRenderer(new DateRenderer());
        getColumnModel().getColumn(2).setCellRenderer(new FileSizeRenderer());
        getColumnModel().getColumn(3).setCellRenderer(new FileAttributesRenderer());

        addMouseListener(new FileTableMouseListener());
        addKeyListener(new CustomerKeyListener());
        getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                ActionListener parent = (ActionListener) ((Component) e.getSource()).getParent().getParent();
                parent.actionPerformed(new ActionEvent(e.getSource(), e.getID(), SELECT_NAVIGATION_PANEL));
            }
        });

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), SCROLL_TO_TOP);
        getActionMap().put(SCROLL_TO_TOP, new FileTableAction(SCROLL_TO_TOP));
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), SCROLL_TO_BOTTOM);
        getActionMap().put(SCROLL_TO_BOTTOM, new FileTableAction(SCROLL_TO_BOTTOM));
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), EXECUTE_FILE);
        getActionMap().put(EXECUTE_FILE, new FileTableAction(EXECUTE_FILE));
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), NAVIGATE_PATH_UP);
        getActionMap().put(NAVIGATE_PATH_UP, new FileTableAction(NAVIGATE_PATH_UP));
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), ActionCommand.SWITCH_NAVIGATION_PANEL);
        getActionMap().put(ActionCommand.SWITCH_NAVIGATION_PANEL, new SwitchNavigationPanelAction());

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

    public void setPath(JPath path) {
        this.path = path;
        ((PathTableModel) getModel()).setPath(path);
        rowSorter.sort();
        getSelectionModel().clearSelection();
        getSelectionModel().setSelectionInterval(0, 0);
        requestFocusInWindow();
    }

    public void listFile(JFile file) {
        ((PathTableModel) getModel()).listFile(file);
        rowSorter.sort();
        getSelectionModel().clearSelection();
        repaint();
    }

    /**
     * Layout columns to the width of their biggest value and make first column fill rest of the table width.
     */
    public void layoutColumns() {
        LOGGER.debug("layoutColumns");
        for (int column = 1; column < getColumnCount(); column++) {
            TableColumn tableColumn = getColumnModel().getColumn(column);
            int maxCellWidth = 0;
            for (int row = 0; row < getRowCount(); row++) {
                TableCellRenderer renderer = getCellRenderer(row, column);
                Component comp = prepareRenderer(renderer, row, column);

                int rendererWidth = comp.getPreferredSize().width;
                int cellWidth = rendererWidth + getIntercellSpacing().width + 1 + 10;

                maxCellWidth = cellWidth > maxCellWidth ? cellWidth : maxCellWidth;
            }
            tableColumn.setPreferredWidth(maxCellWidth);
        }

        if (this.getParent() != null) {
            int columnsSizeWithOutFirst = 0;
            for (int column = 1; column < getColumnCount(); column++) {
                columnsSizeWithOutFirst += getColumnModel().getColumn(column).getWidth();
            }

            TableColumn tableColumn = getColumnModel().getColumn(0);

            int viewportWidth = (int) this.getParent().getSize().getWidth();

            int scrollBarSize = 14;
            int width = (viewportWidth - columnsSizeWithOutFirst) - getIntercellSpacing().width - scrollBarSize;
            tableColumn.setPreferredWidth(width);
        }
    }

    private class FileTableAction extends AbstractAction {
        private String actionCommandKey;

        public FileTableAction(String actionCommandKey) {
            this.actionCommandKey = actionCommandKey;
            putValue(Action.ACTION_COMMAND_KEY, actionCommandKey);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.debug("actionPerformed");
            FileTable table = (FileTable) e.getSource();

            int firstSelectedRowIndex = table.getSelectedRow();

            ActionEvent newEvent = e;
            if (actionCommandKey.equals(EXECUTE_FILE) && firstSelectedRowIndex >= 0) {
                JFile file = (JFile) table.getModel().getValueAt(table.convertRowIndexToModel(firstSelectedRowIndex), 0);
                newEvent = new ParamActionEvent<>(e.getSource(), e.getID(), e.getActionCommand(), file);
            }
            ((ActionListener) getSendActionToParent(table)).actionPerformed(newEvent);
        }
    }

    private Component getSendActionToParent(JComponent component) {
        JComponent parent = (JComponent) component.getParent();
        if (parent instanceof JViewport) {
            return parent.getParent();
        } else if (parent instanceof JPanel) {
            return component.getRootPane().getParent();
        } else {
            return parent;
        }
    }

    private class CustomerKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            LOGGER.debug("keyPressed");
            super.keyPressed(e);

            FileTable table = (FileTable) e.getSource();
            Component parent = getSendActionToParent(table);
            ((ActionListener) parent).actionPerformed(new ParamActionEvent<>(e.getSource(), e.getID(), KEY_PRESSED, e.getKeyCode()));
        }
    }
}

