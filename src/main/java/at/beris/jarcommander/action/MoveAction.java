/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import org.apache.log4j.Logger;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class MoveAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(MoveAction.class);
    public static final ActionType KEY = ActionType.MOVE;

    public MoveAction() {
        super();

        keyStrokeString = "F6";
        putValue(Action.NAME, "Move");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
        putValue(Action.ACTION_COMMAND_KEY, "move");
        putValue(Action.SHORT_DESCRIPTION, "Move");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Move");
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
