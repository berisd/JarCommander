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

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;

import static at.beris.jarcommander.ApplicationContext.SELECTION_FOREGROUND_COLOR;

public class FileAttributesRenderer extends JLabel implements TableCellRenderer {
    private Map<Attribute, AttributeText> attributeTextMap;
    private List<Attribute> windowsAttributeList;
    private List<Attribute> posixAttributeList;

    public FileAttributesRenderer() {
        attributeTextMap = createAttributeTextMap();

        windowsAttributeList = Arrays.asList(Attribute.READ_ONLY, Attribute.WRITE, Attribute.EXECUTE);
        posixAttributeList = Arrays.asList(Attribute.OWNER_READ, Attribute.OWNER_WRITE,
                Attribute.OWNER_EXECUTE, Attribute.GROUP_READ, Attribute.GROUP_WRITE, Attribute.GROUP_EXECUTE,
                Attribute.OTHERS_READ, Attribute.OTHERS_WRITE, Attribute.OTHERS_EXECUTE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        StringBuilder shortText = new StringBuilder("");
        StringBuilder longText = new StringBuilder("");
        Set<Attribute> fileAttributes = (Set<Attribute>) value;

        boolean isWindows = fileAttributes.contains(Attribute.READ_ONLY) || fileAttributes.contains(Attribute.WRITE) || fileAttributes.contains(Attribute.EXECUTE);
        List<Attribute> attributeList = isWindows ? windowsAttributeList : posixAttributeList;

        for (Attribute attribute : attributeList) {
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

    private Map<Attribute, AttributeText> createAttributeTextMap() {
        Map<Attribute, AttributeText> attributeTextMap = new HashMap<>();
        attributeTextMap.put(Attribute.READ_ONLY, new AttributeText('R', "Read"));
        attributeTextMap.put(Attribute.WRITE, new AttributeText('W', "Write"));
        attributeTextMap.put(Attribute.EXECUTE, new AttributeText('X', "Execute"));
        attributeTextMap.put(Attribute.HIDDEN, new AttributeText('H', "Hidden"));
        attributeTextMap.put(Attribute.ARCHIVE, new AttributeText('A', "Archive"));
        attributeTextMap.put(Attribute.OWNER_READ, new AttributeText('R', "Owner Read"));
        attributeTextMap.put(Attribute.OWNER_WRITE, new AttributeText('W', "Owner Write"));
        attributeTextMap.put(Attribute.OWNER_EXECUTE, new AttributeText('X', "Owner Execute"));
        attributeTextMap.put(Attribute.GROUP_READ, new AttributeText('R', "Group Read"));
        attributeTextMap.put(Attribute.GROUP_WRITE, new AttributeText('W', "Group Write"));
        attributeTextMap.put(Attribute.GROUP_EXECUTE, new AttributeText('X', "Group Execute"));
        attributeTextMap.put(Attribute.OTHERS_READ, new AttributeText('R', "Others Read"));
        attributeTextMap.put(Attribute.OTHERS_WRITE, new AttributeText('W', "Others Write"));
        attributeTextMap.put(Attribute.OTHERS_EXECUTE, new AttributeText('X', "Others Execute"));

        return attributeTextMap;
    }
}
