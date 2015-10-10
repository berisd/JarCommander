/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.combobox;

import at.beris.jaxcommander.Application;
import at.beris.jaxcommander.DriveInfo;

import javax.swing.JComboBox;

public class DriveComboBox extends JComboBox<DriveInfo> {
    public DriveComboBox() {
        super();
        setRenderer(new DriveInfoComboBoxRenderer());

        for (DriveInfo driveInfo : Application.getDriveInfo()) {
            addItem(driveInfo);
        }
    }
}
