/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import java.io.IOException;

public interface IFileOperationProvider<FILE> {
    boolean exists(FILE file);

    boolean mkdirs(FILE file);

    void delete(FILE file);

    void copy(FILE sourceFile, IFile targetFile, CopyListener listener) throws IOException;
}
