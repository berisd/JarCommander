/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;

public class JaxTable extends JPanel {
    JTable table;

    public JaxTable() {
        GridLayout gridLayout = new GridLayout(1, 1);

        setLayout(gridLayout);

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Table Title",
                TitledBorder.CENTER,
                TitledBorder.TOP));


        String columnNames[] = {"File", "Date", "Size"};
        Object[][] columndata = {{"autostart.exe", "2012/05/13", "130k"}, {"my image is here.gif", "2012/06/12", "2400k"}};

        table = new JTable(columndata, columnNames);
        add(new JScrollPane(table));
    }
}
