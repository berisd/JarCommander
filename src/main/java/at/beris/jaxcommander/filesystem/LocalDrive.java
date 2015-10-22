/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem;

import at.beris.jaxcommander.filesystem.path.LocalPath;
import at.beris.jaxcommander.filesystem.path.JPath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalDrive implements JDrive {
    private JPath path;
    private long spaceTotal;
    private long spaceLeft;

    public JPath getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = new LocalPath(path);
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
