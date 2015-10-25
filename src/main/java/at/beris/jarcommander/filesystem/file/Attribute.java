/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

public enum Attribute {
    READ("R", "Read"),
    WRITE("W", "Write"),
    EXECUTE("X", "Execute"),
    HIDDEN("H", "Hidden");

    private String shortName;
    private String longName;

    Attribute(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    public String shortName() {
        return this.shortName;
    }

    public String longName() {
        return this.longName;
    }
}
