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
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static at.beris.jarcommander.Application.logException;

public class LocalArchiveOperationProvider extends LocalFileOperationProvider {
    @Override
    public List<IFile> list(IClient client, FileModel model) throws IOException {
        List<IFile> fileList = new ArrayList<>();

//        if (file.isDirectory() || file.isArchive()) {
//            Path path = new File (file.getPath()).toPath();
//            if (!path.toString().equals(File.separator) && StringUtils.countMatches(path.toString(), FileSystems.getDefault().getSeparator()) >= 1) {
//                IFile backFile = FileManager.newFile(file, new File("..").toURI().toURL());
//                fileList.add(backFile);
//            }
//        }
//
//        if (file.isDirectory()) {
//            for (IFile childFile : file.list()) {
//                fileList.add(childFile);
//            }
//        } else if (FileUtils.isArchive(file.getName())) {
//            fileList.addAll(createFileTreeFromArchive(file));
//        }

        return fileList;
    }

    public List<IFile> createFileTreeFromArchive(IFile archiveFile) {
        List<IFile> fileList = new ArrayList<>();
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            InputStream fis = new BufferedInputStream(new FileInputStream(new File(archiveFile.getPath())));
            ArchiveInputStream ais = factory.createArchiveInputStream(fis);

            ArchiveEntry ae;
            Map<String, IFile> pathToArchiveEntryMap = new HashMap<>();
            Map<String, Set<IFile>> archiveEntryToChildrenMap = new HashMap<>();

            while ((ae = ais.getNextEntry()) != null) {
                IFile file = createArchivedFile(ae, archiveFile);

                String[] parts = ae.getName().split(File.separator);
                pathToArchiveEntryMap.put(parts[parts.length - 1], file);

                if (parts.length > 1) {
                    IFile currentFile = file;
                    for (int i = parts.length - 2; i >= 0; i--) {
                        IFile parentFile = pathToArchiveEntryMap.get(parts[i]);
                        currentFile.setParent(parentFile);
                        currentFile = parentFile;
                    }

                    for (int i = 0; i < parts.length - 1; i++) {
                        if (i + 1 < parts.length) {
                            if (archiveEntryToChildrenMap.get(parts[i]) == null) {
                                archiveEntryToChildrenMap.put(parts[i], new LinkedHashSet<>());
                            }
                            archiveEntryToChildrenMap.get(parts[i]).add(pathToArchiveEntryMap.get(parts[i + 1]));
                        }
                    }

                    for (Map.Entry<String, Set<IFile>> entry : archiveEntryToChildrenMap.entrySet()) {
                        Set<IFile> children = archiveEntryToChildrenMap.get(entry.getKey());
                        pathToArchiveEntryMap.get(entry.getKey()).addFile(children);
                    }

                } else {
                    file.setParent(archiveFile);
                    fileList.add(file);
                }
            }
            ais.close();
            fis.close();
        } catch (FileNotFoundException e) {
            logException(e);
        } catch (ArchiveException e) {
            logException(e);
        } catch (IOException e) {
            logException(e);
        }

        return fileList;
    }

    private IFile createArchivedFile(ArchiveEntry archiveEntry, IFile archiveFile) {
        try {
            URL parentUrl = archiveFile.getUrl();
            URL url = new URL(parentUrl.getProtocol(), parentUrl.getHost(), parentUrl.getPort(), parentUrl.getFile() + "/" + archiveEntry.getName());
            return FileManager.newFile(archiveFile, url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
