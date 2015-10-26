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
import at.beris.jarcommander.filesystem.SshContext;
import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.file.SshFile;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SshPath implements JPath<String> {

    private SshContext context;
    private String path;

    public SshPath(SshContext context, String path) {
        this.context = context;
        this.path = path;
    }

    @Override
    public String getBaseObject() {
        return path;
    }

    @Override
    public List<JFile> getEntries() {
        List<JFile> fileList = new ArrayList<JFile>();

        ChannelSftp channel = (ChannelSftp) context.getChannel();

        if (channel != null) {
            //
            //            Vector files = sftp.ls("*");
            //            System.out.printf("Found %d files in dir %s%n", files.size(), directory);
            Vector<ChannelSftp.LsEntry> dirEntries = null;
            try {
                channel.cd(path);
                dirEntries = channel.ls(path);
            } catch (SftpException e) {
                Application.logException(e);
            }

            for (ChannelSftp.LsEntry dirEntry : dirEntries) {
                JFile file = new SshFile(dirEntry);
                file.setName(dirEntry.getFilename());
                fileList.add(file);
            }
        }

        return fileList;
    }

    @Override
    public JPath normalize() {
        throw new NotImplementedException("");
    }

    @Override
    public JPath getRoot() {
        throw new NotImplementedException("");
    }

    @Override
    public JPath getParent() {
        throw new NotImplementedException("");
    }

    @Override
    public JFile toFile() {
        throw new NotImplementedException("");
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public int compareTo(JPath jPath) {
        return 0;
    }
}