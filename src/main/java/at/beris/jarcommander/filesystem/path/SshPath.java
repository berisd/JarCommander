/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.path;

import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.file.client.SftpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SshPath implements IPath {

    private SftpClient context;
    private String path;

    public SshPath(SftpClient context, String path) {
        this.context = context;
        this.path = path;
    }

    @Override
    public List<IFile> getEntries() throws IOException {
        List<IFile> fileList = new ArrayList<IFile>();

//        ChannelSftp channel = (ChannelSftp) context.getChannel();
//
//        if (channel != null) {
//            Vector<ChannelSftp.LsEntry> dirEntries = null;
//            try {
//                channel.cd(path);
//                dirEntries = channel.ls(path);
//            } catch (SftpException e) {
//                Application.logException(e);
//            }
//
//            for (ChannelSftp.LsEntry dirEntry : dirEntries) {
//                if (dirEntry.getFilename().equals(".") || (path.equals(File.separator) && dirEntry.getFilename().equals("..")))
//                    continue;
//
//                URL url = new URL("ssh", context.getHost(), context.getPort(), path + (!path.equals(File.separator) ? File.separator : "") + dirEntry.getFilename());
//                IFile file = new SshFile(url, context);
//                fileList.add(file);
//            }
//        }

        return fileList;
    }

    @Override
    public IPath normalize() {
        return new SshPath(context, path.replace(".." + File.separator, ""));
    }

    @Override
    public IPath getRoot() {
        String[] pathParts = path.split(File.separator);
        String rootPath = pathParts[0];
        return new SshPath(context, rootPath);
    }

    @Override
    public IPath getParent() {
        String[] pathParts = path.split(File.separator);
        String parentPath = StringUtils.join(pathParts, File.separator, 0, pathParts.length - 1);
        return new SshPath(context, parentPath + File.separator);
    }

    @Override
    public IFile toFile() {
        URL url;
        try {
            url = new URL("ssh", context.getHost(), context.getPort(), path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

//        return new SshFile(url, context);
        return null;
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
