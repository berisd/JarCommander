/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PathTableModel extends AbstractTableModel {
    private static final int columnCount = 3;
    private List<Path> pathList;

    public PathTableModel(Path parentPath) {
        DirectoryStream<Path> directoryStream = null;
        try {
            directoryStream = Files.newDirectoryStream(parentPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.pathList = new ArrayList<>();

        for (Path path : directoryStream)
            pathList.add(path);
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: return "File";
            case 1: return "Date";
            case 2: return "Size";

        }
        return "";
    }

    @Override
    public int getRowCount() {
        return pathList.size();
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Path path = pathList.get(rowIndex);
        File file = path.toFile();

        switch (columnIndex) {
            case 0:
                return file.getName();
            case 1:
                return new java.util.Date(file.lastModified() * 1000).toString();
            case 2:
                return String.valueOf(file.length() / 1024) + "k";
        }
        return null;
    }
}
