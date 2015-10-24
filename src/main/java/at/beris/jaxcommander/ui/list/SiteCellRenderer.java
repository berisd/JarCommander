/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.list;

import at.beris.jaxcommander.model.SiteModel;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class SiteCellRenderer extends JLabel implements ListCellRenderer<SiteModel> {

    @Override
    public Component getListCellRendererComponent(JList<? extends SiteModel> list, SiteModel value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getUsername() + "@" + value.getHostname());
        return this;
    }
}
