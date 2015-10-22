/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.combobox;

import at.beris.jaxcommander.action.ActionCommand;
import at.beris.jaxcommander.action.ParamActionEvent;
import at.beris.jaxcommander.filesystem.VirtualDrive;
import at.beris.jaxcommander.filesystem.JFileSystem;
import org.apache.log4j.Logger;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static at.beris.jaxcommander.action.ActionCommand.SELECT_NAVIGATION_PANEL;

public class DriveComboBox extends JComboBox<VirtualDrive> {

    private final static Logger LOGGER = Logger.getLogger(DriveComboBox.class.getName());

    public DriveComboBox(JFileSystem fileSystem) {
        super();

        addItemListener(new

                                ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        LOGGER.debug("drivecombo itemStateChanged " + e.getItem().getClass());
                                        VirtualDrive driveInfo = (VirtualDrive) e.getItem();
                                        ActionListener parent = (ActionListener) ((JComponent) e.getSource()).getParent();

                                        if (parent != null) {
                                            ParamActionEvent<VirtualDrive> event = new ParamActionEvent<>(e.getSource(), e.getID(), ActionCommand.CHANGE_DRIVE, driveInfo);
                                            parent.actionPerformed(event);
                                        }
                                    }
                                });

        addMouseListener(new MouseListener());

        setRenderer(new DriveInfoComboBoxRenderer());

        for (VirtualDrive driveInfo : fileSystem.getDriveList()) {
            addItem(driveInfo);
        }
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            LOGGER.debug("mousePressed");
            ActionListener parent = (ActionListener) ((Component) e.getSource()).getParent();
            parent.actionPerformed(new ActionEvent(e.getSource(), e.getID(), SELECT_NAVIGATION_PANEL));
        }
    }
}
