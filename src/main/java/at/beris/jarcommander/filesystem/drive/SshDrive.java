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
import org.apache.commons.lang3.NotImplementedException;

public class SshDrive implements IDrive {
    private IPath path;

    @Override
    public IPath getPath() {
        return path;
    }

    @Override
    public void setPath(IPath path) {
        this.path = path;
    }

    @Override
    public long getSpaceTotal() {
        return 0;
    }

    @Override
    public void setSpaceTotal(long spaceTotal) {
        throw new NotImplementedException("");
    }

    @Override
    public long getSpaceLeft() {
        return 0;
    }

    @Override
    public void setSpaceLeft(long spaceLeft) {
        throw new NotImplementedException("");
    }

    @Override
    public IPath getPath(String path) {
        throw new NotImplementedException("");
    }
}
