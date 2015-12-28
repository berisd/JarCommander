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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static at.beris.jarcommander.filesystem.file.FileHelper.isArchive;

public class LocalFile implements IFile<File> {
    private File file;
    private File parentFile;
    private LocalFileOperationProvider fileOperationProvider;
    private Set<Attribute> windowsAttributes;
    private FileFactory fileFactory;

    public LocalFile(File file, FileFactory fileFactory) {
        this.file = file;
        this.fileFactory = fileFactory;
        this.fileOperationProvider = new LocalFileOperationProvider(fileFactory);
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
        return fileFactory.newInstance(parentFile);
    }

    @Override
    public IPath toPath() {
        return new LocalPath(file.toPath(), fileFactory);
    }

    @Override
    public boolean exists() {
        return fileOperationProvider.exists(file);
    }

    @Override
    public boolean mkdirs() {
        return fileOperationProvider.mkdirs(file);
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
                IFile backFile = fileFactory.newInstance(new File(".."));
                backFile.setParentFile(this);
                fileList.add(backFile);
            }
        }

        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                fileList.add(fileFactory.newInstance(childFile));
            }
        } else if (FileHelper.isArchive(this)) {
            fileList.addAll(fileFactory.createListFromArchive(this.file));
        }

        return fileList;
    }

    @Override
    public String getPath() {
        return file.getPath();
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
        fileOperationProvider.delete(file);
    }

    @Override
    public List<IFile> listFiles() {
        List<IFile> fileList = new ArrayList<>();

        File[] files = file.listFiles();

        if (files != null) {
            for (File file : files) {
                fileList.add(fileFactory.newInstance(file));
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

    public void copy(IFile targetFile, CopyListener listener) throws IOException {
        fileOperationProvider.copy(file, targetFile, listener);
    }

    @Override
    public boolean create() throws IOException {
        return file.createNewFile();
    }

    @Override
    public void refresh() {
        file = new File(file.getAbsolutePath());
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
}
