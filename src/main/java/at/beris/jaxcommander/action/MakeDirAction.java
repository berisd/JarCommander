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

public class MakeDirAction extends CustomAction {
    private final static Logger LOGGER = Logger.getLogger(MakeDirAction.class);
    public static final ActionType KEY = ActionType.MAKE_DIR;

    public MakeDirAction() {
        super();

        keyStrokeString = "F7";
        putValue(Action.NAME, "MkDir");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
                keyStrokeString));
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
        putValue(Action.ACTION_COMMAND_KEY, "mkdir");
        putValue(Action.SHORT_DESCRIPTION, "Make Directory");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.info("MkDir");
    }

    @Override
    public ActionType getKey() {
        return KEY;
    }
}
