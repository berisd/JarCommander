/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.drive;

import at.beris.jarcommander.filesystem.path.IPath;

public interface IDrive {
    IPath getPath();

    void setPath(IPath path);

    long getSpaceTotal();

    void setSpaceTotal(long spaceTotal);

    long getSpaceLeft();

    void setSpaceLeft(long spaceLeft);

    IPath getPath(String path);
}
