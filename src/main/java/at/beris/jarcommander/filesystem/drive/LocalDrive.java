/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.drive;

import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.filesystem.path.LocalPath;

import java.nio.file.Paths;

public class LocalDrive implements JDrive {
    private JPath path;
    private long spaceTotal;
    private long spaceLeft;

    public JPath getPath() {
        return path;
    }

    public void setPath(JPath path) {
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

    public JPath getPath(String path) {
        return new LocalPath(Paths.get(path));
    }
}
