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
import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.CustomAction;
import at.beris.jarcommander.action.ActionFactory;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

public class ButtonFactory {
    private ApplicationContext context;

    public ButtonFactory(ApplicationContext context) {
        this.context = context;
    }

    public JButton createButton(ActionType actionType) {
        JButton button = new JButton();
        CustomAction action = context.getActionFactory().getAction(actionType);

        button.setAction(action);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(action.getKeyStroke(), actionType.toString());
        button.getActionMap().put(actionType.toString(), action);
        button.setText(action.getKeyStrokeString() + " " + action.getName());

        return button;
    }

    public JButton createIconButton(ActionType actionType, Icon icon) {
        JButton button = new JButton();
        CustomAction action = context.getActionFactory().getAction(actionType);

        button.setAction(action);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(action.getKeyStroke(), actionType.toString());
        button.getActionMap().put(actionType.toString(), action);
        button.setText("");
        button.setIcon(icon);

        return button;
    }
}
