/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.ui.button;

import at.beris.jarcommander.action.ActionType;
import org.junit.Test;

import javax.swing.JButton;

import static org.junit.Assert.assertEquals;

public class ButtonFactoryTest {

    @Test
    public void createButton() {
        JButton button = ButtonFactory.createButton(ActionType.COPY);
        assertEquals("F5 Copy", button.getText());
    }
}