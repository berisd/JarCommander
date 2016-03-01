/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.file.attribute.FileTime;
import java.util.Date;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;
import static at.beris.jarcommander.helper.Localization.dateFormat;

public class FileTimeRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        FileTime filetime = (FileTime) value;

        if (value != null) {
            String text = dateFormat().format(new Date(filetime.toMillis()));
            setText(text);
            setToolTipText(text);
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
