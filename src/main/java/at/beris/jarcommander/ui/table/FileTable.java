/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.FileDefaultComparator;
import at.beris.jarcommander.action.*;
import at.beris.virtualfile.File;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class FileTable extends JTable {
    private final static Logger LOGGER = Logger.getLogger(FileTablePane.class);

    private TableRowSorter<TableModel> rowSorter;
    private File path;
    private ApplicationContext context;
    private ActionFactory actionFactory;

    public FileTable(final ApplicationContext context) {
        super();
        this.context = context;

        actionFactory = context.getActionFactory();

        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setModel(new PathTableModel());
        setSelectionModel(new FileTableSelectionModel(this));

        getColumnModel().getColumn(0).setCellRenderer(new FileNameRenderer());
        getColumnModel().getColumn(1).setCellRenderer(new FileTimeRenderer());
        getColumnModel().getColumn(2).setCellRenderer(new FileSizeRenderer());
        getColumnModel().getColumn(3).setCellRenderer(new FileAttributesRenderer());

        addMouseListener(new FileTableMouseListener(context));
        addKeyListener(new CustomerKeyListener());
        getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                context.invokeAction(SelectNavigationPanelAction.class, e);
            }
        });

        for (Class<?> actionClass : new Class<?>[]{ScrollToTopAction.class, ScrollToBottomAction.class, ExecuteFileAction.class, NavigatePathUpAction.class, SwitchNavigationPanelAction.class}) {
            context.bindActionToComponent(this, (Class<? extends CustomAction>) actionClass);
        }

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

    public void setPath(File path) {
        this.path = path;
        ((PathTableModel) getModel()).setPath(path);
        rowSorter.sort();
        getSelectionModel().clearSelection();
        getSelectionModel().setSelectionInterval(0, 0);
        requestFocusInWindow();
    }

    public void listFile(File file) {
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
                context.invokeAction(ScrollToLetterInFileTablePaneAction.class, e, Character.valueOf(e.getKeyChar()));
            }
        }
    }
}

