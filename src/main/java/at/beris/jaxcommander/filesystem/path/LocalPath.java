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
import at.beris.jaxcommander.filesystem.file.JFile;
import at.beris.jaxcommander.filesystem.file.JFileFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocalPath implements JPath<Path> {
    private Path path;

    public LocalPath(Path path) {
        this.path = path;
    }

    @Override
    public Path getBaseObject() {
        return path;
    }

    @Override
    public List<JFile> getEntries() {
        List<JFile> entryList = new ArrayList<>();
        try {
            for (Path childPath : Files.newDirectoryStream(path))
                entryList.add(JFileFactory.newInstance(childPath.toFile()));
        } catch (IOException e) {
            Application.logException(e);
        }
        return entryList;
    }

    @Override
    public JPath normalize() {
        return new LocalPath(path.normalize());
    }

    @Override
    public JPath getRoot() {
        return new LocalPath(path.getRoot());
    }

    @Override
    public JPath getParent() {
        return new LocalPath(path.getParent());
    }

    @Override
    public JFile toFile() {
        return JFileFactory.newInstance(path.toFile());
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public int compareTo(JPath jPath) {
        return path.compareTo((Path) jPath.getBaseObject());
    }
}
