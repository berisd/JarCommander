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
import at.beris.jarcommander.filesystem.path.LocalPath;

import java.nio.file.Paths;

public class LocalDrive implements IDrive {
    private IPath path;
    private long spaceTotal;
    private long spaceLeft;

    public IPath getPath() {
        return path;
    }

    public void setPath(IPath path) {
        this.path = path;
    }

    public long getSpaceTotal() {
        return spaceTotal;
    }

    public void setSpaceTotal(long spaceTotal) {
        this.spaceTotal = spaceTotal;
    }

    public long getSpaceLeft() {
        return spaceLeft;
    }

    public void setSpaceLeft(long spaceLeft) {
        this.spaceLeft = spaceLeft;
    }

    public IPath getPath(String path) {
        return new LocalPath(Paths.get(path));
    }
}
