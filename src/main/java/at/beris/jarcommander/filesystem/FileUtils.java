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
import at.beris.jarcommander.filesystem.path.IPath;
import at.beris.jarcommander.filesystem.path.LocalPath;
import at.beris.jarcommander.filesystem.path.SshPath;

public final class FileUtils {
    public static IBlockCopy createBlockCopyInstance(IFile sourceFile, IPath targetPath) {
        IBlockCopy blockCopy;

        if (sourceFile instanceof LocalFile && targetPath instanceof LocalPath)
            blockCopy = new LocalBlockCopy();
        else if (sourceFile instanceof SshFile && targetPath instanceof LocalPath)
            blockCopy = new SshBlockCopyFrom();
        else if (sourceFile instanceof LocalFile && targetPath instanceof SshPath)
            blockCopy = new SshBlockCopyTo();
        else
            throw new IllegalArgumentException("Can't create BlockCopyInstance for sourceFile " +
                    sourceFile.getAbsolutePath() + " and targetPath " + targetPath.getClass());

        return blockCopy;
    }
}
