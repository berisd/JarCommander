/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.helper;

import at.beris.jaxcommander.action.ActionType;
import at.beris.jaxcommander.action.CopyAction;
import at.beris.jaxcommander.action.CustomAction;
import at.beris.jaxcommander.action.DeleteAction;
import at.beris.jaxcommander.action.MakeDirAction;
import at.beris.jaxcommander.action.MoveAction;
import at.beris.jaxcommander.action.QuitAction;
import at.beris.jaxcommander.action.RenameAction;
import at.beris.jaxcommander.action.ShowAboutDialogAction;

import java.util.HashMap;
import java.util.Map;

public final class ActionHelper {
    private static Map<ActionType, CustomAction> actionMap;

    public static Map<ActionType, CustomAction> getActionMap() {
        if (actionMap == null) {
            createActionMap();
        }
        return actionMap;
    }

    public static CustomAction getAction(ActionType key) {
        if (actionMap == null) {
            getActionMap();
        }
        return actionMap.get(key);
    }

    private static void createActionMap() {
        actionMap = new HashMap<>();
        actionMap.put(ActionType.COPY, new CopyAction());
        actionMap.put(ActionType.MOVE, new MoveAction());
        actionMap.put(ActionType.MAKE_DIR, new MakeDirAction());
        actionMap.put(ActionType.DELETE, new DeleteAction());
        actionMap.put(ActionType.RENAME, new RenameAction());
        actionMap.put(ActionType.QUIT, new QuitAction());
        actionMap.put(ActionType.SHOW_ABOUT_DIALOG, new ShowAboutDialogAction());
    }
}
