/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.combobox;

import at.beris.jarcommander.filesystem.drive.JDrive;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

import static at.beris.jarcommander.helper.Localization.numberFormat;

public class DriveInfoComboBoxRenderer extends JLabel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JDrive driveInfo = (JDrive) value;
        setText(driveInfo.getPath() + " [" + numberFormat().format((double) driveInfo.getSpaceTotal() / (1024 * 1024 * 1024))
                + "G / " + calculateSpaceFreePercentage(driveInfo) + "% free]");
        return this;
    }

    private long calculateSpaceFreePercentage(JDrive driveInfo) {
        if (driveInfo.getSpaceLeft() == 0 || driveInfo.getSpaceTotal() == 0)
            return 0;
        return driveInfo.getSpaceLeft() * 100 / driveInfo.getSpaceTotal();
    }
}
