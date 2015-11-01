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
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static at.beris.jarcommander.Application.logException;

public class ActionFactory {
    private static Map<Class<? extends CustomAction>, CustomAction> actionMap;

    private ApplicationContext context;

    public ActionFactory() {
    }

    public ActionFactory(ApplicationContext context) {
        this.context = context;
    }

    public Map<Class<? extends CustomAction>, CustomAction> getActionMap() {
        if (actionMap == null) {
            createActionMap();
        }
        return actionMap;
    }

    public void setActionMap(Map<Class<? extends CustomAction>, CustomAction> actionMap) {
        this.actionMap = actionMap;
    }

    public CustomAction getAction(Class<? extends CustomAction> key) {
        if (actionMap == null) {
            getActionMap();
        }
        return actionMap.get(key);
    }

    private void createActionMap() {
        Reflections reflections = context.getReflections(CustomAction.class);
        actionMap = new HashMap<>();
        for (Class<? extends CustomAction> actionClass : reflections.getSubTypesOf(CustomAction.class)) {
            try {
                Constructor<? extends CustomAction> constructor = actionClass.getConstructor(ApplicationContext.class);
                actionMap.put(actionClass, constructor.newInstance(context));
            } catch (ReflectiveOperationException e) {
                logException(e);
            }
        }
    }
}
