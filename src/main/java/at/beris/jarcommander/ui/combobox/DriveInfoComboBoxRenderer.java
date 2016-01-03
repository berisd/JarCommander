/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.combobox;

import at.beris.jarcommander.filesystem.drive.IDrive;

import javax.swing.*;
import java.awt.*;

import static at.beris.jarcommander.helper.Localization.numberFormat;

public class DriveInfoComboBoxRenderer extends JLabel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        IDrive driveInfo = (IDrive) value;
        setText(driveInfo.getFile().getPath() + " [" + numberFormat().format((double) driveInfo.getSpaceTotal() / (1024 * 1024 * 1024))
                + "G / " + calculateSpaceFreePercentage(driveInfo) + "% free]");
        return this;
    }

    private long calculateSpaceFreePercentage(IDrive driveInfo) {
        if (driveInfo.getSpaceLeft() == 0 || driveInfo.getSpaceTotal() == 0)
            return 0;
        return driveInfo.getSpaceLeft() * 100 / driveInfo.getSpaceTotal();
    }
}
