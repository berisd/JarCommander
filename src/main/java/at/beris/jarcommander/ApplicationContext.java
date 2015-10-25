/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander;

import at.beris.jarcommander.filesystem.JFileSystem;
import at.beris.jarcommander.filesystem.LocalFileSystem;
import at.beris.jarcommander.filesystem.drive.JDrive;
import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.ui.FileTableStatusLabel;
import at.beris.jarcommander.ui.NavigationPanel;
import at.beris.jarcommander.ui.SessionPanel;
import at.beris.jarcommander.ui.combobox.DriveComboBox;
import at.beris.jarcommander.ui.table.FileTablePane;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static at.beris.jarcommander.action.ActionCommand.SELECT_NAVIGATION_PANEL;

public class ApplicationContext {
    private static JTabbedPane sessionsPanel;

    private ApplicationContext() {
    }

    public static JTabbedPane createSessionsPanel() {
        if (sessionsPanel == null)
            sessionsPanel = new JTabbedPane();
        sessionsPanel.addTab("Local", createSessionPanel(new LocalFileSystem()));
        return sessionsPanel;
    }

    public static JTabbedPane getSessionsPanel() {
        return sessionsPanel;
    }

    public static SessionPanel createSessionPanel(JFileSystem fileSystem) {
        SessionPanel sessionPanel = new SessionPanel(createNavigationPanel(new LocalFileSystem()), createNavigationPanel(fileSystem));
        return sessionPanel;
    }

    public static NavigationPanel createNavigationPanel(JFileSystem fileSystem) {
        fileSystem.open();
        DriveComboBox driveComboBox = new DriveComboBox(fileSystem);
        JPath currentPath = ((JDrive) driveComboBox.getSelectedItem()).getPath();
        final FileTablePane fileTablePane = new FileTablePane(currentPath);
        JTextField currentPathTextField = new JTextField();
        currentPathTextField.setText(currentPath.toString());
        FileTableStatusLabel statusLabel = new FileTableStatusLabel(fileTablePane.getTable());

        for (Component component : new Component[]{currentPathTextField, statusLabel}) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    ActionListener parent = (ActionListener) ((Component) e.getSource()).getParent();
                    parent.actionPerformed(new ActionEvent(e.getSource(), e.getID(), SELECT_NAVIGATION_PANEL));
                }
            });
        }

        return new NavigationPanel(fileSystem, fileTablePane, driveComboBox, currentPathTextField, statusLabel);
    }
}
