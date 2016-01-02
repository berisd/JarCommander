/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file.provider;

import at.beris.jarcommander.filesystem.file.FileManager;
import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.file.client.IClient;
import at.beris.jarcommander.filesystem.model.FileModel;
import com.jcraft.jsch.SftpATTRS;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class SftpFileOperationProvider implements IFileOperationProvider {

    @Override
    public IFile create(IClient client, FileModel model) throws IOException {
        if (model.isDirectory())
            client.makeDirectory(model.getPath());
        else {
            client.createFile(model.getPath());
        }
        return null;
    }

    @Override
    public boolean exists(IClient client, FileModel model) {
        return client.exists(model.getPath());
    }

    @Override
    public void delete(IClient client, FileModel model) {
        client.delete(model.getPath());
    }

    @Override
    public void add(IFile parent, IFile child) {
        throw new NotImplementedException("");
    }

    @Override
    public byte[] checksum(IClient client, FileModel model) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + "tmpfile_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        IFile tempFile = copyToLocalFile(client, model, tempFilePath);
        return tempFile.checksum();
    }

    @Override
    public List<IFile> list(IClient client, FileModel model) throws IOException {
        return null;
    }

    @Override
    public void updateModel(IClient client, FileModel model) {
        SftpATTRS sftpATTRS;
        try {
            sftpATTRS = (SftpATTRS) client.getFileInfo(model.getPath());
        } catch (FileNotFoundException e) {
            return;
        }
        model.setLastModified(new Date(sftpATTRS.getMTime() * 1000L));
        model.setSize(sftpATTRS.getSize());
    }

    @Override
    public void save(URL url, FileModel model) {
        throw new NotImplementedException("");
    }

    @Override
    public InputStream getInputStream(IClient client, FileModel model) throws IOException {
        return client.getInputStream(model.getPath());
    }

    @Override
    public OutputStream getOutputStream(IClient client, FileModel model) throws IOException {
        return client.getOutputStream(model.getPath());
    }

    private IFile copyToLocalFile(IClient client, FileModel model, String path) throws IOException {
        byte[] buffer = new byte[1024];
        int length;

        try (
                InputStream inputStream = client.getInputStream(model.getPath());
                OutputStream outputStream = new FileOutputStream(path)
        ) {
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        return FileManager.newLocalFile(path);
    }
}
