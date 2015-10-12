/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.combobox;

import at.beris.jaxcommander.Application;
import at.beris.jaxcommander.DriveInfo;
import at.beris.jaxcommander.action.ActionCommand;
import at.beris.jaxcommander.action.ParamActionEvent;
import org.apache.log4j.Logger;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DriveComboBox extends JComboBox<DriveInfo> {

    private final static Logger LOGGER = Logger.getLogger(DriveComboBox.class.getName());

    public DriveComboBox() {
        super();

        addItemListener(new

                                ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        LOGGER.info("drivecombo itemStateChanged " + e.getItem().getClass());
                                        DriveInfo driveInfo = (DriveInfo) e.getItem();
                                        ActionListener parent = (ActionListener) ((JComponent) e.getSource()).getParent();

                                        if (parent != null) {
                                            ParamActionEvent<DriveInfo> event = new ParamActionEvent<>(e.getSource(), e.getID(), ActionCommand.DRIVE_CHANGED, driveInfo);
                                            parent.actionPerformed(event);
                                        }
                                    }
                                });

        setRenderer(new DriveInfoComboBoxRenderer());

        for (DriveInfo driveInfo : Application.getDriveInfo()) {
            addItem(driveInfo);
        }
    }
}
