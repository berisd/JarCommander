/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.action.ActionCommand;
import at.beris.jarcommander.filesystem.path.LocalPath;
import org.junit.Before;
import org.junit.Test;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class FileTableTest {
    private static final String MESSAGE_EXPECTED = "Parent didn't receive ActionEvent with expected ActionCommand";

    FileTable fileTable;
    ParentComponent parentComponent;

    @Before
    public void setup() throws AWTException {
        parentComponent = new ParentComponent();
        fileTable = new FileTable(new LocalPath(new File("/").toPath()));

        fileTable.setSelectionModel(new DefaultListSelectionModel());

        parentComponent.add(fileTable);
        parentComponent.setVisible(true);

        fileTable.requestFocusInWindow();
        fileTable.setColumnSelectionInterval(0, 0);
    }

    @Test
    public void whenEnterKeyIsPressedParentMustReceiveExecuteFileActionCommand() {
        fileTable.dispatchEvent(new KeyEvent(fileTable, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, ' '));
        assertTrue(MESSAGE_EXPECTED, parentComponent.getActionEventByActionCommand().containsKey(ActionCommand.EXECUTE_FILE));
    }

    @Test
    public void whenBackspaceKeyIsPressedParentMustReceiveNavigatePathUpActionCommand() {
        fileTable.dispatchEvent(new KeyEvent(fileTable, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_BACK_SPACE, ' '));
        assertTrue(MESSAGE_EXPECTED, parentComponent.getActionEventByActionCommand().containsKey(ActionCommand.NAVIGATE_PATH_UP));
    }

    @Test
    public void whenAnyKeyIsPressedParentMustReceiveKeyPressedActionCommand() {
        fileTable.dispatchEvent(new KeyEvent(fileTable, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, ' '));
        assertTrue(MESSAGE_EXPECTED, parentComponent.getActionEventByActionCommand().containsKey(ActionCommand.KEY_PRESSED));
    }

    public class ParentComponent extends JDialog implements ActionListener {
        private Map<String, ActionEvent> actionEventByActionCommand;

        public ParentComponent() {
            super();
            actionEventByActionCommand = new HashMap();
        }

        public Map<String, ActionEvent> getActionEventByActionCommand() {
            return actionEventByActionCommand;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            actionEventByActionCommand.put(e.getActionCommand(), e);
        }
    }
}









































