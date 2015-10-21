/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.file;

import org.apache.commons.compress.archivers.ArchiveEntry;

import java.io.File;

public class VirtualFileFactory {
    public static VirtualFile newInstance(File file) {
        VirtualFile<File>  virtualFile = new VirtualFile<>(new LocalFileProvider(file));

        File parent = file.getParentFile();
        VirtualFile virtualChild = virtualFile;
        while (parent != null) {
            VirtualFile virtualParent = newInstance(parent);
            virtualChild.setParentFile(virtualParent);
            virtualChild = virtualParent;
            parent = parent.getParentFile();
        }
        return virtualFile;
    }
    public static VirtualFile newInstance(ArchiveEntry archiveEntry) {
        return new VirtualFile<>(new ArchiveEntryProvider(archiveEntry));
    }
}
