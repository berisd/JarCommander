/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.path.CompressedPath;
import at.beris.jarcommander.filesystem.path.IPath;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompressedFile implements IFile<ArchiveEntry>, Archivable {
    private ArchiveEntry archiveEntry;
    private IFile parentFile;
    private Set<Attribute> windowsAttributes;
    private Set<IFile> children;
    private IFile archiveFile;

    public CompressedFile(ArchiveEntry archiveEntry, IFile archiveFile) {
        this(archiveEntry);
        this.archiveFile = archiveFile;
    }

    public CompressedFile(ArchiveEntry archiveEntry) {
        this.archiveEntry = archiveEntry;
        windowsAttributes = new HashSet<>();
        children = new HashSet<>();
    }

    @Override
    public String getName() {
        String[] nameParts = archiveEntry.getName().split(File.separator);
        return nameParts[nameParts.length - 1];
    }

    @Override
    public Date getLastModified() {
        return archiveEntry.getLastModifiedDate();
    }

    @Override
    public long getSize() {
        return archiveEntry.getSize();
    }

    @Override
    public boolean isDirectory() {
        return archiveEntry.isDirectory();
    }

    @Override
    public void setParentFile(IFile parentFile) {
        this.parentFile = parentFile;
    }

    @Override
    public IFile getParentFile() {
        return parentFile;
    }

    @Override
    public IPath toPath() {
        return new CompressedPath(this);
    }

    @Override
    public boolean exists() {
        throw new NotImplementedException("");
    }

    @Override
    public boolean mkdirs() {
        throw new NotImplementedException("");
    }

    @Override
    public Set<Attribute> attributes() {
        return windowsAttributes;
    }

    @Override
    public List<IFile> list() {
        List<IFile> files = new ArrayList<>();
        IFile backFile = FileFactory.newInstance(createEmptyArchiveEntry(), this.archiveFile);
        backFile.setParentFile(this.parentFile);

        files.add(backFile);
        for (IFile childFile : children) {
            files.add(childFile);
        }

        return files;
    }

    @Override
    public String getAbsolutePath() {
        throw new NotImplementedException("");
    }

    @Override
    public ArchiveEntry getBaseObject() {
        return archiveEntry;
    }

    @Override
    public void addFile(Set<IFile> files) {
        children.addAll(files);
    }

    @Override
    public IFile getArchive() {
        return archiveFile;
    }

    @Override
    public void delete() {
        throw new NotImplementedException("");
    }

    @Override
    public List<IFile> listFiles() {
        throw new NotImplementedException("");
    }

    @Override
    public byte[] checksum() {
        throw new NotImplementedException("");
    }

    private ArchiveEntry createEmptyArchiveEntry() {
        return new ArchiveEntry() {
            @Override
            public String getName() {
                String[] pathParts = archiveEntry.getName().split(File.separator);
                String parentPath = StringUtils.join(pathParts, File.separator, 0, pathParts.length - 2);
                return parentPath + File.separator + "..";
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
        };
    }

    public ArchiveEntry getArchiveEntry() {
        return archiveEntry;
    }

    public IFile getArchiveFile() {
        return archiveFile;
    }


}
