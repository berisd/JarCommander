/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.filesystem.path.LocalPath;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static at.beris.jarcommander.Application.logException;

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
    public void addFile(Set<JFile> files) {
        throw new NotImplementedException("");
    }

    @Override
    public List<JFile> list() {

        // if file is an archive that give back a list of archiveEntries
        List<JFile> fileList = new ArrayList<>();

        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                fileList.add(JFileFactory.newInstance(childFile));
            }
        } else if (FilenameUtils.getExtension(file.toString()).toUpperCase().equals("ZIP")) {
            try {
                ArchiveStreamFactory factory = new ArchiveStreamFactory();
                InputStream fis = new BufferedInputStream(new FileInputStream(file));
                ArchiveInputStream ais = factory.createArchiveInputStream(fis);

                ArchiveEntry ae;
                Map<String, JFile> pathToArchiveEntryMap = new HashMap<>();
                Map<String, Set<JFile>> archiveEntryToChildrenMap = new HashMap<>();

                while ((ae = ais.getNextEntry()) != null) {
                    JFile file = JFileFactory.newInstance(ae, this.file);

                    String[] parts = ae.getName().split(File.separator);
                    pathToArchiveEntryMap.put(parts[parts.length - 1], file);

                    if (parts.length > 1) {
                        JFile currentFile = file;
                        for (int i = parts.length - 2; i >= 0; i--) {
                            JFile parentFile = pathToArchiveEntryMap.get(parts[i]);
                            currentFile.setParentFile(parentFile);
                            currentFile = parentFile;
                        }

                        for (int i = 0; i < parts.length - 1; i++) {
                            if (i + 1 < parts.length) {
                                if (archiveEntryToChildrenMap.get(parts[i]) == null) {
                                    archiveEntryToChildrenMap.put(parts[i], new LinkedHashSet<JFile>());
                                }
                                archiveEntryToChildrenMap.get(parts[i]).add(pathToArchiveEntryMap.get(parts[i + 1]));
                            }
                        }

                        for (Map.Entry<String, Set<JFile>> entry : archiveEntryToChildrenMap.entrySet()) {
                            Set<JFile> children = archiveEntryToChildrenMap.get(entry.getKey());
                            pathToArchiveEntryMap.get(entry.getKey()).addFile(children);
                        }

                    } else {
                        file.setParentFile(JFileFactory.newInstance(this.file));
                        fileList.add(file);
                    }
                }
                ais.close();
                fis.close();
            } catch (FileNotFoundException e) {
                logException(e);
            } catch (ArchiveException e) {
                logException(e);
            } catch (IOException e) {
                logException(e);
            }
        }

        return fileList;
    }

    @Override
    public File getBaseObject() {
        return file;
    }

    @Override
    public String toString() {
        return file.toString();
    }

    @Override
    public void setLastModified(Date date) {
        throw new NotImplementedException("");
    }

    @Override
    public void setName(String name) {
        throw new NotImplementedException("");
    }

    @Override
    public void setSize(long size) {
        throw new NotImplementedException("");
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
