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
import at.beris.jaxcommander.filesystem.path.LocalPath;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LocalFile implements JFile<File> {
    private File file;
    private File parentFile;
    private Set<Attribute> attributes;

    public LocalFile(File file) {
        this.file = file;
        fillAttributes();
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
    public void setParentFile(JFile parentFile) {
        this.parentFile = (File) parentFile.getBaseObject();
    }

    @Override
    public JFile getParentFile() {
        return JFileFactory.newInstance(parentFile);
    }

    @Override
    public JPath toPath() {
        return new LocalPath(file.toPath());
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean mkdirs() {
        return file.mkdirs();
    }

    @Override
    public Set<Attribute> attributes() {
        return attributes;
    }

    @Override
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public List<JFile> list() {
        List<JFile> fileList = new ArrayList<>();

        for (File childFile : file.listFiles()) {
            fileList.add(JFileFactory.newInstance(childFile));
        }

        return fileList;
    }

    @Override
    public File getBaseObject() {
        return file;
    }

    private void fillAttributes() {
        attributes = new LinkedHashSet<>();
        if (file.canRead()) {
            attributes.add(Attribute.READ);
        }
        if (file.canWrite()) {
            attributes.add(Attribute.WRITE);
        }
        if (file.canExecute()) {
            attributes.add(Attribute.EXECUTE);
        }
        if (file.isHidden()) {
            attributes.add(Attribute.HIDDEN);
        }
    }
}
