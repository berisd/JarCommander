/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander;

import at.beris.jarcommander.action.ActionEventFactory;
import at.beris.jarcommander.action.ActionFactory;
import at.beris.jarcommander.action.CustomAction;
import at.beris.jarcommander.filesystem.file.FileFactory;
import at.beris.jarcommander.ui.ApplicationFrame;
import at.beris.jarcommander.ui.UIFactory;
import at.beris.jarcommander.ui.button.ButtonFactory;
import org.reflections.Reflections;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ApplicationContext {
    public final static String HOME_DIRECTORY = System.getProperty("user.home") + File.separator + ".JarCommander";
    public final static Color SELECTION_FOREGROUND_COLOR = Color.BLUE;

    private JTabbedPane sessionsPanel;

    private ActionFactory actionFactory;
    private ActionEventFactory actionEventFactory;
    private ButtonFactory buttonFactory;
    private UIFactory uiFactory;
    private Window applicationWindow;
    private FileFactory fileFactory;

    public Window getApplicationWindow() {
        if (applicationWindow == null) {
            applicationWindow = new ApplicationFrame(this);
        }
        return applicationWindow;
    }

    public void setApplicationWindow(Window applicationWindow) {
        this.applicationWindow = applicationWindow;
    }

    public ActionFactory getActionFactory() {
        if (actionFactory == null) {
            actionFactory = new ActionFactory(this);
        }
        return actionFactory;
    }

    public void setActionFactory(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    public FileFactory getFileFactory() {
        if (fileFactory == null) {
            fileFactory = new FileFactory();
        }
        return fileFactory;
    }

    public void setFileFactory(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }

    public ActionEventFactory getActionEventFactory() {
        if (actionEventFactory == null) {
            actionEventFactory = new ActionEventFactory();
        }
        return actionEventFactory;
    }

    public ButtonFactory getButtonFactory() {
        if (buttonFactory == null) {
            buttonFactory = new ButtonFactory(this);
        }
        return buttonFactory;
    }

    public UIFactory getUiFactory() {
        if (uiFactory == null) {
            uiFactory = new UIFactory(this);
        }
        return uiFactory;
    }

    public Reflections getReflections(final Object... params) {
        return new Reflections(params);
    }

    public JTabbedPane getSessionsPanel() {
        return sessionsPanel;
    }

    public void setSessionsPanel(JTabbedPane sessionsPanel) {
        this.sessionsPanel = sessionsPanel;
    }

    public Object findComponentAncestor(Class ancestorClass, Component component) {
        Component parent = component.getParent();
        while (parent != null) {
            if (parent.getClass().equals(ancestorClass))
                break;
            parent = parent.getParent();
        }

        return parent;
    }

    public void invokeAction(Class actionClass, AWTEvent sourceEvent) {
        ActionEvent actionEvent = getActionEventFactory().createActionEvent(sourceEvent, actionClass);
        getActionFactory().getAction(actionClass).actionPerformed(actionEvent);
    }

    public <T> void invokeAction(Class actionClass, AWTEvent sourceEvent, T param) {
        ActionEvent actionEvent = getActionEventFactory().createActionEvent(sourceEvent, actionClass, param);
        getActionFactory().getAction(actionClass).actionPerformed(actionEvent);
    }

    public void bindActionToComponent(JComponent component, Class<? extends CustomAction> actionClass) {
        CustomAction customAction = actionFactory.getAction(actionClass);
        if (customAction != null) {
            component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(customAction.getKeyStroke(), actionClass.getSimpleName());
            component.getActionMap().put(actionClass.getSimpleName(), customAction);
        }
    }
}
