/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.file;

import at.beris.jaxcommander.filesystem.path.CompressedPath;
import at.beris.jaxcommander.filesystem.path.JPath;
import org.apache.commons.compress.archivers.ArchiveEntry;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompressedFile implements JFile<ArchiveEntry> {
    private ArchiveEntry archiveEntry;
    private ArchiveEntry parentFile;
    private Set<Attribute> attributes;
    private Set<ArchiveEntry> children;

    public CompressedFile(ArchiveEntry archiveEntry) {
        this.archiveEntry = archiveEntry;
        attributes = new HashSet<>();
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
        this.parentFile = (ArchiveEntry) parentFile.getBaseObject();
    }

    @Override
    public JFile getParentFile() {
        return JFileFactory.newInstance(parentFile);
    }

    @Override
    public JPath toPath() {
        return new CompressedPath(this);
    }

    @Override
    public boolean exists() {
        throw new NotImplementedException();
    }

    @Override
    public boolean mkdirs() {
        throw new NotImplementedException();
    }

    @Override
    public Set<Attribute> attributes() {
        return attributes;
    }

    @Override
    public List<JFile> list() {
        List<JFile> files = new ArrayList<>();
        for (ArchiveEntry archiveEntry : children) {
            files.add(JFileFactory.newInstance(archiveEntry));
        }

        return files;
    }

    @Override
    public String getAbsolutePath() {
        throw new NotImplementedException();
    }

    @Override
    public ArchiveEntry getBaseObject() {
        return archiveEntry;
    }

    @Override
    public void addFile(Set<JFile> files) {
        for (JFile file : files) {
            this.children.add((ArchiveEntry) file.getBaseObject());
        }
    }
}
