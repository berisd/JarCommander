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

import java.util.HashMap;
import java.util.Map;

public class ActionFactory {
    private static Map<ActionType, CustomAction> actionMap;

    private ApplicationContext context;

    public ActionFactory(ApplicationContext context) {
        this.context = context;
    }

    public Map<ActionType, CustomAction> getActionMap() {
        if (actionMap == null) {
            createActionMap();
        }
        return actionMap;
    }

    public CustomAction getAction(ActionType key) {
        if (actionMap == null) {
            getActionMap();
        }
        return actionMap.get(key);
    }

    private void createActionMap() {
        actionMap = new HashMap<>();
        actionMap.put(ActionType.COPY, new CopyAction(context));
        actionMap.put(ActionType.MOVE, new MoveAction(context));
        actionMap.put(ActionType.MAKE_DIR, new MakeDirAction(context));
        actionMap.put(ActionType.DELETE, new DeleteAction(context));
        actionMap.put(ActionType.RENAME, new RenameAction(context));
        actionMap.put(ActionType.QUIT, new QuitAction(context));
        actionMap.put(ActionType.SHOW_ABOUT_DIALOG, new ShowAboutDialogAction(context));
        actionMap.put(ActionType.REFRESH, new RefreshAction(context));
        actionMap.put(ActionType.SHOW_SITE_DIALOG, new ShowSiteDialogAction(context));
    }
}
