/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

import javax.swing.KeyStroke;

public enum ActionType {
    CHANGE_DRIVE(""),
    COPY("F5"),
    DELETE("F8"),
    EXECUTE_FILE("ENTER"),
    KEY_PRESSED(""),
    MAKE_DIR("F7"),
    MOVE("F6"),
    NAVIGATE_PATH_UP("BACK_SPACE"),
    QUIT("alt F4"),
    REFRESH("ctrl R"),
    RENAME("F9"),
    SCROLL_TO_LETTER_IN_FILE_TABLE_PANE(""),
    SCROLL_TO_BOTTOM("END"),
    SCROLL_TO_TOP("HOME"),
    SELECT_NAVIGATION_PANEL(""),
    SHOW_ABOUT_DIALOG("alt A"),
    SHOW_SITE_DIALOG("ctrl S"),
    SWITCH_NAVIGATION_PANEL("TAB");

    private String keyStrokeString;
    private KeyStroke keyStroke;

    ActionType(String keyStrokeString) {
        this.keyStrokeString = keyStrokeString;
        this.keyStroke = KeyStroke.getKeyStroke(keyStrokeString);
    }

    public String getKeyStrokeString() {
        return keyStrokeString;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
}
