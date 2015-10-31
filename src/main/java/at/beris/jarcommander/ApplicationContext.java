/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander;

import at.beris.jarcommander.action.ActionFactory;
import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.SelectNavigationPanelAction;
import at.beris.jarcommander.filesystem.JFileSystem;
import at.beris.jarcommander.filesystem.LocalFileSystem;
import at.beris.jarcommander.filesystem.drive.JDrive;
import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.ui.ApplicationFrame;
import at.beris.jarcommander.ui.FileTableStatusLabel;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import at.beris.jarcommander.ui.UIFactory;
import at.beris.jarcommander.ui.button.ButtonFactory;
import at.beris.jarcommander.ui.combobox.DriveComboBox;
import at.beris.jarcommander.ui.table.FileTablePane;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ApplicationContext {
    public final static String HOME_DIRECTORY = System.getProperty("user.home") + File.separator + ".JarCommander";
    public final static Color SELECTION_FOREGROUND_COLOR = Color.BLUE;

    private JTabbedPane sessionsPanel;

    private ActionFactory actionFactory;
    private ButtonFactory buttonFactory;
    private UIFactory uiFactory;
    private ApplicationFrame applicationFrame;


    public ApplicationContext() {
    }

    public ApplicationFrame getApplicationFrame() {
        if (applicationFrame == null) {
            applicationFrame = new ApplicationFrame(this);
        }
        return applicationFrame;
    }

    public ActionFactory getActionFactory() {
        if (actionFactory == null) {
            actionFactory = new ActionFactory(this);
        }
        return actionFactory;
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

    public JTabbedPane getSessionsPanel() {
        return sessionsPanel;
    }

    public void setSessionsPanel(JTabbedPane sessionsPanel) {
        this.sessionsPanel = sessionsPanel;
    }
}
