/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.button;

import at.beris.jarcommander.ApplicationContext;
import at.beris.jarcommander.action.CopyAction;
import org.junit.Test;

import javax.swing.JButton;

import static org.junit.Assert.assertEquals;

public class ButtonFactoryTest {

    @Test
    public void createButton() {
        ApplicationContext context = new ApplicationContext();
        ButtonFactory buttonFactory = context.getButtonFactory();

        JButton button = buttonFactory.createButton(CopyAction.class);
        assertEquals("F5 Copy", button.getText());
    }
}