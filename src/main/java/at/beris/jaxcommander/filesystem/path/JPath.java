/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.path;

import at.beris.jaxcommander.filesystem.file.JFile;

import java.util.List;

public interface JPath<T> {
    T getBaseObject();
    List<JFile> getEntries();
    JPath normalize();
    JPath getRoot();
    JPath getParent();
    JFile toFile();
    int compareTo(JPath jPath);
    String toString();
}