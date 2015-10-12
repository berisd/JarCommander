/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui;

import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionScrollPane extends JScrollPane implements ActionListener {
    public ActionScrollPane(Component view) {
        super(view);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ((ActionListener) this.getParent()).actionPerformed(e);
    }
}
