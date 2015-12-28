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
import com.jcraft.jsch.Buffer;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.CustomChannelSftp;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import java.io.*;
import java.util.*;

import static at.beris.jarcommander.Application.logException;

public class FileFactory {
    public IFile newInstance(IFile parent, String child) {
        if (parent instanceof LocalFile) {
            return newInstance(new File((File) parent.getBaseObject(), child));
        }
        if (parent instanceof SshFile) {
            CustomChannelSftp c = new CustomChannelSftp();
            Buffer buf = new Buffer();
            buf.putInt(0);
            ChannelSftp.LsEntry lsEntry = c.new CustomLsEntry(child, child, c.getATTR(buf));

            return newSshFileInstance(((SshFile) parent).getContext(), parent.getAbsolutePath(), lsEntry);
        } else
            throw new RuntimeException("Can't create IFile with given parent and child.");
    }

    public IFile newInstance(File file) {
        if (file == null)
            return null;

        IFile<File> iFile = new LocalFile(file, this);

        File parent = file.getParentFile();
        IFile virtualChild = iFile;
        while (parent != null) {
            IFile virtualParent = newInstance(parent);
            virtualChild.setParentFile(virtualParent);
            virtualChild = virtualParent;
            parent = parent.getParentFile();
        }
        return iFile;
    }

    public IFile newInstance(ArchiveEntry archiveEntry, File archiveFile) {
        if (archiveEntry == null)
            return null;
        return newInstance(archiveEntry, newInstance(archiveFile));
    }

    public IFile newInstance(ArchiveEntry archiveEntry, IFile archiveFile) {
        if (archiveEntry == null)
            return null;
        return new CompressedFile(archiveEntry, archiveFile, this);
    }

    public IFile newSshFileInstance(SshContext context, String path, ChannelSftp.LsEntry file) {
        if (file == null)
            return null;
        return new SshFile(context, path, file, this);
    }

    public List<IFile> createListFromArchive(File archiveFile) {
        List<IFile> fileList = new ArrayList<>();
        try {

            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            InputStream fis = new BufferedInputStream(new FileInputStream(archiveFile));
            ArchiveInputStream ais = factory.createArchiveInputStream(fis);

            ArchiveEntry ae;
            Map<String, IFile> pathToArchiveEntryMap = new HashMap<>();
            Map<String, Set<IFile>> archiveEntryToChildrenMap = new HashMap<>();

            while ((ae = ais.getNextEntry()) != null) {
                IFile file = newInstance(ae, archiveFile);

                String[] parts = ae.getName().split(File.separator);
                pathToArchiveEntryMap.put(parts[parts.length - 1], file);

                if (parts.length > 1) {
                    IFile currentFile = file;
                    for (int i = parts.length - 2; i >= 0; i--) {
                        IFile parentFile = pathToArchiveEntryMap.get(parts[i]);
                        currentFile.setParentFile(parentFile);
                        currentFile = parentFile;
                    }

                    for (int i = 0; i < parts.length - 1; i++) {
                        if (i + 1 < parts.length) {
                            if (archiveEntryToChildrenMap.get(parts[i]) == null) {
                                archiveEntryToChildrenMap.put(parts[i], new LinkedHashSet<IFile>());
                            }
                            archiveEntryToChildrenMap.get(parts[i]).add(pathToArchiveEntryMap.get(parts[i + 1]));
                        }
                    }

                    for (Map.Entry<String, Set<IFile>> entry : archiveEntryToChildrenMap.entrySet()) {
                        Set<IFile> children = archiveEntryToChildrenMap.get(entry.getKey());
                        pathToArchiveEntryMap.get(entry.getKey()).addFile(children);
                    }

                } else {
                    file.setParentFile(newInstance(archiveFile));
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
