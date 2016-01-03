/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.drive;

import at.beris.jarcommander.filesystem.file.FileManager;
import at.beris.jarcommander.filesystem.file.IFile;

public class LocalDrive implements IDrive {
    private IFile path;
    private long spaceTotal;
    private long spaceLeft;

    public IFile getFile() {
        return path;
    }

    public void setFile(IFile file) {
        this.path = file;
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

    public IFile getPath(String path) {
        return FileManager.newLocalFile(path);
    }
}
