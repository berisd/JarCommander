/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.compress.archivers.ArchiveEntry;

import java.io.File;

public class JFileFactory {
    public static JFile newInstance(File file) {
        if (file == null)
            return null;

        JFile<File> jFile = new LocalFile(file);

        File parent = file.getParentFile();
        JFile virtualChild = jFile;
        while (parent != null) {
            JFile virtualParent = newInstance(parent);
            virtualChild.setParentFile(virtualParent);
            virtualChild = virtualParent;
            parent = parent.getParentFile();
        }
        return jFile;
    }

    public static JFile newInstance(ArchiveEntry archiveEntry, File archiveFile) {
        if (archiveEntry == null)
            return null;
        return newInstance(archiveEntry, JFileFactory.newInstance(archiveFile));
    }

    public static JFile newInstance(ArchiveEntry archiveEntry, JFile archiveFile) {
        if (archiveEntry == null)
            return null;
        return new CompressedFile(archiveEntry, archiveFile);
    }

    public static JFile newSshFileInstance(ChannelSftp.LsEntry file) {
        if (file == null)
            return null;
        return new SshFile(file);
    }
}
