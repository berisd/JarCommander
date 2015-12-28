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
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class SshFile implements IFile<ChannelSftp.LsEntry> {
    private final static Logger LOGGER = Logger.getLogger(SshFile.class);

    private SshContext context;
    private ChannelSftp.LsEntry file;
    private String fileName;
    private SftpATTRS sftpAttrs;
    private String path;
    private ChannelSftp.LsEntry parentFile;
    private SshFileOperationProvider fileOperationProvider;
    private Set<Attribute> attributes;
    private FileFactory fileFactory;

    public SshFile(SshContext context, String path, ChannelSftp.LsEntry file, FileFactory fileFactory) {
        this(context, path, fileFactory);
        this.file = file;
        this.fileName = file.getFilename();
        this.sftpAttrs = file.getAttrs();

        attributes = new LinkedHashSet<>();
        setPermissions();
    }

    public SshFile(SshContext context, String path, FileFactory fileFactory) {
        this.context = context;
        this.path = path;
        this.fileFactory = fileFactory;
        this.fileOperationProvider = new SshFileOperationProvider();
        this.fileName = path.substring(path.lastIndexOf(File.separatorChar) + 1);

        try {
            ChannelSftp channel = (ChannelSftp) this.context.getChannel();
            sftpAttrs = channel.stat(path);
        } catch (SftpException e) {
            if (ChannelSftp.SSH_FX_NO_SUCH_FILE != e.id)
                Application.logException(e);
        }
    }

    @Override
    public ChannelSftp.LsEntry getBaseObject() {
        return this.file;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public Date getLastModified() {
        return new Date(((long) sftpAttrs.getMTime()) * 1000L);
    }

    @Override
    public long getSize() {
        return sftpAttrs.getSize();
    }

    @Override
    public boolean isDirectory() {
        return sftpAttrs.isDir();
    }

    @Override
    public void setParentFile(IFile parentFile) {
        this.parentFile = (ChannelSftp.LsEntry) parentFile.getBaseObject();
    }

    @Override
    public IFile getParentFile() {
        String[] pathParts = path.split(File.separator);
        String parentPath = StringUtils.join(pathParts, File.separator, 0, pathParts.length - 2);
        return fileFactory.newSshFileInstance(context, parentPath, this.parentFile);
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
            try {
                channel.stat(path);
                exists = true;
            } catch (SftpException e) {
                if (ChannelSftp.SSH_FX_NO_SUCH_FILE != e.id)
                    Application.logException(e);
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
    public String getPath() {
        throw new NotImplementedException("");
    }

    @Override
    public String getAbsolutePath() {
        return this.path;
    }

    @Override
    public IPath toPath() {
        return new SshPath(context, path, fileFactory);
    }

    @Override
    public void delete() {
        fileOperationProvider.delete(this);
    }

    @Override
    public List<IFile> listFiles() {
        throw new NotImplementedException("");
    }

    @Override
    public byte[] checksum() {
        throw new NotImplementedException("");
    }

    @Override
    public void copy(IFile targetFile, CopyListener listener) throws IOException {
        fileOperationProvider.copy(this, targetFile, listener);
        refresh();
    }

    @Override
    public boolean create() throws IOException {
        throw new NotImplementedException("");
    }

    @Override
    public void refresh() throws IOException {
        try {
            ChannelSftp channelSftp = (ChannelSftp) context.getChannel();
            // fixes empty sftpAttrs (e.g. filesize=0)
            channelSftp.pwd();
            sftpAttrs = channelSftp.stat(path);
        } catch (SftpException e) {
            throw new IOException(e);
//        } catch (JSchException e) {
//            throw new IOException(e);
        }
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
