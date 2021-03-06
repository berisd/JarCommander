package at.beris.jarcommander.ui.table;

import at.beris.virtualfile.VirtualFile;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;
import static at.beris.jarcommander.helper.Localization.numberFormat;

public class FileSizeRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Long size = (Long) value;
        VirtualFile file = (VirtualFile) table.getValueAt(row, 0);

        String text = file.isDirectory() ? "<DIR>" : numberFormat().format((double) size / 1024) + "K";
        setText(text);
        setToolTipText(text);

        if (isSelected) {
            setForeground(SELECTION_FOREGROUND_COLOR);
        } else {
            setForeground(table.getForeground());
        }

        return this;
    }
}
