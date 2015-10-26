/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.list;

import at.beris.jarcommander.model.SiteModel;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;

public class SiteCellRenderer extends JLabel implements ListCellRenderer<SiteModel> {

    @Override
    public Component getListCellRendererComponent(JList<? extends SiteModel> list, SiteModel value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value.getUsername() == null && value.getHostname() == null)
            setText("<New site>");
        else
            setText(value.getUsername() + "@" + value.getHostname());

        setForeground(isSelected ? SELECTION_FOREGROUND_COLOR : Color.BLACK);

        return this;
    }
}
