/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander;

import at.beris.jarcommander.filesystem.LocalFileSystem;
import at.beris.jarcommander.filesystem.JFileSystem;
import at.beris.jarcommander.ui.FileTableStatusLabel;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.combobox.DriveComboBox;
import at.beris.jarcommander.ui.table.FileTablePane;
import org.junit.Before;

import javax.swing.JTextField;

public class NavigationPanelTest {

    private NavigationPanel navPanel;
    private FileTablePane fileTablePane;

    @Before
    public void setUp() {
        JFileSystem fileSystem = new LocalFileSystem();
        fileTablePane = new FileTablePane();
        DriveComboBox driveComboBox = new DriveComboBox(fileSystem);
        JTextField currentPathTextField = new JTextField();
        FileTableStatusLabel statusLabel = new FileTableStatusLabel(fileTablePane.getTable());
        navPanel = new NavigationPanel(fileSystem, fileTablePane, driveComboBox, currentPathTextField, statusLabel);
    }
}