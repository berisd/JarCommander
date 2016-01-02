/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file.provider;

import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.file.client.IClient;
import at.beris.jarcommander.filesystem.model.FileModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public interface IFileOperationProvider {
    /**
     * Creates a new  physical representation
     *
     * @param model
     * @return
     */
    //TODO void zurückgeben. calling object is what we want
    IFile create(IClient client, FileModel model) throws IOException;

    boolean exists(IClient client, FileModel model);

    void delete(IClient client, FileModel model);

    void add(IFile parent, IFile child);

    byte[] checksum(IClient client, FileModel model) throws IOException;

    /**
     * List files in this file
     *
     * @param client
     * @param model
     * @return
     * @throws IOException
     */
    List<IFile> list(IClient client, FileModel model) throws IOException;

    /**
     * Updates the Model with information from the physical file.
     *
     * @param url
     * @param model
     */
    void updateModel(IClient client, FileModel model);

    /**
     * Saves Model on the filesystem
     *
     * @param url
     * @param model
     */
    void save(URL url, FileModel model);

    InputStream getInputStream(IClient client, FileModel model) throws IOException;

    OutputStream getOutputStream(IClient client, FileModel model) throws IOException;
}
