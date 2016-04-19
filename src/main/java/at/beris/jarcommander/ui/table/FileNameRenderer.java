/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.virtualfile.File;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.io.IOException;
import static at.beris.jarcommander.Application.logException;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;

public class FileNameRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        File file = (File) value;
        try {
            setText(file.getName());
            setToolTipText(file.getName());
        }
        catch(IOException e) {
            logException(e);
        }

        if (isSelected) {
            setForeground(SELECTION_FOREGROUND_COLOR);
        } else {
            setForeground(table.getForeground());
        }

        return this;
    }
}
