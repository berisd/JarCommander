/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.filesystem.file.JFile;
import at.beris.jaxcommander.filesystem.file.JFileFactory;
import at.beris.jaxcommander.filesystem.path.JPath;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static at.beris.jaxcommander.Application.logException;

public class PathTableModel extends AbstractTableModel {
    private final static Logger LOGGER = Logger.getLogger(PathTableModel.class);

    private static final int columnCount = 3;
    private List<JFile> fileList;


    private JPath path;

    public PathTableModel(JPath path) {
        this.path = path;
        fileList = new ArrayList<>();
        setPath(path);
    }

    public void setPath(JPath path) {
        this.path = path;
        fileList.clear();

        if (!path.toString().equals("/") && StringUtils.countMatches(path.toString(), FileSystems.getDefault().getSeparator()) >= 1) {
            fileList.add(JFileFactory.newInstance(new File("..")));
        }

        fileList.addAll(path.getEntries());
    }

    public void listFile(File file) {
        LOGGER.debug("listFile " + file);
        fileList.clear();
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            ArchiveInputStream ais = factory.createArchiveInputStream(fis);

            ArchiveEntry ae;
            while ((ae = ais.getNextEntry()) != null) {
                fileList.add(JFileFactory.newInstance(ae));
            }
            ais.close();
            fis.close();
        } catch (FileNotFoundException e) {
            logException(e);
        } catch (ArchiveException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
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
        }
        return null;
    }

}
