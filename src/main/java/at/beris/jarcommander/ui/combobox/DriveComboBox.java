/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.combobox;

import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.ParamActionEvent;
import at.beris.jarcommander.filesystem.JFileSystem;
import at.beris.jarcommander.filesystem.drive.JDrive;
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

public class DriveComboBox extends JComboBox<JDrive> {

    private final static Logger LOGGER = Logger.getLogger(DriveComboBox.class.getName());

    private JFileSystem fileSystem;

    public DriveComboBox(JFileSystem fileSystem) {
        super();

        addItemListener(new

                                ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        LOGGER.debug("drivecombo itemStateChanged " + e.getItem().getClass());
                                        JDrive driveInfo = (JDrive) e.getItem();
                                        ActionListener parent = (ActionListener) ((JComponent) e.getSource()).getParent();

                                        if (parent != null) {
                                            ParamActionEvent<JDrive> event = new ParamActionEvent<>(e.getSource(), e.getID(), ActionType.CHANGE_DRIVE.toString(), driveInfo);
                                            parent.actionPerformed(event);
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
            ActionListener parent = (ActionListener) ((Component) e.getSource()).getParent();
            parent.actionPerformed(new ActionEvent(e.getSource(), e.getID(), ActionType.SELECT_NAVIGATION_PANEL.toString()));
        }
    }
}
