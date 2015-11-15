/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.SshContext;
import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.filesystem.path.SshPath;
import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class SshFile implements JFile<ChannelSftp.LsEntry> {
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
    public void setParentFile(JFile parentFile) {
        this.parentFile = (ChannelSftp.LsEntry) parentFile.getBaseObject();
    }

    @Override
    public JFile getParentFile() {
        String[] pathParts = path.split(File.separator);
        String parentPath = StringUtils.join(pathParts, File.separator, 0, pathParts.length - 2);
        return JFileFactory.newSshFileInstance(context, parentPath, this.parentFile);
    }

    @Override
    public void addFile(Set<JFile> files) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean exists() {
        throw new NotImplementedException("");
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
    public List<JFile> list() {
        throw new NotImplementedException("");
    }

    @Override
    public String getAbsolutePath() {
        throw new NotImplementedException("");
    }

    @Override
    public JPath toPath() {
        return new SshPath(context, path);
    }

    @Override
    public void delete() {
        throw new NotImplementedException("");
    }

    @Override
    public List<JFile> listFiles() {
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
}
