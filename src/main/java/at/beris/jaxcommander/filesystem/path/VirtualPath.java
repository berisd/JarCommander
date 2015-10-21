/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.path;

import at.beris.jaxcommander.filesystem.file.VirtualFile;

import java.util.List;

public class VirtualPath<T> {
    private PathProvider<T> pathProvider;

    public VirtualPath(PathProvider<T> pathProvider) {
        this.pathProvider = pathProvider;
    }

    public T getBaseObject() {
        return pathProvider.getBaseObject();
    }

    public int compareTo(VirtualPath virtualPath) {
        return pathProvider.compareTo(virtualPath);
    }

    public List<VirtualFile> getEntries() {
        return pathProvider.getEntries();
    }

    public VirtualPath normalize() {
        return new VirtualPath(pathProvider.newInstance(pathProvider.normalize()));
    }

    public VirtualPath getRoot() {
        return new VirtualPath(pathProvider.newInstance(pathProvider.getRoot()));
    }

    public VirtualPath getParent() {
        return new VirtualPath(pathProvider.newInstance(pathProvider.getParent()));
    }

    public VirtualFile toFile() {
        return pathProvider.toFile();
    }

    @Override
    public String toString() {
        return pathProvider.toString();
    }
}
