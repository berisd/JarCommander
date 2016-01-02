/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.path;

import at.beris.jarcommander.filesystem.file.IFile;

import java.io.IOException;
import java.util.List;

public interface IPath {
    List<IFile> getEntries() throws IOException;

    IPath normalize();

    IPath getRoot();

    IPath getParent();

    String toString();

    IFile toFile();

    int compareTo(IPath iPath);
}
