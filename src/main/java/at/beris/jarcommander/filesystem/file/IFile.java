/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.file.client.IClient;
import at.beris.jarcommander.filesystem.file.provider.IFileOperationProvider;
import at.beris.jarcommander.filesystem.model.FileModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IFile {
    URL getUrl();

    FileModel getModel();

    IClient getClient();

    IFileOperationProvider getFileOperationProvider();

    String getName();

    Date getLastModified();

    long getSize();

    void setSize(long size);

    boolean isDirectory();

    void setParent(IFile parent);

    IFile getParent();

    IFile getRoot();

    boolean isRoot();

    void add(IFile file);

    boolean exists();

    Set<Attribute> getAttributes();

    List<IFile> list() throws IOException;

    String getPath();

    void delete();

    byte[] checksum() throws IOException;

    /**
     * File is an archive
     */
    boolean isArchive();

    /**
     * File is archived within an archive
     */
    boolean isArchived();

    void copy(IFile targetFile, CopyListener listener) throws IOException;

    int compareTo(IFile file);

    /**
     * Creates an empty file
     *
     * @return true if the named file does not exist and was successfully created; false if the named file already exists
     * @throws IOException
     */
    void create() throws IOException;

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;
}
