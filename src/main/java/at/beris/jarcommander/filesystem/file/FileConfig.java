/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.file.client.SftpClient;
import at.beris.jarcommander.filesystem.file.provider.LocalArchiveOperationProvider;
import at.beris.jarcommander.filesystem.file.provider.LocalArchivedFileOperationProvider;
import at.beris.jarcommander.filesystem.file.provider.LocalFileOperationProvider;
import at.beris.jarcommander.filesystem.file.provider.SftpFileOperationProvider;
import at.beris.jarcommander.filesystem.protocol.Protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileConfig {
    private Map<Protocol, Map<FileType, Class>> fileOperationProviderClassMap;
    private Map<Protocol, Class> clientClassMap;

    public FileConfig() {
        fileOperationProviderClassMap = new HashMap<>();
        clientClassMap = new HashMap<>();
        put(Protocol.FILE, createLocalFileOperationProviderClassMap(), null);
        put(Protocol.SFTP, Collections.singletonMap(FileType.DEFAULT, SftpFileOperationProvider.class), SftpClient.class);
    }

    public Map<FileType, Class> getFileOperationProviderClassMap(Protocol protocol) {
        return fileOperationProviderClassMap.get(protocol);
    }

    public Class getClientClass(Protocol protocol) {
        return clientClassMap.get(protocol);
    }

    public void put(Protocol protocol, Map<FileType, Class> fileOperationProviderClassForFileExt, Class clientClass) {
        fileOperationProviderClassMap.put(protocol, fileOperationProviderClassForFileExt);
        clientClassMap.put(protocol, clientClass);
    }

    private Map<FileType, Class> createLocalFileOperationProviderClassMap() {
        Map<FileType, Class> localFileProviderForExtMap = new HashMap<>();
        localFileProviderForExtMap.put(FileType.DEFAULT, LocalFileOperationProvider.class);
        localFileProviderForExtMap.put(FileType.ARCHIVED, LocalArchivedFileOperationProvider.class);
        localFileProviderForExtMap.put(FileType.ARCHIVE, LocalArchiveOperationProvider.class);
        return localFileProviderForExtMap;
    }
}
