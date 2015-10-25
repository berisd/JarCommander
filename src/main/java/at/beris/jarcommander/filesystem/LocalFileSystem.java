/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.filesystem.drive.JDrive;
import at.beris.jarcommander.filesystem.drive.LocalDrive;
import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.filesystem.path.LocalPath;

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

public class LocalFileSystem implements JFileSystem {
    private List<JDrive> driveList;

    public LocalFileSystem() {
        driveList = createDriveList();
    }

    public List<JDrive> createDriveList() {
        List<JDrive> driveList = new ArrayList<>();
        if (driveList.size() > 0)
            return driveList;

        try {
            for (FileStore fileStore : FileSystems.getDefault().getFileStores()) {
//                long total = fileStore.getTotalSpace() / 1024;
//                long used = (fileStore.getTotalSpace() - fileStore.getUnallocatedSpace()) / 1024;
//                long avail = fileStore.getUsableSpace() / 1024;
//                System.out.format("%-20s %12d %12d %12d%n", fileStore, total, used, avail);

                String[] parts = fileStore.toString().split(" ");
                Path path = new File(parts[0]).toPath();

                LocalDrive driveInfo = new LocalDrive();
                driveInfo.setPath(new LocalPath(path));
                driveInfo.setSpaceTotal(fileStore.getTotalSpace());
                driveInfo.setSpaceLeft(fileStore.getUsableSpace());

                driveList.add(driveInfo);
            }
        } catch (IOException e) {
            logException(e);
        }

        driveList.sort(new Comparator<JDrive>() {
            @Override
            public int compare(JDrive o1, JDrive o2) {
                return o1.getPath().compareTo(o2.getPath());
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
    public List<JDrive> getDriveList() {
        return driveList;
    }

    @Override
    public void delete(JPath jPath) throws IOException {
        Path path = (Path) jPath.getBaseObject();
        Files.delete(path);
    }
}
