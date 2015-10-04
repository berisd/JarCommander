/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.ui.button;

import at.beris.jaxcommander.action.ActionType;
import at.beris.jaxcommander.action.CustomAction;
import at.beris.jaxcommander.helper.ActionHelper;

import javax.swing.JButton;
import javax.swing.JComponent;

public final class ButtonFactory {
    public static JButton createButton(ActionType actionType) {
        JButton button = new JButton();
        CustomAction action = ActionHelper.getAction(actionType);
        button.setText(action.getKeyStrokeString() + " " + action.getName());
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(action.getKeyStroke(), action.getKey());
        button.getActionMap().put(action.getKey(), action);
        return button;
    }
}
