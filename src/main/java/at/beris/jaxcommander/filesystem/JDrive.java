/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem;

import at.beris.jaxcommander.filesystem.path.VirtualPath;

import java.nio.file.Path;

public interface JDrive {
    VirtualPath getPath();
    void setPath(Path path);
    long getSpaceTotal();
    void setSpaceTotal(long spaceTotal);
    long getSpaceLeft();
    void setSpaceLeft(long spaceLeft);
    VirtualPath getPath(String path);
}
