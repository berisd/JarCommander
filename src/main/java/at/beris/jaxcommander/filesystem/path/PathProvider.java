/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.path;

import at.beris.jaxcommander.filesystem.file.VirtualFile;

import java.util.List;

public interface PathProvider<T> {
    T getBaseObject();
    List<VirtualFile> getEntries();
    T normalize();
    T getRoot();
    T getParent();
    VirtualFile toFile();
    int compareTo(VirtualPath virtualPath);
    PathProvider<T> newInstance(T object);
    String toString();
}
