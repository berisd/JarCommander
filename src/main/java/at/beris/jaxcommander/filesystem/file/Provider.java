/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.file;

import at.beris.jaxcommander.filesystem.path.VirtualPath;

import java.util.Date;

public interface Provider<T> {
    String getName();

    Date getLastModified();

    long getSize();

    boolean isDirectory();

    VirtualPath toPath();

    T[] listFiles();

    T getBaseObject();
}
