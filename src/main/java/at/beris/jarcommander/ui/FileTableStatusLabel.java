/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.ui.table.FileTable;

import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import static at.beris.jarcommander.helper.Localization.numberFormat;

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
            JFile file = (JFile) fileTable.getValueAt(rowIndex, 0);
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

            totalSize += file.getSize();
            if (isCellSelected)
                selectedSize += file.getSize();
        }

        setText(numberFormat().format((double) selectedSize / 1024) + "K / " + numberFormat().format((double) totalSize / 1024)
                + "K , " + selectedNoOfFiles + " / " + totalNoOfFiles + " files, " + selectedNoOfDirs + " / " + totalNoOfDirs + " directories");
    }
}
