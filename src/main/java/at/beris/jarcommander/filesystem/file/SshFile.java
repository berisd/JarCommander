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
import at.beris.jarcommander.filesystem.SshContext;
import at.beris.jarcommander.filesystem.path.IPath;
import at.beris.jarcommander.filesystem.path.SshPath;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;


public class SshFile implements IFile<ChannelSftp.LsEntry> {
    private SshContext context;
    private ChannelSftp.LsEntry file;
    private String path;
    private ChannelSftp.LsEntry parentFile;
    private Date lastModified;
    private Set<Attribute> attributes;


    public SshFile(SshContext context, String path, ChannelSftp.LsEntry file) {
        this.context = context;
        this.file = file;
        this.path = path;
        attributes = new LinkedHashSet<>();
        lastModified = new Date(((long) file.getAttrs().getMTime()) * 1000L);
        setPermissions();
    }

    @Override
    public ChannelSftp.LsEntry getBaseObject() {
        return this.file;
    }

    @Override
    public String getName() {
        return file.getFilename();
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public long getSize() {
        return file.getAttrs().getSize();
    }

    @Override
    public boolean isDirectory() {
        return file.getAttrs().isDir();
    }

    @Override
    public void setParentFile(IFile parentFile) {
        this.parentFile = (ChannelSftp.LsEntry) parentFile.getBaseObject();
    }

    @Override
    public IFile getParentFile() {
        String[] pathParts = path.split(File.separator);
        String parentPath = StringUtils.join(pathParts, File.separator, 0, pathParts.length - 2);
        return FileFactory.newSshFileInstance(context, parentPath, this.parentFile);
    }

    @Override
    public void addFile(Set<IFile> files) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean exists() {
        boolean exists = false;

        ChannelSftp channel = (ChannelSftp) context.getChannel();

        if (channel != null) {
            Vector<ChannelSftp.LsEntry> dirEntries = null;
            try {
                channel.cd(path);
                dirEntries = channel.ls(path);
            } catch (SftpException e) {
                Application.logException(e);
            }

            for (ChannelSftp.LsEntry dirEntry : dirEntries) {
                if (dirEntry.getFilename().equals(file.getFilename())) {
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }

    @Override
    public boolean mkdirs() {
        throw new NotImplementedException("");
    }

    @Override
    public Set<Attribute> attributes() {
        return attributes;
    }

    @Override
    public List<IFile> list() {
        throw new NotImplementedException("");
    }

    @Override
    public String getAbsolutePath() {
        return this.path;
    }

    @Override
    public IPath toPath() {
        return new SshPath(context, path);
    }

    @Override
    public void delete() {
        throw new NotImplementedException("");
    }

    @Override
    public List<IFile> listFiles() {
        throw new NotImplementedException("");
    }

    @Override
    public byte[] checksum() {
        throw new NotImplementedException("");
    }

    private void setPermissions() {
        int permissions = file.getAttrs().getPermissions();

        for (UnixAttribute attribute : UnixAttribute.values()) {
            if ((permissions & attribute.getValue()) != 0) {
                this.attributes.add(attribute);
            }
        }
    }

    public SshContext getContext() {
        return context;
    }
}
