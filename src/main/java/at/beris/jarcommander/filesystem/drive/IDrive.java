/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.drive;

import at.beris.jarcommander.filesystem.file.IFile;

public interface IDrive {
    IFile getFile();

    void setFile(IFile file);

    long getSpaceTotal();

    void setSpaceTotal(long spaceTotal);

    long getSpaceLeft();

    void setSpaceLeft(long spaceLeft);
}
