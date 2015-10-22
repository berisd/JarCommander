/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.file;

import at.beris.jaxcommander.filesystem.path.JPath;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Date;

public class ArchiveEntryProvider implements FileProvider<ArchiveEntry> {
    private ArchiveEntry archiveEntry;

    public ArchiveEntryProvider(ArchiveEntry archiveEntry) {
        this.archiveEntry = archiveEntry;
    }

    @Override
    public String getName() {
        return archiveEntry.getName();
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
    public JPath toPath() {
        throw new NotImplementedException("");
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
    public String[] list() {
        throw new NotImplementedException("");
    }

    @Override
    public String getAbsolutePath() {
        throw new NotImplementedException("");
    }

    @Override
    public ArchiveEntry[] listFiles() {
        throw new NotImplementedException("");
    }

    @Override
    public ArchiveEntry getBaseObject() {
        return archiveEntry;
    }
}
