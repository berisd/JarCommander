/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.filesystem.drive.IDrive;
import at.beris.jarcommander.filesystem.drive.LocalDrive;
import at.beris.jarcommander.filesystem.file.FileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static at.beris.jarcommander.Application.logException;

public class LocalFileSystem implements IFileSystem {
    private List<IDrive> driveList;

    public LocalFileSystem() {
        driveList = createDriveList();
    }

    public List<IDrive> createDriveList() {
        List<IDrive> driveList = new ArrayList<>();
        if (driveList.size() > 0)
            return driveList;

        boolean isOsWindows = System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;
        try {
            if (isOsWindows) {
                for (Path path : FileSystems.getDefault().getRootDirectories()) {
                    FileStore fileStore = Files.getFileStore(path);
                    LocalDrive driveInfo = new LocalDrive();
                    driveInfo.setFile(FileManager.newFile(path.toUri().toURL()));
                    driveInfo.setSpaceTotal(fileStore.getTotalSpace());
                    driveInfo.setSpaceLeft(fileStore.getUsableSpace());
                    driveList.add(driveInfo);
                }
            } else {
                for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
                    String[] parts = fileStore.toString().split(" ");
                    Path path = new File(parts[0]).toPath();

                    LocalDrive driveInfo = new LocalDrive();
                    driveInfo.setFile(FileManager.newFile(path.toUri().toURL()));
                    driveInfo.setSpaceTotal(fileStore.getTotalSpace());
                    driveInfo.setSpaceLeft(fileStore.getUsableSpace());
                    driveList.add(driveInfo);
                }
            }
        } catch (IOException e) {
            logException(e);
        }

        driveList.sort(new Comparator<IDrive>() {
            @Override
            public int compare(IDrive o1, IDrive o2) {
                return o1.getFile().toString().compareTo(o2.getFile().toString());
            }
        });

        return driveList;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    @Override
    public List<IDrive> getDriveList() {
        return driveList;
    }
}
