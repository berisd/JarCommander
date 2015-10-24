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
import org.junit.Before;
import org.junit.Test;

import javax.swing.JDialog;
import javax.swing.JTextField;

import static org.junit.Assert.assertEquals;

public class ModelViewControllerTest {

    private ModelViewController mvc;
    private TestDialog view;
    private TestModel model;

    @Before
    public void setUp() {
        view = createView();
        model = createModel();
        mvc = new ModelViewController(model, view);
        mvc.registerObjectWithModelProperty(view.getStringField(), "stringField", false);
    }

    @Test
    public void modelUpdateUpdatesView() {
        String expectedValue = "stringfieldtestvalue";
        model.setStringField(expectedValue);
        assertEquals(expectedValue, view.getStringField().getText());
    }

    @Test
    public void viewUpdateUpdatesModel() {
        String expectedValue = "stringfieldtestvalue";
        view.getStringField().setText(expectedValue);
        assertEquals(expectedValue, model.getStringField());
    }

    private TestDialog createView() {
        TestDialog testDialog = new TestDialog();
        return testDialog;
    }

    private TestModel createModel() {
        TestModel model = new TestModel();
        return model;
    }

    private class TestModel extends AbstractModel {
        private String stringField;

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String newValue) {
            String oldValue = this.stringField;
            this.stringField = newValue;
            firePropertyChange("setStringField", oldValue, stringField);
        }
    }

    private class TestDialog extends JDialog {
        private JTextField stringField;

        public TestDialog() {
            stringField = new JTextField();
        }

        public JTextField getStringField() {
            return stringField;
        }
    }
}