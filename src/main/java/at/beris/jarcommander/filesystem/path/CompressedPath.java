/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.path;

import at.beris.jarcommander.filesystem.file.Archivable;
import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.file.JFileFactory;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.lang3.NotImplementedException;

import java.io.File;
import java.util.Date;
import java.util.List;

public class CompressedPath implements JPath<ArchiveEntry> {
    private JFile file;

    public CompressedPath(JFile file) {
        this.file = file;
    }

    @Override
    public ArchiveEntry getBaseObject() {
        return (ArchiveEntry) file.getBaseObject();
    }

    @Override
    public List<JFile> getEntries() {
        return file.list();
    }

    @Override
    public JPath normalize() {
        return this;
    }

    @Override
    public JPath getRoot() {
        if (file instanceof Archivable) {
            return new CompressedPath(JFileFactory.newInstance(new ArchiveEntry() {
                @Override
                public String getName() {
                    return "";
                }

                @Override
                public long getSize() {
                    return 0;
                }

                @Override
                public boolean isDirectory() {
                    return false;
                }

                @Override
                public Date getLastModifiedDate() {
                    return null;
                }
            }, ((Archivable) file).getArchive()));
        } else {
            return new LocalPath(new File(File.separator).toPath());
        }
    }

    @Override
    public JPath getParent() {
        JFile parentFile = file.getParentFile();

        if (parentFile == null) {
            JFile archivefile = ((Archivable) file).getArchive();
            return archivefile.toPath();
        }

        return new CompressedPath(file.getParentFile());
    }

    @Override
    public JFile toFile() {
        throw new NotImplementedException("");
    }

    @Override
    public String toString() {
        if (file instanceof Archivable) {
            Archivable archive = ((Archivable) file);
            JFile archiveFile = archive.getArchive();

            return archiveFile.getAbsolutePath() + File.separator + file.getName();
        } else {
            return file.toString();
        }
    }

    @Override
    public int compareTo(JPath jPath) {
        return 0;
    }
}
