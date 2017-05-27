/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.combobox;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.action.ChangeDriveAction;
import at.beris.jarcommander.action.SelectNavigationPanelAction;
import at.beris.jarcommander.filesystem.FileSystem;
import at.beris.jarcommander.filesystem.drive.Drive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DriveComboBox extends JComboBox<Drive> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DriveComboBox.class.getName());

    private FileSystem fileSystem;
    private ApplicationContext context;

    public DriveComboBox(ApplicationContext context, FileSystem fileSystem) {
        super();
        this.context = context;
        this.fileSystem = fileSystem;

        addItemListener(new

                                ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        LOGGER.debug("drivecombo itemStateChanged " + e.getItem().getClass());
                                        Drive driveInfo = (Drive) e.getItem();
                                        Component parent = ((Component) e.getSource()).getParent();

                                        if (parent != null) {
                                            DriveComboBox.this.context.invokeAction(SelectNavigationPanelAction.class, e);
                                            DriveComboBox.this.context.invokeAction(ChangeDriveAction.class, e, driveInfo);
                                        }
                                    }
                                });

        addMouseListener(new MouseListener());

        setRenderer(new DriveInfoComboBoxRenderer());

        for (Drive driveInfo : fileSystem.getDriveList()) {
            addItem(driveInfo);
        }
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            LOGGER.debug("mousePressed");
            context.invokeAction(SelectNavigationPanelAction.class, e);
        }
    }
}
