/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.action;

public interface ActionCommand {
    String CHANGE_DRIVE = "changeDrive";
    String SCROLL_TO_TOP = "scrollToTop";
    String SCROLL_TO_BOTTOM = "scrollToBottom";
    String KEY_PRESSED = "keyPressed";
    String NAVIGATE_PATH_UP = "navigatePathUp";
    String SELECT_NAVIGATION_PANEL = "selectNavigationPanel";
    String EXECUTE_FILE = "executeFile";
}
