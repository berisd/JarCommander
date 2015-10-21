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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VirtualPath {
    Path path;
    public VirtualPath(Path path) {
        this.path = path;
    }

    public int compareTo(VirtualPath o) {
        return path.compareTo(o.getPath());
    }

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

    public VirtualPath normalize() {
        return new VirtualPath(path.normalize());
    }

    public VirtualPath getRoot() {
        return new VirtualPath(path.getRoot());
    }

    public VirtualPath getParent() {
        return new VirtualPath(path.getParent());
    }

    public File toFile() {
        return path.toFile();
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
