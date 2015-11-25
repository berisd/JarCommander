/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.path.IPath;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IFile<T> {
    T getBaseObject();

    String getName();

    Date getLastModified();

    long getSize();

    boolean isDirectory();

    void setParentFile(IFile parentFile);

    IFile getParentFile();

    void addFile(Set<IFile> files);

    boolean exists();

    boolean mkdirs();

    Set<Attribute> attributes();

    List<IFile> list();

    String getAbsolutePath();

    IPath toPath();

    void delete();

    List<IFile> listFiles();

    byte[] checksum();
}
