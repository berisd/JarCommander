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
import org.apache.log4j.Logger;

import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
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


}
