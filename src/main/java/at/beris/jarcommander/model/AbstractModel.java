/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractModel {
    protected PropertyChangeSupport propertyChangeSupport;
    protected Set<PropertyChangeListener> propertyChangeListenerSet;

    public AbstractModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        propertyChangeListenerSet = new HashSet<>();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
        propertyChangeListenerSet.add(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
        propertyChangeListenerSet.remove(listener);
    }

    public void removePropertyChangeListeners() {
        for (PropertyChangeListener listener : propertyChangeListenerSet) {
            propertyChangeSupport.removePropertyChangeListener(listener);
        }

        Iterator<PropertyChangeListener> iterator = propertyChangeListenerSet.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
