/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import org.apache.commons.lang3.StringUtils;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PathTableModel extends AbstractTableModel {
    private static final int columnCount = 3;
    private List<File> fileList;

    private DateFormat dateFormat;
    private NumberFormat numberFormat;

    public PathTableModel(Path path) {
        dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
        numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        fileList = new ArrayList<>();
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

        if (!path.toString().toString().equals("/") && StringUtils.countMatches(path.toString(), FileSystems.getDefault().getSeparator()) >= 1) {
            fileList.add(new File(".."));
        }

        for (Path childPath : directoryStream)
            fileList.add(childPath.toFile());

//        fileList.sort(Application.fileDefaultComparator);
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
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return File.class;
            case 1:
                return Date.class;
            case 2:
                return Long.class;
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
        File file = fileList.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return file;
            case 1:
//                return dateFormat.format(new java.util.Date(file.lastModified()));
                return new java.util.Date(file.lastModified());
            case 2:
//                return file.isDirectory() ? "<DIR>" : numberFormat.format((double) file.length() / 1024) + "K";
                return file.length();
        }
        return null;
    }

}
