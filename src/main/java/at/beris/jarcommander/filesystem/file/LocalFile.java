/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.filesystem.path.IPath;
import at.beris.jarcommander.filesystem.path.LocalPath;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static at.beris.jarcommander.filesystem.file.FileHelper.isArchive;

public class LocalFile implements IFile<File> {
    private File file;
    private File parentFile;
    private Set<Attribute> windowsAttributes;

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
    public void setParentFile(IFile parentFile) {
        this.parentFile = (File) parentFile.getBaseObject();
    }

    @Override
    public IFile getParentFile() {
        return FileFactory.newInstance(parentFile);
    }

    @Override
    public IPath toPath() {
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
        return windowsAttributes;
    }

    @Override
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public void addFile(Set<IFile> files) {
        throw new NotImplementedException("");
    }

    @Override
    public List<IFile> list() {
        List<IFile> fileList = new ArrayList<>();

        if (file.isDirectory() || isArchive(this)) {
            Path path = file.toPath();
            if (!path.toString().equals(File.separator) && StringUtils.countMatches(path.toString(), FileSystems.getDefault().getSeparator()) >= 1) {
                IFile backFile = FileFactory.newInstance(new File(".."));
                backFile.setParentFile(this);
                fileList.add(backFile);
            }
        }

        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                fileList.add(FileFactory.newInstance(childFile));
            }
        } else if (FileHelper.isArchive(this)) {
            fileList.addAll(FileFactory.createListFromArchive(this.file));
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
    public void delete() {
        try {
            Files.walkFileTree(file.toPath(), new DeletingFileVisitor());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<IFile> listFiles() {
        List<IFile> fileList = new ArrayList<>();

        File[] files = file.listFiles();

        if (files != null) {
            for (File file : files) {
                fileList.add(FileFactory.newInstance(file));
            }
        }

        return fileList;
    }

    @Override
    public byte[] checksum() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.reset();
            messageDigest.update(Files.readAllBytes(file.toPath()));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            Application.logException(e);
        } catch (IOException e) {
            Application.logException(e);
        }
        return null;
    }

    private void fillAttributes() {
        windowsAttributes = new LinkedHashSet<>();
        if (file.canRead()) {
            windowsAttributes.add(WindowsAttribute.READ);
        }
        if (file.canWrite()) {
            windowsAttributes.add(WindowsAttribute.WRITE);
        }
        if (file.canExecute()) {
            windowsAttributes.add(WindowsAttribute.EXECUTE);
        }
        if (file.isHidden()) {
            windowsAttributes.add(WindowsAttribute.HIDDEN);
        }
    }

    private class DeletingFileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                throws IOException {
            if (attributes.isRegularFile()) {
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path directory, IOException ioe)
                throws IOException {
            Files.delete(directory);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException ioe)
                throws IOException {
            Application.logException(ioe);
            return FileVisitResult.CONTINUE;
        }
    }
}
