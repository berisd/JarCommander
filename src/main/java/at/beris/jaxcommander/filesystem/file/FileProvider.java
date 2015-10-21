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

public interface FileProvider<T> {
    String getName();

    Date getLastModified();

    long getSize();

    boolean isDirectory();

    VirtualPath toPath();

    boolean exists();

    boolean mkdirs();

    String[] list();

    String getAbsolutePath();

    T[] listFiles();

    T getBaseObject();
}
