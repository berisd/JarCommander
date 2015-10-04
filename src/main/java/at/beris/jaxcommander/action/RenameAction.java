/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.action;

import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class RenameAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(RenameAction.class);
    public static final ActionType KEY = ActionType.MOVE;

    public RenameAction() {
        super();

        keyStrokeString = "F9";
        putValue(Action.NAME, "Rename");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
        putValue(Action.ACTION_COMMAND_KEY, "rename");
        putValue(Action.SHORT_DESCRIPTION, "Rename");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.info("Rename");
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
