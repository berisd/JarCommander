/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import at.beris.jaxcommander.ui.NavigationPanel;
import at.beris.jaxcommander.ui.combobox.DriveComboBox;
import at.beris.jaxcommander.ui.table.FileTable;
import org.junit.Before;
import org.junit.Test;

import javax.swing.JTextField;

import static org.junit.Assert.assertEquals;

public class NavigationPanelTest {

    private NavigationPanel navPanel;
    private FileTable fileTable;

    @Before
    public void setUp() {
        fileTable = new FileTable();
        DriveComboBox driveComboBox = new DriveComboBox();
        JTextField currentPathTextField = new JTextField();
        navPanel = new NavigationPanel(fileTable, driveComboBox, currentPathTextField);
    }

    @Test
    public void whenPanelGetsSelectedThenTableSelectionMustBeEmpty() throws Exception {
        fileTable.getSelectionModel().addSelectionInterval(1, 3);
        assertEquals(3, navPanel.getFileTable().getSelectedRowCount());

        navPanel.setSelected(true);
        assertEquals(0, navPanel.getFileTable().getSelectedRowCount());
    }
}