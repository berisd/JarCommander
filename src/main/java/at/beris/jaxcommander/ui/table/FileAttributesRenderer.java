/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.filesystem.file.Attribute;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.util.Set;

public class FileAttributesRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Set<Attribute> attributes = (Set<Attribute>) value;

        String shortText = "";
        String longText = "";
        for (Attribute attribute : attributes) {
            shortText += attribute.shortName();
            if (longText != "")
                longText += "<br>";
            longText += attribute.longName();
        }

        longText = "<html>" + longText + "</html>";

        setText(shortText);
        setToolTipText(longText);

        if (isSelected) {
            setForeground(Color.RED);
        } else {
            setForeground(table.getForeground());
        }

        return this;
    }
}
