/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.file.JFileFactory;
import at.beris.jarcommander.filesystem.path.JPath;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class PathTableModel extends AbstractTableModel {
    private final static Logger LOGGER = Logger.getLogger(PathTableModel.class);

    private static final int columnCount = 4;
    private List<JFile> fileList;
    private JPath path;

    public PathTableModel() {
        fileList = new ArrayList<>();
    }

    public void setPath(JPath path) {
        LOGGER.debug("setPath");
        this.path = path;
        fileList.clear();

        if (!path.toString().equals("/") && StringUtils.countMatches(path.toString(), FileSystems.getDefault().getSeparator()) >= 1) {
            fileList.add(JFileFactory.newInstance(new File("..")));
        }

        fileList.addAll(path.getEntries());
    }

    public void listFile(JFile file) {
        LOGGER.debug("listFile " + file);
        fileList.clear();

        if (!path.toString().equals("/") && StringUtils.countMatches(path.toString(), FileSystems.getDefault().getSeparator()) >= 1) {
            JFile backFile = JFileFactory.newInstance(new File(".."));
            backFile.setParentFile(file);
            fileList.add(backFile);
        }

        fileList.addAll(file.list());
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
                return JFile.class;
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
        JFile file = fileList.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return file;
            case 1:
                return file.getLastModified();
            case 2:
                return file.getSize();
            case 3:
                return file.attributes();
        }
        return null;
    }

}
