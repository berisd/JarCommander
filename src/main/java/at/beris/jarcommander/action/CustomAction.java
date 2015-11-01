/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import at.beris.jarcommander.ApplicationContext;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

public abstract class CustomAction extends AbstractAction {

    protected String keyStrokeString;
    protected ApplicationContext context;

    public CustomAction() {
    }

    public CustomAction(ApplicationContext context) {
        super();
        this.context = context;
        putValue(Action.ACTION_COMMAND_KEY, this.getClass().getSimpleName());
    }

    public KeyStroke getKeyStroke() {
        return (KeyStroke) getValue(Action.ACCELERATOR_KEY);
    }

    public String getKeyStrokeString() {
        return keyStrokeString;
    }

    public String getName() {
        return (String) getValue(Action.NAME);
    }
}
