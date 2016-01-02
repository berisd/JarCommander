/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.path;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.filesystem.file.FileManager;
import at.beris.jarcommander.filesystem.file.IFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocalPath implements IPath {
    private Path path;

    public LocalPath(Path path) {
        this.path = path;
    }

    @Override
    public List<IFile> getEntries() {
        List<IFile> entryList = new ArrayList<>();
        try {
            if (!path.toString().equals("/") && StringUtils.countMatches(path.toString(), FileSystems.getDefault().getSeparator()) >= 1) {
                entryList.add(FileManager.newFile(new File("..").toURI().toURL()));
            }

            for (Path childPath : Files.newDirectoryStream(path)) {
                File file = childPath.toFile();
                entryList.add(FileManager.newFile(file.toURI().toURL()));
            }
        } catch (IOException e) {
            Application.logException(e);
        }
        return entryList;
    }

    @Override
    public IPath normalize() {
        return new LocalPath(path.normalize());
    }

    @Override
    public IPath getRoot() {
        return new LocalPath(path.getRoot());
    }

    @Override
    public IPath getParent() {
        return new LocalPath(path.getParent());
    }

    @Override
    public IFile toFile() {
        try {
            return FileManager.newFile(path.toUri().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public int compareTo(IPath iPath) {
        return path.toString().compareTo(iPath.toString());
    }
}
