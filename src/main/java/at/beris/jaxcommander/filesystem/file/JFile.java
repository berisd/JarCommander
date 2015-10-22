/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.file;

import at.beris.jaxcommander.filesystem.path.JPath;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface JFile<T> {
    T getBaseObject();

    String getName();

    Date getLastModified();

    long getSize();

    boolean isDirectory();

    void setParentFile(JFile parentFile);

    JFile getParentFile();

    boolean exists();

    boolean mkdirs();

    Set<Attribute> attributes();

    List<JFile> list();

    String getAbsolutePath();

    JPath toPath();
}
