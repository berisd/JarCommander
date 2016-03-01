/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.table;

import at.beris.virtualfile.attribute.BasicFilePermission;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import at.beris.virtualfile.attribute.DosFileAttribute;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;

public class FileAttributesRenderer extends JLabel implements TableCellRenderer {
    private Map<FileAttribute, AttributeText> attributeTextMap;
    private List<FileAttribute> windowsAttributeList;
    private List<FileAttribute> posixAttributeList;

    public FileAttributesRenderer() {
        attributeTextMap = createAttributeTextMap();

        windowsAttributeList = Arrays.asList(BasicFilePermission.READ, BasicFilePermission.WRITE, BasicFilePermission.EXECUTE, DosFileAttribute.ARCHIVE, DosFileAttribute.HIDDEN, DosFileAttribute.READ_ONLY, DosFileAttribute.SYSTEM);
        posixAttributeList = Arrays.asList(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        StringBuilder shortText = new StringBuilder("");
        StringBuilder longText = new StringBuilder("");
        Set<FileAttribute> fileAttributes = (Set<FileAttribute>) value;

        boolean isWindows = fileAttributes.contains(BasicFilePermission.READ) || fileAttributes.contains(BasicFilePermission.WRITE) || fileAttributes.contains(BasicFilePermission.EXECUTE);
        List<FileAttribute> attributeList = isWindows ? windowsAttributeList : posixAttributeList;

        for (FileAttribute attribute : attributeList) {
            if (fileAttributes.contains(attribute)) {
                shortText.append(attributeTextMap.get(attribute).shortText);
                if (longText.length() > 0)
                    longText.append("<br>");
                longText.append(attributeTextMap.get(attribute).longText);
            } else {
                shortText.append('-');
            }
        }

        longText.insert(0, "<html>");
        longText.append("</html>");

        setText(shortText.toString());
        setToolTipText(longText.toString());

        if (isSelected) {
            setForeground(SELECTION_FOREGROUND_COLOR);
        } else {
            setForeground(table.getForeground());
        }

        return this;
    }

    private class AttributeText {
        public AttributeText(char shortText, String longText) {
            this.shortText = shortText;
            this.longText = longText;
        }

        public char shortText;
        public String longText;
    }

    private Map<FileAttribute, AttributeText> createAttributeTextMap() {
        Map<FileAttribute, AttributeText> attributeTextMap = new HashMap<>();
        attributeTextMap.put(BasicFilePermission.READ, new AttributeText('R', "Read"));
        attributeTextMap.put(BasicFilePermission.WRITE, new AttributeText('W', "Write"));
        attributeTextMap.put(BasicFilePermission.EXECUTE, new AttributeText('X', "Execute"));
        attributeTextMap.put(DosFileAttribute.ARCHIVE, new AttributeText('A', "Archive"));
        attributeTextMap.put(DosFileAttribute.HIDDEN, new AttributeText('H', "Hidden"));
        attributeTextMap.put(DosFileAttribute.READ_ONLY, new AttributeText('r', "Read-Only"));
        attributeTextMap.put(DosFileAttribute.SYSTEM, new AttributeText('S', "System"));
        attributeTextMap.put(PosixFilePermission.OWNER_READ, new AttributeText('R', "Owner Read"));
        attributeTextMap.put(PosixFilePermission.OWNER_WRITE, new AttributeText('W', "Owner Write"));
        attributeTextMap.put(PosixFilePermission.OWNER_EXECUTE, new AttributeText('X', "Owner Execute"));
        attributeTextMap.put(PosixFilePermission.GROUP_READ, new AttributeText('R', "Group Read"));
        attributeTextMap.put(PosixFilePermission.GROUP_WRITE, new AttributeText('W', "Group Write"));
        attributeTextMap.put(PosixFilePermission.GROUP_EXECUTE, new AttributeText('X', "Group Execute"));
        attributeTextMap.put(PosixFilePermission.OTHERS_READ, new AttributeText('R', "Others Read"));
        attributeTextMap.put(PosixFilePermission.OTHERS_WRITE, new AttributeText('W', "Others Write"));
        attributeTextMap.put(PosixFilePermission.OTHERS_EXECUTE, new AttributeText('X', "Others Execute"));

        return attributeTextMap;
    }
}
