/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.file;

import at.beris.jaxcommander.filesystem.path.VirtualPath;

import java.io.File;
import java.util.Date;

public class FileProvider implements Provider<File> {
    private File file;

    public FileProvider(File file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public Date getLastModified() {
        return new java.util.Date(file.lastModified());
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public VirtualPath toPath() {
        return new VirtualPath(file.toPath());
    }

    @Override
    public File[] listFiles() {
        return file.listFiles();
    }

    @Override
    public File getBaseObject() {
        return file;
    }
}
