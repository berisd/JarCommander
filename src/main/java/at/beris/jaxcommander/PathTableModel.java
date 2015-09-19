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
    private List<File> fileList;

    public PathTableModel(Path path) {
        this.fileList = new ArrayList<>();
        setPath(path);
    }

    public void setPath(Path path) {
        fileList.clear();

        DirectoryStream<Path> directoryStream = null;
        try {
            directoryStream = Files.newDirectoryStream(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Path childPath : directoryStream)
            fileList.add(childPath.toFile());

        fileList.sort(Application.fileDefaultComparator);
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

        }
        return "";
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
        File file = fileList.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return file.getName();
            case 1:
                return new java.util.Date(file.lastModified() * 1000).toString();
            case 2:
                return file.isDirectory() ? "<DIR>" : String.valueOf(file.length() / 1024) + "k";
        }
        return null;
    }

}
