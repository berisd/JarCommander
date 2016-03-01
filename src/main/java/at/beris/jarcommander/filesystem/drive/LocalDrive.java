/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.drive;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileManager;

public class LocalDrive implements Drive {
    private File path;
    private long spaceTotal;
    private long spaceLeft;

    public File getFile() {
        return path;
    }

    public void setFile(File file) {
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

    public File getPath(String path) {
        return FileManager.newLocalFile(path);
    }
}
