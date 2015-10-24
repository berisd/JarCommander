/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.helper;

import at.beris.jarcommander.action.ActionType;
import at.beris.jarcommander.action.CopyAction;
import at.beris.jarcommander.action.CustomAction;
import at.beris.jarcommander.action.DeleteAction;
import at.beris.jarcommander.action.MakeDirAction;
import at.beris.jarcommander.action.MoveAction;
import at.beris.jarcommander.action.QuitAction;
import at.beris.jarcommander.action.RefreshAction;
import at.beris.jarcommander.action.RenameAction;
import at.beris.jarcommander.action.ShowAboutDialogAction;
import at.beris.jarcommander.action.ShowSiteDialogAction;

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
        actionMap.put(ActionType.REFRESH, new RefreshAction());
        actionMap.put(ActionType.SHOW_SITE_DIALOG, new ShowSiteDialogAction());
    }
}
