/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.SelectNavigationPanelAction;
import at.beris.jarcommander.filesystem.JFileSystem;
import at.beris.jarcommander.filesystem.LocalFileSystem;
import at.beris.jarcommander.filesystem.drive.JDrive;
import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.ui.combobox.DriveComboBox;
import at.beris.jarcommander.ui.table.FileTablePane;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIFactory {
    private ApplicationContext context;

    public UIFactory(ApplicationContext context) {
        this.context = context;
    }

    public JTabbedPane createSessionsPanel() {
        JTabbedPane sessionsPanel = new JTabbedPane();
        context.setSessionsPanel(sessionsPanel);
        createSessionPanel("Local", new LocalFileSystem());
        return sessionsPanel;
    }

    public SessionPanel createSessionPanel(String title, JFileSystem fileSystem) {
        SessionPanel sessionPanel = new SessionPanel(createNavigationPanel(new LocalFileSystem()), createNavigationPanel(fileSystem));
        context.getSessionsPanel().addTab(title, sessionPanel);
        return sessionPanel;
    }

    public NavigationPanel createNavigationPanel(JFileSystem fileSystem) {
        fileSystem.open();
        DriveComboBox driveComboBox = new DriveComboBox(context, fileSystem);
        JPath currentPath = ((JDrive) driveComboBox.getSelectedItem()).getPath();
        final FileTablePane fileTablePane = new FileTablePane(context);
        JTextField currentPathTextField = new JTextField();
        currentPathTextField.setText(currentPath.toString());
        FileTableStatusLabel statusLabel = new FileTableStatusLabel(fileTablePane.getTable());

        for (Component component : new Component[]{currentPathTextField, statusLabel}) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    Component parent = ((Component) e.getSource()).getParent();
                    new SelectNavigationPanelAction(context).actionPerformed(new ActionEvent(parent, e.getID(), ActionType.SELECT_NAVIGATION_PANEL.toString()));
                }
            });
        }

        fileTablePane.getTable().setPath(currentPath);

        return new NavigationPanel(fileTablePane, driveComboBox, currentPathTextField, statusLabel);
    }
}
