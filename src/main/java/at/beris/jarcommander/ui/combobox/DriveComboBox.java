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
import at.beris.jarcommander.filesystem.JFileSystem;
import at.beris.jarcommander.filesystem.drive.JDrive;
import org.apache.log4j.Logger;

import javax.swing.JComboBox;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DriveComboBox extends JComboBox<JDrive> {

    private final static Logger LOGGER = Logger.getLogger(DriveComboBox.class.getName());

    private JFileSystem fileSystem;
    private ApplicationContext context;

    public DriveComboBox(ApplicationContext context, JFileSystem fileSystem) {
        super();
        this.context = context;
        this.fileSystem = fileSystem;

        addItemListener(new

                                ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        LOGGER.debug("drivecombo itemStateChanged " + e.getItem().getClass());
                                        JDrive driveInfo = (JDrive) e.getItem();
                                        Component parent = ((Component) e.getSource()).getParent();

                                        if (parent != null) {
                                            DriveComboBox.this.context.invokeAction(SelectNavigationPanelAction.class, e);
                                            DriveComboBox.this.context.invokeAction(ChangeDriveAction.class, e, driveInfo);
                                        }
                                    }
                                });

        addMouseListener(new MouseListener());

        setRenderer(new DriveInfoComboBoxRenderer());

        for (JDrive driveInfo : fileSystem.getDriveList()) {
            addItem(driveInfo);
        }
    }

    public JFileSystem getFileSystem() {
        return fileSystem;
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            LOGGER.debug("mousePressed");
            context.invokeAction(SelectNavigationPanelAction.class, e);
        }
    }
}
