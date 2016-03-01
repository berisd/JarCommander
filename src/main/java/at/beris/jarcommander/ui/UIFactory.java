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
import at.beris.jarcommander.action.SelectNavigationPanelAction;
import at.beris.jarcommander.exception.ApplicationException;
import at.beris.jarcommander.filesystem.FileSystem;
import at.beris.jarcommander.filesystem.LocalFileSystem;
import at.beris.jarcommander.filesystem.drive.Drive;
import at.beris.virtualfile.File;
import at.beris.jarcommander.ui.combobox.DriveComboBox;
import at.beris.jarcommander.ui.table.FileTablePane;

import javax.swing.*;
import java.awt.*;
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

    public SessionPanel createSessionPanel(String title, FileSystem fileSystem) {
        SessionPanel sessionPanel = null;
        try {
            sessionPanel = new SessionPanel(createNavigationPanel(new LocalFileSystem()), createNavigationPanel(fileSystem));
            context.getSessionsPanel().addTab(title, sessionPanel);
        } catch (ApplicationException e) {
            e.show();
        }
        return sessionPanel;
    }

    public NavigationPanel createNavigationPanel(FileSystem fileSystem) {
        DriveComboBox driveComboBox = new DriveComboBox(context, fileSystem);
        File currentFile = ((Drive) driveComboBox.getSelectedItem()).getFile();
        final FileTablePane fileTablePane = new FileTablePane(context);
        JTextField currentPathTextField = new JTextField();
        currentPathTextField.setText(currentFile.getPath().toString());
        FileTableStatusLabel statusLabel = new FileTableStatusLabel(fileTablePane.getTable());

        for (Component component : new Component[]{currentPathTextField, statusLabel}) {
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    context.invokeAction(SelectNavigationPanelAction.class, e);
                }
            });
        }

        fileTablePane.getTable().setPath(currentFile);

        return new NavigationPanel(fileTablePane, driveComboBox, currentPathTextField, statusLabel);
    }
}
