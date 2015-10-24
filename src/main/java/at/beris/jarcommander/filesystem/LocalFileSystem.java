/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.filesystem.path.JPath;

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
    private List<LocalDrive> driveList;

    public LocalFileSystem() {
        driveList = createDriveList();
    }

    public List<LocalDrive> createDriveList() {
        List<LocalDrive> driveList = new ArrayList<LocalDrive>();
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
                driveInfo.setPath(path);
                driveInfo.setSpaceTotal(fileStore.getTotalSpace());
                driveInfo.setSpaceLeft(fileStore.getUsableSpace());

                driveList.add(driveInfo);
            }
        } catch (IOException e) {
            logException(e);
        }

        driveList.sort(new Comparator<LocalDrive>() {
            @Override
            public int compare(LocalDrive o1, LocalDrive o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });

        return driveList;
    }

    @Override
    public List<LocalDrive> getDriveList() {
        return driveList;
    }

    @Override
    public void delete(JPath jPath) throws IOException {
        Path path = (Path) jPath.getBaseObject();
        Files.delete(path);
    }
}
