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
import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SshFile implements JFile<ChannelSftp.LsEntry> {
    private ChannelSftp.LsEntry file;
    private ChannelSftp.LsEntry parentFile;
    private Date lastModified;
    private String name;
    private Set<Attribute> attributes;

    public SshFile(ChannelSftp.LsEntry file) {
        this.file = file;
        attributes = new LinkedHashSet<>();
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
        return 0;
    }

    @Override
    public boolean isDirectory() {
        return file.getLongname().startsWith("d");
    }

    @Override
    public void setParentFile(JFile parentFile) {
        this.parentFile = (ChannelSftp.LsEntry) parentFile.getBaseObject();
    }

    @Override
    public JFile getParentFile() {
        return JFileFactory.newSshFileInstance(this.parentFile);
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
        throw new NotImplementedException("");
    }

    @Override
    public void setLastModified(Date date) {
        this.lastModified = date;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setSize(long size) {

    }
}
