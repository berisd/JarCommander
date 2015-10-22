/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.path;

import at.beris.jaxcommander.filesystem.file.JFile;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.lang3.NotImplementedException;

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
        throw new NotImplementedException("");
    }

    @Override
    public JPath getParent() {
        throw new NotImplementedException("");
    }

    @Override
    public JFile toFile() {
        throw new NotImplementedException("");
    }

    @Override
    public String toString() {
        return file.getName();
    }

    @Override
    public int compareTo(JPath jPath) {
        return 0;
    }
}
