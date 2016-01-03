/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.FileUtils;
import at.beris.jarcommander.filesystem.file.client.IClient;
import at.beris.jarcommander.filesystem.file.provider.IFileOperationProvider;
import at.beris.jarcommander.filesystem.model.FileModel;
import at.beris.jarcommander.filesystem.protocol.Protocol;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FileContext {
    private FileConfig fileConfig;

    private Map<String, IClient> siteToClientMap;
    private Map<Protocol, Map<FileType, IFileOperationProvider>> protocolFileTypeToFileOperationProviderMap;

    public FileContext(FileConfig fileConfig) {
        registerProtocolURLStreamHandlers();
        this.fileConfig = fileConfig;
        this.siteToClientMap = new HashMap<>();
        this.protocolFileTypeToFileOperationProviderMap = new HashMap<>();
    }

    /**
     * Creates a local file. (Convenience method)
     *
     * @param path
     * @return
     */
    public IFile newLocalFile(String path) {
        try {
            return newFile((IFile) null, new java.io.File(path).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a file. (Convenience method)
     *
     * @param url
     * @return
     */
    public IFile newFile(String url) {
        try {
            return newFile((IFile) null, new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public IFile newFile(URL parentUrl, URL url) {
        return newFile(newFile(parentUrl), url);
    }

    /**
     * Creates a file instance for the corresponding url
     *
     * @param parent
     * @param url
     * @return
     */
    public IFile newFile(IFile parent, URL url) {
        URL normalizedUrl = FileUtils.normalizeUrl(url);
        String protocolString = normalizedUrl.getProtocol();
        Protocol protocol = null;
        try {
            protocol = Protocol.valueOf(normalizedUrl.getProtocol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown protocol: " + protocolString);
        }

        if (fileConfig.getFileOperationProviderClassMap(protocol) == null)
            throw new RuntimeException("No configuration found for protocol: " + protocolString);

        File file = null;
        try {
            IClient client = createClientInstance(normalizedUrl, fileConfig.getClientClass(protocol));
            IFileOperationProvider fileOperationProvider = createFileOperationProviderInstance(normalizedUrl, fileConfig.getFileOperationProviderClassMap(protocol));
            file = new File(parent, normalizedUrl, new FileModel(), fileOperationProvider, client);
            if (file.exists())
                file.updateModel();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    public IFile newFile(URL url) {
        URL normalizedUrl = FileUtils.normalizeUrl(url);
        String fullPath = normalizedUrl.getPath();
        String[] pathParts = fullPath.split("/");

        String path = "";
        IFile parentFile = null;

        for (int i = 0; i < pathParts.length; i++) {
            path += pathParts[i];

            if ((i < pathParts.length - 1) || fullPath.endsWith("/"))
                path += "/";

            try {
                String pathUrlString = getSiteUrlString(normalizedUrl) + path;
                URL pathUrl = new URL(pathUrlString);
                parentFile = newFile(parentFile, pathUrl);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return parentFile;
    }

    /**
     * Set property so that URL class will find custom handlers
     */
    public void registerProtocolURLStreamHandlers() {
        String propertyKey = "java.protocol.handler.pkgs";
        String propertyValue = System.getProperties().getProperty(propertyKey);
        if (StringUtils.isEmpty(propertyValue))
            propertyValue = "";
        else
            propertyValue += "|";
        propertyValue += "at.beris.jarcommander.filesystem.protocol";
        System.getProperties().setProperty(propertyKey, propertyValue);
    }

    public String getSiteUrlString(URL url) {
        String urlString = url.toString();
        return urlString.substring(0, urlString.indexOf("/", urlString.indexOf("//") + 2));
    }

    private IClient createClientInstance(URL url, Class clientClass) throws InstantiationException, IllegalAccessException {
        IClient client = null;
        if (clientClass != null) {
            String siteUrl = getSiteUrlString(url);
            client = siteToClientMap.get(siteUrl);
            if (client == null) {
                client = (IClient) clientClass.newInstance();
                client.setHost(url.getHost());
                client.setPort(url.getPort());
                String userInfoParts[] = url.getUserInfo().split(":");
                client.setUsername(userInfoParts[0]);
                client.setPassword(userInfoParts[1]);
                siteToClientMap.put(siteUrl, client);
            }
        }
        return client;
    }

    private IFileOperationProvider createFileOperationProviderInstance(URL url, Map<FileType, Class> fileTypeToFileOperationProviderClassMap) throws IllegalAccessException, InstantiationException {
        IFileOperationProvider fileOperationProvider = null;
        Class fileOperationProviderClass = null;
        Protocol protocol = Protocol.valueOf(url.getProtocol().toUpperCase());
        FileType fileType = null;

        if (fileTypeToFileOperationProviderClassMap != null && fileTypeToFileOperationProviderClassMap.size() > 0) {
            if (FileUtils.isArchive(url.getPath())) {
                fileType = FileType.ARCHIVE.ARCHIVE;
                fileOperationProviderClass = fileTypeToFileOperationProviderClassMap.get(fileType);
            } else if (FileUtils.isArchived(url)) {
                fileType = FileType.ARCHIVED;
                fileOperationProviderClass = fileTypeToFileOperationProviderClassMap.get(fileType);
            } else {
                fileType = FileType.DEFAULT;
                fileOperationProviderClass = fileTypeToFileOperationProviderClassMap.get(fileType);
            }
        }

        fileOperationProvider = (IFileOperationProvider) fileOperationProviderClass.newInstance();

        Map<FileType, IFileOperationProvider> fileTypeToFileOpProviderMap = protocolFileTypeToFileOperationProviderMap.get(protocol);
        if (fileTypeToFileOpProviderMap == null) {
            fileTypeToFileOpProviderMap = new HashMap<>();
            protocolFileTypeToFileOperationProviderMap.put(protocol, fileTypeToFileOpProviderMap);
        }
        fileTypeToFileOpProviderMap.put(fileType, fileOperationProvider);

        return fileOperationProvider;
    }
}
