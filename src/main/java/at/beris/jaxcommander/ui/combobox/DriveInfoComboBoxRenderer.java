/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.combobox;

import at.beris.jaxcommander.DriveInfo;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class DriveInfoComboBoxRenderer extends JLabel implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        DriveInfo driveInfo = (DriveInfo) value;
        setText(driveInfo.getPath() + " [" + (driveInfo.getSpaceTotal() / (1024 * 1024 * 1024)) + "G / 50% free]");
        return this;
    }
}
