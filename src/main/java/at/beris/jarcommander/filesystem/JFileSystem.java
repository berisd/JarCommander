/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.filesystem.drive.JDrive;
import at.beris.jarcommander.filesystem.path.JPath;

import java.io.IOException;
import java.util.List;

public interface JFileSystem {
    void open();

    void close();

    List<JDrive> getDriveList();

    void delete(JPath jPath) throws IOException;
}
