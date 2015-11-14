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
import at.beris.jarcommander.filesystem.file.JFileFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
            Vector<ChannelSftp.LsEntry> dirEntries = null;
            try {
                channel.cd(path);
                dirEntries = channel.ls(path);
            } catch (SftpException e) {
                Application.logException(e);
            }

            for (ChannelSftp.LsEntry dirEntry : dirEntries) {
                if (dirEntry.getFilename().equals(".") || (path.equals(File.separator) && dirEntry.getFilename().equals("..")))
                    continue;

                JFile file = JFileFactory.newSshFileInstance(context, path + dirEntry.getFilename() + File.separator, dirEntry);
                fileList.add(file);
            }
        }

        return fileList;
    }

    @Override
    public JPath normalize() {
        return new SshPath(context, path.replace(".." + File.separator, ""));
    }

    @Override
    public JPath getRoot() {
        String[] pathParts = path.split(File.separator);
        String rootPath = pathParts[0];
        return new SshPath(context, rootPath);
    }

    @Override
    public JPath getParent() {
        String[] pathParts = path.split(File.separator);
        String parentPath = StringUtils.join(pathParts, File.separator, 0, pathParts.length - 1);
        return new SshPath(context, parentPath + File.separator);
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
