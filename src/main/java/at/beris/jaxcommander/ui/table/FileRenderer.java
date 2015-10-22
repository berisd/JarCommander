/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.filesystem.file.VirtualFile;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class FileRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        VirtualFile file = (VirtualFile) value;
        setText(file.getName());
        setToolTipText(file.getName());

        if (isSelected) {
            setForeground(Color.RED);
        } else {
            setForeground(table.getForeground());
        }

        return this;
    }
}
