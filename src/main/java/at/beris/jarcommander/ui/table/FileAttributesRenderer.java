/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.virtualfile.Attribute;
import at.beris.virtualfile.UnixAttribute;
import at.beris.virtualfile.WindowsAttribute;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;

public class FileAttributesRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        StringBuilder shortText = new StringBuilder("");
        StringBuilder longText = new StringBuilder("");

        Set<Attribute> fileAttributes = (Set<Attribute>) value;

        Attribute firstFileAttribute = null;
        if (fileAttributes != null && fileAttributes.iterator().hasNext()) {
            firstFileAttribute = fileAttributes.iterator().next();
        }

        if (firstFileAttribute != null) {
            List<Attribute> attributes;
            if (firstFileAttribute instanceof UnixAttribute)
                attributes = Arrays.asList((Attribute[]) UnixAttribute.values());
            else
                attributes = Arrays.asList((Attribute[]) WindowsAttribute.values());

            for (Attribute attribute : attributes) {
                if (fileAttributes.contains(attribute)) {
                    if (attribute == UnixAttribute.S_IXUSR && fileAttributes.contains(UnixAttribute.S_ISUID))
                        continue;
                    if (attribute == UnixAttribute.S_IXGRP && fileAttributes.contains(UnixAttribute.S_ISGID))
                        continue;
                    shortText.append(attribute.shortName());
                    if (longText.length() > 0)
                        longText.append("<br>");
                    longText.append(attribute.longName());
                } else if (attribute != WindowsAttribute.HIDDEN && attribute != UnixAttribute.S_ISUID && attribute != UnixAttribute.S_ISGID) {
                    shortText.append('-');
                    longText.append('-');
                }
            }

            longText.insert(0, "<html>");
            longText.append("</html>");

            setText(shortText.toString());
            setToolTipText(longText.toString());
        } else {
            setText("");
            setToolTipText("");
        }

        if (isSelected) {
            setForeground(SELECTION_FOREGROUND_COLOR);
        } else {
            setForeground(table.getForeground());
        }

        return this;
    }
}
