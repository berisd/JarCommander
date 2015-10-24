/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.helper;

import at.beris.jarcommander.model.AbstractModel;
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
        mvc.registerObjectWithModelProperty(view.getIntegerField(), "integerField", false);
        mvc.registerObjectWithModelProperty(view.getEnumField(), "enumField", false);
    }

    @Test
    public void stringPropertyModelUpdatesView() {
        String expectedValue = "stringfieldtestvalue";
        model.setStringField(expectedValue);
        assertEquals(expectedValue, view.getStringField().getText());
    }

    @Test
    public void integerPropertyModelUpdatesView() {
        Integer expectedValue = 12345;
        model.setIntegerField(12345);
        assertEquals(String.valueOf(expectedValue), view.getIntegerField().getText());
    }

    @Test
    public void stringPropertyViewUpdatesModel() {
        String expectedValue = "stringfieldtestvalue";
        view.getStringField().setText(expectedValue);
        assertEquals(expectedValue, model.getStringField());
    }

    @Test
    public void integerPropertyViewUpdatesModel() {
        Integer expectedValue = 12345;
        view.getIntegerField().setText(expectedValue.toString());
        assertEquals(expectedValue, model.getIntegerField());
    }

    @Test
    public void enumPropertyViewUpdatesModel() {
        TestEnum expectedValue = TestEnum.TEST_ENUM_1;
        view.getEnumField().setText(expectedValue.toString());
        assertEquals(expectedValue, model.getEnumField());
    }

    @Test
    public void enumPropertyModelUpdatesView() {
        TestEnum expectedValue = TestEnum.TEST_ENUM_1;
        model.setEnumField(expectedValue);
        assertEquals(expectedValue.toString(), view.getEnumField().getText());
    }

    private enum TestEnum {
        TEST_ENUM_1
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
        private Integer integerField;
        private TestEnum enumField;

        public Integer getIntegerField() {
            return integerField;
        }

        public void setIntegerField(Integer newValue) {
            Integer oldValue = this.integerField;
            this.integerField = newValue;
            firePropertyChange("setIntegerField", oldValue, newValue);
        }

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String newValue) {
            String oldValue = this.stringField;
            this.stringField = newValue;
            firePropertyChange("setStringField", oldValue, newValue);
        }

        public TestEnum getEnumField() {
            return enumField;
        }

        public void setEnumField(TestEnum newValue) {
            TestEnum oldValue = this.enumField;
            this.enumField = newValue;
            firePropertyChange("setEnumField", oldValue, newValue);
        }
    }

    private class TestDialog extends JDialog {
        private JTextField stringField;
        private JTextField integerField;
        private JTextField enumField;

        public TestDialog() {
            stringField = new JTextField();
            integerField = new JTextField();
            enumField = new JTextField();
        }

        public JTextField getStringField() {
            return stringField;
        }

        public JTextField getIntegerField() {
            return integerField;
        }

        public JTextField getEnumField() {
            return enumField;
        }
    }
}