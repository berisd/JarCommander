package at.beris.jaxcommander.ui.table;

import at.beris.jaxcommander.filesystem.file.JFile;

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
        JFile file = (JFile) table.getValueAt(row, 0);

        String text = file.isDirectory() ? "<DIR>" : numberFormat().format((double) size / 1024) + "K";

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
