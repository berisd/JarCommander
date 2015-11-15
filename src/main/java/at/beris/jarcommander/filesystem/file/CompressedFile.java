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
import at.beris.jarcommander.filesystem.path.JPath;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompressedFile implements JFile<ArchiveEntry>, Archivable {
    private ArchiveEntry archiveEntry;
    private JFile parentFile;
    private Set<Attribute> windowsAttributes;
    private Set<JFile> children;
    private JFile archiveFile;

    public CompressedFile(ArchiveEntry archiveEntry, JFile archiveFile) {
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
    public void setParentFile(JFile parentFile) {
        this.parentFile = parentFile;
    }

    @Override
    public JFile getParentFile() {
        return parentFile;
    }

    @Override
    public JPath toPath() {
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
    public List<JFile> list() {
        List<JFile> files = new ArrayList<>();
        JFile backFile = JFileFactory.newInstance(createEmptyArchiveEntry(), this.archiveFile);
        backFile.setParentFile(this.parentFile);

        files.add(backFile);
        for (JFile childFile : children) {
            files.add(childFile);
        }

        return files;
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

    @Override
    public String getAbsolutePath() {
        throw new NotImplementedException("");
    }

    @Override
    public ArchiveEntry getBaseObject() {
        return archiveEntry;
    }

    @Override
    public void addFile(Set<JFile> files) {
        children.addAll(files);
    }

    @Override
    public JFile getArchive() {
        return archiveFile;
    }

    @Override
    public void delete() {
        throw new NotImplementedException("");
    }

    @Override
    public List<JFile> listFiles() {
        throw new NotImplementedException("");
    }

    public ArchiveEntry getArchiveEntry() {
        return archiveEntry;
    }

    public JFile getArchiveFile() {
        return archiveFile;
    }


}
