/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;

public class ActionEventFactory {

    public ActionEvent createActionEvent(AWTEvent sourceEvent, Class actionEventClass) {
        return new ActionEvent(sourceEvent.getSource(), sourceEvent.getID(), actionEventClass.getSimpleName());
    }

    public <T> ActionEvent createActionEvent(AWTEvent sourceEvent, Class actionEventClass, T param) {
        return new ParamActionEvent<>(sourceEvent.getSource(), sourceEvent.getID(), actionEventClass.getSimpleName(), param);
    }

}
