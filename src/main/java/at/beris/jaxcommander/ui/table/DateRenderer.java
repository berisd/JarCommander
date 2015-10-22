/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.table;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.util.Date;

import static at.beris.jaxcommander.helper.Localization.dateFormat;

public class DateRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Date date = (Date) value;
        String text = dateFormat().format(date);
        setText(text);
        setToolTipText(text);

        if (isSelected) {
            setForeground(Color.RED);
        } else {
            setForeground(table.getForeground());
        }

        return this;
    }
}
