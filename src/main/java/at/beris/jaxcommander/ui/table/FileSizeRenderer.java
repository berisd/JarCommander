package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.filesystem.file.VirtualFile;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

import static at.beris.jaxcommander.helper.Localization.numberFormat;

public class FileSizeRenderer extends JLabel implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Long size = (Long) value;
        VirtualFile file = (VirtualFile) table.getValueAt(row, 0);

        setText(file.isDirectory() ? "<DIR>" : numberFormat().format((double) size / 1024) + "K");

        if (isSelected) {
            setForeground(Color.RED);
        } else {
            setForeground(table.getForeground());
        }

        return this;
    }
}
