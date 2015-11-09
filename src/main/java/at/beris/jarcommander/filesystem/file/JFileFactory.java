/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.SshContext;
import com.jcraft.jsch.ChannelSftp;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static at.beris.jarcommander.Application.logException;

public class JFileFactory {
    public static JFile newInstance(File file) {
        if (file == null)
            return null;

        JFile<File> jFile = new LocalFile(file);

        File parent = file.getParentFile();
        JFile virtualChild = jFile;
        while (parent != null) {
            JFile virtualParent = newInstance(parent);
            virtualChild.setParentFile(virtualParent);
            virtualChild = virtualParent;
            parent = parent.getParentFile();
        }
        return jFile;
    }

    public static JFile newInstance(ArchiveEntry archiveEntry, File archiveFile) {
        if (archiveEntry == null)
            return null;
        return newInstance(archiveEntry, JFileFactory.newInstance(archiveFile));
    }

    public static JFile newInstance(ArchiveEntry archiveEntry, JFile archiveFile) {
        if (archiveEntry == null)
            return null;
        return new CompressedFile(archiveEntry, archiveFile);
    }

    public static JFile newSshFileInstance(SshContext context, String path, ChannelSftp.LsEntry file) {
        if (file == null)
            return null;
        return new SshFile(context, path, file);
    }

    public static List<JFile> createListFromArchive(File archiveFile) {
        List<JFile> fileList = new ArrayList<>();
        try {

            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            InputStream fis = new BufferedInputStream(new FileInputStream(archiveFile));
            ArchiveInputStream ais = factory.createArchiveInputStream(fis);

            ArchiveEntry ae;
            Map<String, JFile> pathToArchiveEntryMap = new HashMap<>();
            Map<String, Set<JFile>> archiveEntryToChildrenMap = new HashMap<>();

            while ((ae = ais.getNextEntry()) != null) {
                JFile file = JFileFactory.newInstance(ae, archiveFile);

                String[] parts = ae.getName().split(File.separator);
                pathToArchiveEntryMap.put(parts[parts.length - 1], file);

                if (parts.length > 1) {
                    JFile currentFile = file;
                    for (int i = parts.length - 2; i >= 0; i--) {
                        JFile parentFile = pathToArchiveEntryMap.get(parts[i]);
                        currentFile.setParentFile(parentFile);
                        currentFile = parentFile;
                    }

                    for (int i = 0; i < parts.length - 1; i++) {
                        if (i + 1 < parts.length) {
                            if (archiveEntryToChildrenMap.get(parts[i]) == null) {
                                archiveEntryToChildrenMap.put(parts[i], new LinkedHashSet<JFile>());
                            }
                            archiveEntryToChildrenMap.get(parts[i]).add(pathToArchiveEntryMap.get(parts[i + 1]));
                        }
                    }

                    for (Map.Entry<String, Set<JFile>> entry : archiveEntryToChildrenMap.entrySet()) {
                        Set<JFile> children = archiveEntryToChildrenMap.get(entry.getKey());
                        pathToArchiveEntryMap.get(entry.getKey()).addFile(children);
                    }

                } else {
                    file.setParentFile(JFileFactory.newInstance(archiveFile));
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
}
