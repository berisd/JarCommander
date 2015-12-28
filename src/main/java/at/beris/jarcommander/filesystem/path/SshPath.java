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
import at.beris.jarcommander.filesystem.file.FileFactory;
import at.beris.jarcommander.filesystem.file.IFile;
import com.jcraft.jsch.Buffer;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.CustomChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SshPath implements IPath<String> {

    private SshContext context;
    private String path;
    private FileFactory fileFactory;

    public SshPath(SshContext context, String path, FileFactory fileFactory) {
        this.context = context;
        this.path = path;
        this.fileFactory = fileFactory;
    }

    @Override
    public String getBaseObject() {
        return path;
    }

    @Override
    public List<IFile> getEntries() {
        List<IFile> fileList = new ArrayList<IFile>();

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

                IFile file = fileFactory.newSshFileInstance(context, path + (!path.equals(File.separator) ? File.separator : "") + dirEntry.getFilename(), dirEntry);
                fileList.add(file);
            }
        }

        return fileList;
    }

    @Override
    public IPath normalize() {
        return new SshPath(context, path.replace(".." + File.separator, ""), fileFactory);
    }

    @Override
    public IPath getRoot() {
        String[] pathParts = path.split(File.separator);
        String rootPath = pathParts[0];
        return new SshPath(context, rootPath, fileFactory);
    }

    @Override
    public IPath getParent() {
        String[] pathParts = path.split(File.separator);
        String parentPath = StringUtils.join(pathParts, File.separator, 0, pathParts.length - 1);
        return new SshPath(context, parentPath + File.separator, fileFactory);
    }

    @Override
    public IFile toFile() {
        CustomChannelSftp c = new CustomChannelSftp();
        Buffer buf = new Buffer();
        buf.putInt(0);
        ChannelSftp.LsEntry lsEntry = c.new CustomLsEntry("", "", c.getATTR(buf));

        return fileFactory.newSshFileInstance(context, path, lsEntry);
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public int compareTo(IPath iPath) {
        return 0;
    }
}
