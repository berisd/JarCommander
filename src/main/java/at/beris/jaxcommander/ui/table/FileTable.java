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
import at.beris.jaxcommander.ui.ActionScrollPane;
import org.apache.log4j.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileTable extends JTable {
    private final static Logger LOGGER = Logger.getLogger(FileTable.class);

    private TableModel tableModel;
    private TableRowSorter<TableModel> rowSorter;
    private Path path;

    public FileTable() {
        super();
    }

    public FileTable(Path path) {
        this(new PathTableModel(path));
    }

    public FileTable(PathTableModel tableModel) {
        super();
        this.tableModel = tableModel;
        this.path = tableModel.getPath();

        setModel(tableModel);
        getColumnModel().getColumn(0).setCellRenderer(new FileRenderer());
        getColumnModel().getColumn(1).setCellRenderer(new DateRenderer());
        getColumnModel().getColumn(2).setCellRenderer(new FileSizeRenderer());

        addKeyListener(new CustomerKeyListener());

        getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "scrollToTop");
        getActionMap().put("scrollToTop", new ScrollToTopAction());
        getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "scrollToBottom");
        getActionMap().put("scrollToBottom", new ScrollToBottomAction());
        getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterItemAction");
        getActionMap().put("enterItemAction", new EnterItemAction());
        getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "levelUpAction");
        getActionMap().put("levelUpAction", new LevelUpAction());

        rowSorter = new TableRowSorter<>((TableModel) tableModel);
        setRowSorter(rowSorter);

        rowSorter.setComparator(0, new FileDefaultComparator());

        List<RowSorter.SortKey> sortKeys
                = new ArrayList<>();

        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeys);
    }

    public void dispose() {
        LOGGER.debug("dispose");
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

    public Path getPath() {
        return path;
    }

    public void scrollToRow(int rowIndex) {
        JViewport viewport = (JViewport) this.getParent();
        Rectangle rect = this.getCellRect(rowIndex, 0, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        viewport.scrollRectToVisible(rect);
    }

    private class ScrollToTopAction extends AbstractAction {
        public ScrollToTopAction() {
            super();
            putValue(Action.ACTION_COMMAND_KEY, "scrollToTop");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("actionPerformed");
            FileTable table = (FileTable) e.getSource();
            JViewport viewport = (JViewport) table.getParent();
            ActionScrollPane scrollPane = (ActionScrollPane) viewport.getParent();
            if (table.getRowCount() > 0) {
                table.scrollToRow(0);
                table.repaint();
            }
            scrollPane.actionPerformed(e);
        }
    }


    private class ScrollToBottomAction extends AbstractAction {
        public ScrollToBottomAction() {
            super();
            putValue(Action.ACTION_COMMAND_KEY, "scrollToBottom");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("actionPerformed");
            FileTable table = (FileTable) e.getSource();
            JViewport viewport = (JViewport) table.getParent();
            ActionScrollPane scrollPane = (ActionScrollPane) viewport.getParent();
            if (table.getRowCount() > 0) {
                table.scrollToRow(table.getRowCount());
                table.repaint();
            }
            scrollPane.actionPerformed(e);
        }
    }

    private class EnterItemAction extends AbstractAction {
        public EnterItemAction() {
            super();
            putValue(Action.ACTION_COMMAND_KEY, "enterItem");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("actionPerformed");
            JTable table = (JTable) e.getSource();
            JViewport viewport = (JViewport) table.getParent();
            ActionScrollPane scrollPane = (ActionScrollPane) viewport.getParent();
            scrollPane.actionPerformed(e);
        }
    }

    private class LevelUpAction extends AbstractAction {
        public LevelUpAction() {
            super();
            putValue(Action.ACTION_COMMAND_KEY, "levelUp");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("actionPerformed");
            JTable table = (JTable) e.getSource();
            JViewport viewport = (JViewport) table.getParent();
            ActionScrollPane scrollPane = (ActionScrollPane) viewport.getParent();
            scrollPane.actionPerformed(e);
        }
    }

    private class CustomerKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            LOGGER.info("keyPressed");
            super.keyPressed(e);
            FileTable table = (FileTable) e.getSource();
            JViewport viewport = (JViewport) table.getParent();

            char keyChar = (char) Character.toLowerCase(e.getKeyCode());

            if ((keyChar == '.') || (keyChar >= 'a' && keyChar <= 'z')) {
                for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
                    File file = (File) table.getValueAt(rowIndex, 0);
                    String[] fileNameParts = file.toString().split(File.separator);
                    String fileName = fileNameParts[fileNameParts.length - 1];
                    if (fileName.toLowerCase().charAt(0) == Character.toLowerCase(e.getKeyCode())) {
                        Rectangle rect = table.getCellRect(rowIndex, 0, true);
                        Point pt = viewport.getViewPosition();
                        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
                        viewport.scrollRectToVisible(rect);
                        break;
                    }
                }
            }
        }
    }
}
