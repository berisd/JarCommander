/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.file.LocalFile;
import at.beris.jarcommander.filesystem.file.SshFile;

public final class FileUtils {
    public static IBlockCopy createBlockCopyInstance(IFile sourceFile) {
        IBlockCopy blockCopy = null;

        if (sourceFile instanceof LocalFile)
            blockCopy = new LocalBlockCopy();
        else if (sourceFile instanceof SshFile)
            blockCopy = new SshBlockCopy();

        return blockCopy;
    }
}
