/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.action.ActionFactory;
import at.beris.jarcommander.action.CustomAction;
import at.beris.jarcommander.action.ExecuteFileAction;
import at.beris.jarcommander.action.NavigatePathUpAction;
import at.beris.jarcommander.action.ScrollToLetterInFileTablePaneAction;
import org.junit.Before;
import org.junit.Test;

import javax.swing.JDialog;
import java.awt.AWTException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class FileTableTest {

    FileTable fileTable;
    ExecuteFileAction executeFileAction;
    NavigatePathUpAction navigatePathUpAction;
    ScrollToLetterInFileTablePaneAction scrollToLetterAction;
    ApplicationContext appContext;
    ActionFactory actionFactory;

    @Before
    public void setup() throws AWTException {
        appContext = new ApplicationContext();

        Window parentComponent = new JDialog();
        appContext.setApplicationWindow(parentComponent);

        actionFactory = createActionFactoryMock();
        appContext.setActionFactory(actionFactory);

        mockActions();

        fileTable = new FileTable(appContext);

        parentComponent.add(fileTable);
        parentComponent.setVisible(true);
    }

    @Test
    public void whenEnterKeyIsPressedParentMustReceiveExecuteFileActionCommand() {
        fileTable.dispatchEvent(new KeyEvent(fileTable, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, ' '));
        verify(executeFileAction).actionPerformed(any(ActionEvent.class));
    }

    @Test
    public void whenBackspaceKeyIsPressedParentMustReceiveNavigatePathUpActionCommand() {
        fileTable.dispatchEvent(new KeyEvent(fileTable, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_BACK_SPACE, ' '));
        verify(navigatePathUpAction).actionPerformed(any(ActionEvent.class));
    }

    @Test
    public void whenAnyKeyIsPressedParentMustReceiveKeyPressedActionCommand() {
        fileTable.dispatchEvent(new KeyEvent(fileTable, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'a'));
        verify(scrollToLetterAction).actionPerformed(any(ActionEvent.class));
    }

    private ActionFactory createActionFactoryMock() {
        ActionFactory actionFactory = spy(ActionFactory.class);
        Map<Class<? extends CustomAction>, CustomAction> actionMap = new HashMap<>();
        actionFactory.setActionMap(actionMap);
        return actionFactory;
    }

    private void mockActions() {
        executeFileAction = spy(new ExecuteFileAction(appContext));
        doNothing().when(executeFileAction).actionPerformed(any(ActionEvent.class));
        when(actionFactory.getAction(ExecuteFileAction.class)).thenReturn(executeFileAction);

        navigatePathUpAction = spy(new NavigatePathUpAction(appContext));
        doNothing().when(navigatePathUpAction).actionPerformed(any(ActionEvent.class));
        when(actionFactory.getAction(NavigatePathUpAction.class)).thenReturn(navigatePathUpAction);

        scrollToLetterAction = spy(new ScrollToLetterInFileTablePaneAction(appContext));
        doNothing().when(scrollToLetterAction).actionPerformed(any(ActionEvent.class));
        when(actionFactory.getAction(ScrollToLetterInFileTablePaneAction.class)).thenReturn(scrollToLetterAction);
    }
}









































