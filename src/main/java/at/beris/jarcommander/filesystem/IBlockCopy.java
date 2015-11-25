/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.filesystem.file.JFile;

public interface IBlockCopy {
    int COPY_BUFFER_SIZE = 1024 * 16;

    void init(JFile sourceFile, JFile targetFile);
    void close();
    void copy();
    int read();
    int positionBuffer();
    long size();
    long bytesWrittenTotal();
    long bytesWritten();
}
