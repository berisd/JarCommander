/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.combobox;

import at.beris.jarcommander.filesystem.drive.Drive;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static at.beris.jarcommander.helper.Localization.numberFormat;
import static at.beris.jarcommander.Application.logException;

public class DriveInfoComboBoxRenderer extends JLabel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Drive driveInfo = (Drive) value;
        try {
            setText(driveInfo.getFile().getPath() + " [" + numberFormat().format((double) driveInfo.getSpaceTotal() / (1024 * 1024 * 1024))
                    + "G / " + calculateSpaceFreePercentage(driveInfo) + "% free]");
        } catch (IOException e) {
            logException(e);
        }
        return this;
    }

    private long calculateSpaceFreePercentage(Drive driveInfo) {
        if (driveInfo.getSpaceLeft() == 0 || driveInfo.getSpaceTotal() == 0)
            return 0;
        return driveInfo.getSpaceLeft() * 100 / driveInfo.getSpaceTotal();
    }
}
