/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem;

import at.beris.jaxcommander.filesystem.path.JPath;

import java.io.IOException;
import java.util.List;

public interface JFileSystem {
    List<LocalDrive> getDriveList();
    void delete(JPath jPath) throws IOException;
}
