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
import at.beris.jaxcommander.ui.table.FileTablePane;
import org.junit.Before;
import org.junit.Test;

import javax.swing.JTextField;

import static org.junit.Assert.assertEquals;

public class NavigationPanelTest {

    private NavigationPanel navPanel;
    private FileTablePane fileTablePane;

    @Before
    public void setUp() {
        fileTablePane = new FileTablePane();
        DriveComboBox driveComboBox = new DriveComboBox();
        JTextField currentPathTextField = new JTextField();
        navPanel = new NavigationPanel(fileTablePane, driveComboBox, currentPathTextField);
    }

    @Test
    public void whenPanelGetsSelectedThenTableSelectionMustBeEmpty() throws Exception {
        fileTablePane.getTable().getSelectionModel().addSelectionInterval(1, 3);
        assertEquals(3, navPanel.getFileTablePane().getTable().getSelectedRowCount());

        navPanel.setSelected(true);
        assertEquals(0, navPanel.getFileTablePane().getTable().getSelectedRowCount());
    }
}