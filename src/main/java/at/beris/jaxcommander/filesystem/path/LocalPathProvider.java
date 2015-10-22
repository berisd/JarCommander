/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.path;

import at.beris.jaxcommander.Application;
import at.beris.jaxcommander.filesystem.file.VirtualFile;
import at.beris.jaxcommander.filesystem.file.VirtualFileFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocalPathProvider implements PathProvider<Path> {
    private Path path;

    public LocalPathProvider(Path path) {
        this.path = path;
    }

    @Override
    public Path getBaseObject() {
        return path;
    }

    @Override
    public List<VirtualFile> getEntries() {
        List<VirtualFile> entryList = new ArrayList<>();
        try {
            for (Path childPath : Files.newDirectoryStream(path))
                entryList.add(VirtualFileFactory.newInstance(childPath.toFile()));
        } catch (IOException e) {
            Application.logException(e);
        }
        return entryList;
    }

    @Override
    public Path normalize() {
        return path.normalize();
    }

    @Override
    public Path getRoot() {
        return path.getRoot();
    }

    @Override
    public Path getParent() {
        return path.getParent();
    }

    @Override
    public VirtualFile toFile() {
        return VirtualFileFactory.newInstance(path.toFile());
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public int compareTo(VirtualPath virtualPath) {
        return path.compareTo((Path) virtualPath.getBaseObject());
    }

    @Override
    public PathProvider<Path> newInstance(Path object) {
        return new LocalPathProvider(object);
    }
}
