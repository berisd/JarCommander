/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import java.awt.event.ActionEvent;

public class ParamActionEvent<T> extends ActionEvent {
    private T param;

    public ParamActionEvent(Object source, int id, String command, T param) {
        super(source, id, command);
        this.param = param;
    }

    public T getParam() {
        return param;
    }
}
