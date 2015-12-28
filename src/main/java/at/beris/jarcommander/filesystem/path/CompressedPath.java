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
import at.beris.jarcommander.filesystem.file.CompressedFile;
import at.beris.jarcommander.filesystem.file.FileFactory;
import at.beris.jarcommander.filesystem.file.IFile;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

public class CompressedPath implements IPath<ArchiveEntry> {
    private IFile file;
    private FileFactory fileFactory;

    public CompressedPath(IFile file, FileFactory fileFactory) {
        this.file = file;
        this.fileFactory = fileFactory;
    }

    @Override
    public ArchiveEntry getBaseObject() {
        return (ArchiveEntry) file.getBaseObject();
    }

    @Override
    public List<IFile> getEntries() {
        return file.list();
    }

    @Override
    public IPath normalize() {
        final ArchiveEntry archiveEntry = ((CompressedFile) file).getArchiveEntry();

        if (archiveEntry.getName().contains("..")) {
            CompressedFile backFile = new CompressedFile(new ArchiveEntry() {
                @Override
                public String getName() {
                    String[] pathParts = archiveEntry.getName().split(File.separator);
                    return StringUtils.join(pathParts, File.separator, 0, pathParts.length - 2);
                }

                @Override
                public long getSize() {
                    return 0;
                }

                @Override
                public boolean isDirectory() {
                    return true;
                }

                @Override
                public Date getLastModifiedDate() {
                    return new Date();
                }
            }, ((CompressedFile) file).getArchiveFile(), fileFactory);
            return new CompressedPath(backFile, fileFactory);
        } else
            return this;
    }

    @Override
    public IPath getRoot() {
        if (file instanceof Archivable) {
            return new CompressedPath(fileFactory.newInstance(new ArchiveEntry() {
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
            }, ((Archivable) file).getArchive()), fileFactory);
        } else {
            return new LocalPath(new File(File.separator).toPath(), fileFactory);
        }
    }

    @Override
    public IPath getParent() {
        IFile parentFile = file.getParentFile();

        if (parentFile == null) {
            IFile archivefile = ((Archivable) file).getArchive();
            return archivefile.toPath();
        }

        return new CompressedPath(file.getParentFile(), fileFactory);
    }

    @Override
    public IFile toFile() {
        throw new NotImplementedException("");
    }

    @Override
    public String toString() {
        if (file instanceof Archivable) {
            Archivable archive = ((Archivable) file);
            IFile archiveFile = archive.getArchive();

            return archiveFile.getAbsolutePath() + File.separator + file.getName();
        } else {
            return file.toString();
        }
    }

    @Override
    public int compareTo(IPath iPath) {
        return 0;
    }
}
