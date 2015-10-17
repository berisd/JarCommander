/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui;

import at.beris.jaxcommander.ui.table.FileTable;

import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.File;

import static at.beris.jaxcommander.helper.Localization.numberFormat;

public class FileTableStatusLabel extends JLabel {
    private FileTable fileTable;

    public FileTableStatusLabel(FileTable fileTable) {
        super();
        this.fileTable = fileTable;

        fileTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                FileTableStatusLabel.this.updateStatus();
            }
        });

        updateStatus();
    }

    public void updateStatus() {
        long selectedSize = 0;
        long selectedNoOfFiles = 0;
        long selectedNoOfDirs = 0;
        long totalSize = 0;
        long totalNoOfFiles = 0;
        long totalNoOfDirs = 0;

        for (int rowIndex = 0; rowIndex < fileTable.getRowCount(); rowIndex++) {
            File file = (File) fileTable.getValueAt(rowIndex, 0);
            boolean isCellSelected = fileTable.isCellSelected(rowIndex, 0);

            if (file.isDirectory()) {
                totalNoOfDirs++;
                if (isCellSelected)
                    selectedNoOfDirs++;
            } else {
                totalNoOfFiles++;
                if (isCellSelected)
                    selectedNoOfFiles++;
            }

            totalSize += file.length();
            if (isCellSelected)
                selectedSize += file.length();
        }

        setText(numberFormat().format((double) selectedSize / 1024) + "K / " + numberFormat().format((double) totalSize / 1024)
                + "K , " + selectedNoOfFiles + " / " + totalNoOfFiles + " files, " + selectedNoOfDirs + " / " + totalNoOfDirs + " directories");
    }
}
