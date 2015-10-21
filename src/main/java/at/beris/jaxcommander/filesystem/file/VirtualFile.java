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

import java.util.Date;

public class VirtualFile<T> {

    private Provider<T> provider;
    private VirtualFile<T> parentFile;

    public VirtualFile(Provider<T> provider) {
        this.provider = provider;
    }

    public T getBaseObject() {
        return provider.getBaseObject();
    }

    public String getName() {
        return provider.getName();
    }

    public Date getLastModified() {
        return provider.getLastModified();
    }

    public long getSize() {
        return provider.getSize();
    }

    public boolean isDirectory() {
        return provider.isDirectory();
    }

    public void setParentFile(VirtualFile parentFile) {
        this.parentFile = parentFile;
    }

    public VirtualFile getParentFile() {
        return parentFile;
    }

    public VirtualPath toPath() {
        return provider.toPath();
    }
}
