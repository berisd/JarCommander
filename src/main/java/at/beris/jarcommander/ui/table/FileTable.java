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
import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.ExecuteFileAction;
import at.beris.jarcommander.action.NavigatePathUpAction;
import at.beris.jarcommander.action.ParamActionEvent;
import at.beris.jarcommander.action.ScrollToBottomAction;
import at.beris.jarcommander.action.ScrollToLetterInFileTablePaneAction;
import at.beris.jarcommander.action.ScrollToTopAction;
import at.beris.jarcommander.action.SelectNavigationPanelAction;
import at.beris.jarcommander.action.SwitchNavigationPanelAction;
import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.path.JPath;
import org.apache.log4j.Logger;

import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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
                Component parent = ((Component) e.getSource()).getParent().getParent().getParent();
                new SelectNavigationPanelAction().actionPerformed(new ActionEvent(parent, e.getID(), ActionType.SELECT_NAVIGATION_PANEL.toString()));
            }
        });

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ActionType.SCROLL_TO_TOP.getKeyStroke(), ActionType.SCROLL_TO_TOP);
        getActionMap().put(ActionType.SCROLL_TO_TOP, new ScrollToTopAction());
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ActionType.SCROLL_TO_BOTTOM.getKeyStroke(), ActionType.SCROLL_TO_BOTTOM);
        getActionMap().put(ActionType.SCROLL_TO_BOTTOM, new ScrollToBottomAction());
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ActionType.EXECUTE_FILE.getKeyStroke(), ActionType.EXECUTE_FILE);
        getActionMap().put(ActionType.EXECUTE_FILE, new ExecuteFileAction());
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ActionType.NAVIGATE_PATH_UP.getKeyStroke(), ActionType.NAVIGATE_PATH_UP);
        getActionMap().put(ActionType.NAVIGATE_PATH_UP, new NavigatePathUpAction());
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ActionType.SWITCH_NAVIGATION_PANEL.getKeyStroke(), ActionType.SWITCH_NAVIGATION_PANEL);
        getActionMap().put(ActionType.SWITCH_NAVIGATION_PANEL, new SwitchNavigationPanelAction());

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

    private class CustomerKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            LOGGER.debug("keyPressed");
            super.keyPressed(e);

            if ((e.getKeyChar() == '.') || (e.getKeyChar() >= 'a' && e.getKeyChar() <= 'z')) {
                new ScrollToLetterInFileTablePaneAction().actionPerformed(new ParamActionEvent<Character>(e.getSource(), e.getID(), ActionType.SCROLL_TO_LETTER_IN_FILE_TABLE_PANE.toString(), Character.valueOf(e.getKeyChar())));
            }
        }
    }
}

