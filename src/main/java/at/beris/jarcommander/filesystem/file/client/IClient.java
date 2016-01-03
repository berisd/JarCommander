/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IClient {
    void init();

    String getHost();

    void setHost(String host);

    int getPort();

    void setPort(int port);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    void connect();

    void disconnect();

    void deleteFile(String Path);

    void createFile(String path);

    boolean exists(String path);

    void createDirectory(String path);

    void deleteDirectory(String Path);

    InputStream getInputStream(String path);

    OutputStream getOutputStream(String path);

    FileInfo getFileInfo(String path);
}
