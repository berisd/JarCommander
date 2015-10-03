/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.Application;
import at.beris.jaxcommander.FileDefaultComparator;
import at.beris.jaxcommander.PathTableModel;
import org.apache.log4j.Logger;

import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.Timer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

public class FileTable extends JTable {
    private final static Logger LOGGER = Logger.getLogger(FileTable.class);

    private MouseAdapter mouseEventHandler;
    private TableModel tableModel;
    private TableRowSorter<TableModel> rowSorter;
    private FileTableListener fileTableListener;

    private WatchService watchService;
    private WatchKey watchKey;
    private Timer timer;
    private Path path;

    public FileTable(Path path) {
        super();
        this.path = path;
        mouseEventHandler = new FileTableMouseListener();
        tableModel = new PathTableModel(path);

        setModel(tableModel);
        getColumnModel().getColumn(0).setCellRenderer(new FileRenderer());
        getColumnModel().getColumn(1).setCellRenderer(new DateRenderer());
        getColumnModel().getColumn(2).setCellRenderer(new FileSizeRenderer());

        rowSorter = new TableRowSorter<>(tableModel);
        setRowSorter(rowSorter);

        rowSorter.setComparator(0, new FileDefaultComparator());

        List<RowSorter.SortKey> sortKeys
                = new ArrayList<RowSorter.SortKey>();

        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeys);

        addMouseListener(mouseEventHandler);

        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            Application.logException(e);
        }

        try {
            watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            Application.logException(e);
        }

        timer = new Timer(1000, new RefreshDirectoryTask());
        timer.start();
    }

    public void setFileTableListener(FileTableListener fileTableListener) {
        this.fileTableListener = fileTableListener;
    }

    private class FileTableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                JTable table = (JTable) e.getSource();
                int rowIndex = convertRowIndexToModel(table.getSelectedRow());
                LOGGER.info("Double Clicked on row with index " + rowIndex);

                File file = (File) table.getModel().getValueAt(rowIndex, 0);

                if (file.isDirectory()) {
                    changeDirectory(file.toPath());
                }
            }
        }
    }

    public void changeDirectory(Path newPath) {
        fileTableListener.changeDirectory(newPath);

        if (watchKey != null) {
            watchKey.cancel();
        }

        if (newPath.toString().equals("..")) {
            path = path.getParent();
        } else {
            path = newPath.normalize();
        }

        try {
            watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            Application.logException(e);
        }

        refreshDirectory();
    }

    private void refreshDirectory() {
        ((PathTableModel) getModel()).setPath(path);
        rowSorter.sort();
        repaint();
    }

    private class RefreshDirectoryTask implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            LOGGER.info(Thread.currentThread().getName() + " RefreshDirectoryTask");
            if (watchKey != null) {
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                if (watchEvents.size() > 0) {
                    refreshDirectory();
                }
                watchKey.reset();
            }
        }
    }

    public void dispose() {
        LOGGER.debug("dispose");
        timer.stop();
        timer = null;
    }
}
