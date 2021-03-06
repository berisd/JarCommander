/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.drive;


import at.beris.virtualfile.VirtualFile;
import org.apache.commons.lang3.NotImplementedException;

public class RemoteDrive implements Drive {
    private VirtualFile path;

    @Override
    public VirtualFile getFile() {
        return path;
    }

    @Override
    public void setFile(VirtualFile file) {
        this.path = file;
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
}
