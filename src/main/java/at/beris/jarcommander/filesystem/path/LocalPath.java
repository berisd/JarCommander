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
import at.beris.jarcommander.filesystem.file.FileFactory;
import at.beris.jarcommander.filesystem.file.IFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocalPath implements IPath<Path> {
    private Path path;
    private FileFactory fileFactory;

    public LocalPath(Path path, FileFactory fileFactory) {
        this.path = path;
        this.fileFactory = fileFactory;
    }

    @Override
    public Path getBaseObject() {
        return path;
    }

    @Override
    public List<IFile> getEntries() {
        List<IFile> entryList = new ArrayList<>();
        try {
            if (!path.toString().equals("/") && StringUtils.countMatches(path.toString(), FileSystems.getDefault().getSeparator()) >= 1) {
                entryList.add(fileFactory.newInstance(new File("..")));
            }

            for (Path childPath : Files.newDirectoryStream(path)) {
                File file = childPath.toFile();
                entryList.add(fileFactory.newInstance(file));
            }
        } catch (IOException e) {
            Application.logException(e);
        }
        return entryList;
    }

    @Override
    public IPath normalize() {
        return new LocalPath(path.normalize(), fileFactory);
    }

    @Override
    public IPath getRoot() {
        return new LocalPath(path.getRoot(), fileFactory);
    }

    @Override
    public IPath getParent() {
        return new LocalPath(path.getParent(), fileFactory);
    }

    @Override
    public IFile toFile() {
        return fileFactory.newInstance(path.toFile());
    }

    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public int compareTo(IPath iPath) {
        return path.compareTo((Path) iPath.getBaseObject());
    }
}
