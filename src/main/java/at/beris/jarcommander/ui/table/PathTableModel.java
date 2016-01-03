/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.filesystem.FileUtils;
import at.beris.jarcommander.filesystem.file.FileManager;
import at.beris.jarcommander.filesystem.file.IFile;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class PathTableModel extends AbstractTableModel {
    private final static Logger LOGGER = Logger.getLogger(PathTableModel.class);

    private static final int columnCount = 4;
    private List<IFile> fileList;
    private IFile path;

    public PathTableModel() {
        fileList = new ArrayList<>();
    }

    public void setPath(IFile path) {
        LOGGER.debug("setFile");
        this.path = path;
        fileList.clear();
        try {
            if (!path.isRoot())
                fileList.add(FileManager.newFile(FileUtils.newUrl(path.getUrl(), "/../")));
            fileList.addAll(path.list());
        } catch (IOException e) {
            Application.logException(e);
        }
    }

    public void listFile(IFile file) {
        LOGGER.debug("listFile " + file);
        fileList.clear();
        try {
            fileList.addAll(file.list());
        } catch (IOException e) {
            Application.logException(e);
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "File";
            case 1:
                return "Last Modified";
            case 2:
                return "Size";
            case 3:
                return "Attr.";

        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return IFile.class;
            case 1:
                return Date.class;
            case 2:
                return Long.class;
            case 3:
                return HashSet.class;
            default:
                return String.class;
        }
    }

    @Override
    public int getRowCount() {
        return fileList.size();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IFile file = fileList.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return file;
            case 1:
                return file.getLastModified();
            case 2:
                return file.getSize();
            case 3:
                return file.getAttributes();
        }
        return null;
    }

}
