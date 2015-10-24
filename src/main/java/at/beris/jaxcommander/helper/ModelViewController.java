/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.helper;

import at.beris.jaxcommander.model.AbstractModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static at.beris.jaxcommander.Application.logException;

public class ModelViewController {
    private final static Logger LOGGER = Logger.getLogger(ModelViewController.class);

    private AbstractModel model;
    private Container view;
    private Map<Method, List<Component>> modelGetPropertyMethodToViewFieldsMap;
    private Map<Method, Boolean> modelPropertyReadOnlyMap;

    public ModelViewController(AbstractModel model, Container view) {
        this.model = model;
        this.view = view;
        this.modelGetPropertyMethodToViewFieldsMap = new HashMap<>();
        this.modelPropertyReadOnlyMap = new HashMap<>();
        model.addPropertyChangeListener(new ModelPropertyChangeListener());
    }

    public void registerObjectWithModelProperty(Component component, String modelPropertyName, Boolean readOnly) {
        Method modelGetPropertyMethod = getPropertyGetter(model, modelPropertyName);
        List<Component> componentList = modelGetPropertyMethodToViewFieldsMap.get(modelGetPropertyMethod);
        modelPropertyReadOnlyMap.put(modelGetPropertyMethod, readOnly);

        if (componentList == null) {
            componentList = new ArrayList<>();
            modelGetPropertyMethodToViewFieldsMap.put(modelGetPropertyMethod, componentList);
        }

        componentList.add(component);

        if (!readOnly) {
            // Update from Model to View (=read), additionally Update from View to Model (=write)
            if (component instanceof JTextField) {
                ((JTextField) component).getDocument().addDocumentListener(new TextFieldDocumentListener(model, modelPropertyName));
            }
        }
    }

    private class ModelPropertyChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            LOGGER.debug("propertyChange");

            String modelFieldName = evt.getPropertyName().substring("set".length());

            Object newValue = evt.getNewValue();
            Object oldValue = evt.getOldValue();

            if (Objects.equals(oldValue, newValue))
                return;

            List<Component> registeredViewFields = modelGetPropertyMethodToViewFieldsMap.get(getPropertyGetter(model, modelFieldName));

            for (Component object : registeredViewFields) {
                setViewPropertyValue(object, newValue);
            }
        }
    }

    private class TextFieldDocumentListener implements DocumentListener {
        private AbstractModel model;
        private String propertyName;

        public TextFieldDocumentListener(AbstractModel model, String propertyName) {
            this.model = model;
            this.propertyName = propertyName;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            LOGGER.info("insertUpdate");

            String modelPropertyValue = null;
            try {
                modelPropertyValue = (String) getPropertyGetter(model, propertyName).invoke(model);
            } catch (IllegalAccessException ex) {
                logException(ex);
            } catch (InvocationTargetException ex) {
                logException(ex);
            }

            try {
                final String viewPropertyValue = e.getDocument().getText(0, e.getDocument().getLength());

                if (Objects.equals(modelPropertyValue, viewPropertyValue))
                    return;

                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.info("insertUpdate run");
                        setModelPropertyValue(propertyName, viewPropertyValue);
                    }
                });

            } catch (BadLocationException ex) {
                logException(ex);
            } catch (InterruptedException ex) {
                logException(ex);

            } catch (InvocationTargetException ex) {
                logException(ex);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            LOGGER.info("removeUpdate");
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            LOGGER.info("changedUpdate");
        }
    }

    public Method getPropertyGetter(Object object, String propertyName) {
        try {
            return object.getClass().getDeclaredMethod("get" + StringUtils.capitalize(propertyName));
        } catch (NoSuchMethodException e) {
            logException(e);
        }
        return null;
    }

    public Method getPropertySetter(Object object, String propertyName, Class valueType) {
        try {
            return object.getClass().getDeclaredMethod("set" + StringUtils.capitalize(propertyName), valueType);
        } catch (NoSuchMethodException e) {
            logException(e);
        }
        return null;
    }

    public void setViewPropertyValue(Component viewProperty, Object newValue) {
        if (viewProperty instanceof JTextField) {
            JTextField textfield = (JTextField) viewProperty;
            String oldValue = textfield.getText();
            if (!Objects.equals(oldValue, newValue)) {
                ((JTextField) viewProperty).setText(String.valueOf(newValue));
            }
        }
    }

    public void setModelPropertyValue(String propertyName, Object newValue) {
        try {
            Class modelPropertyType = model.getClass().getDeclaredField(propertyName).getType();
            Method modelPropertySetter = getPropertySetter(model, propertyName, modelPropertyType);
            Method modelPropertyGetter = getPropertyGetter(model, propertyName);

            Object oldValue = modelPropertyGetter.invoke(model);

            if (!Objects.equals(oldValue, newValue)) {
                modelPropertySetter.invoke(model, newValue);
            }
        } catch (IllegalAccessException e) {
            logException(e);
        } catch (InvocationTargetException e) {
            logException(e);
        } catch (NoSuchFieldException e) {
            logException(e);
        }
    }
}