/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui;

import at.beris.jaxcommander.filesystem.LocalFileSystem;
import at.beris.jaxcommander.filesystem.VirtualDrive;
import at.beris.jaxcommander.filesystem.JFileSystem;
import at.beris.jaxcommander.filesystem.path.VirtualPath;
import at.beris.jaxcommander.ui.combobox.DriveComboBox;
import at.beris.jaxcommander.ui.table.FileTablePane;
import org.apache.log4j.Logger;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static at.beris.jaxcommander.action.ActionCommand.SELECT_NAVIGATION_PANEL;

public class SessionPanel extends JTabbedPane implements ActionListener {
    private final static Logger LOGGER = Logger.getLogger(SessionPanel.class);

    private NavigationPanel leftNavigationPanel;
    private NavigationPanel rightNavigationPanel;

    public SessionPanel() {
        super();

        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(1, 2);

        panel.setLayout(layout);
        leftNavigationPanel = createNavigationPanel();
        leftNavigationPanel.setSelected(true);
        panel.add(leftNavigationPanel);
        rightNavigationPanel = createNavigationPanel();
        rightNavigationPanel.setSelected(false);
        panel.add(rightNavigationPanel);

        addTab("Local", panel);
    }

    private NavigationPanel createNavigationPanel() {
        JFileSystem fileSystem = new LocalFileSystem();
        DriveComboBox driveComboBox = new DriveComboBox(fileSystem);
        VirtualPath currentPath = ((VirtualDrive) driveComboBox.getSelectedItem()).getPath();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof NavigationPanel) {
            NavigationPanel navigationPanel = (NavigationPanel) e.getSource();
            leftNavigationPanel.setSelected(leftNavigationPanel.equals(navigationPanel));
            rightNavigationPanel.setSelected(rightNavigationPanel.equals(navigationPanel));
        }
    }

    public NavigationPanel getLeftNavigationPanel() {
        return leftNavigationPanel;
    }

    public NavigationPanel getRightNavigationPanel() {
        return rightNavigationPanel;
    }

    public NavigationPanel getSelectedNavigationPanel() {
        NavigationPanel selectedPanel = null;

        if (selectedPanel == null && leftNavigationPanel.isSelected())
            selectedPanel = leftNavigationPanel;
        if (selectedPanel == null && rightNavigationPanel.isSelected())
            selectedPanel = rightNavigationPanel;

        return selectedPanel;
    }
}
