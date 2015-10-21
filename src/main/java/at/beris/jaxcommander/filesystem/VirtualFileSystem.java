/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem;

import at.beris.jaxcommander.filesystem.path.VirtualPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static at.beris.jaxcommander.Application.logException;

public class VirtualFileSystem {
    private List<VirtualDrive> driveList;

    public VirtualFileSystem() {
        driveList = createDriveList();

    }

    public List<VirtualDrive> createDriveList() {
        List<VirtualDrive> driveList = new ArrayList<VirtualDrive>();
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

                VirtualDrive driveInfo = new VirtualDrive();
                driveInfo.setPath(path);
                driveInfo.setSpaceTotal(fileStore.getTotalSpace());
                driveInfo.setSpaceLeft(fileStore.getUsableSpace());

                driveList.add(driveInfo);
            }
        } catch (IOException e) {
            logException(e);
        }

        driveList.sort(new Comparator<VirtualDrive>() {
            @Override
            public int compare(VirtualDrive o1, VirtualDrive o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });

        return driveList;
    }

    public List<VirtualDrive> getDriveList() {
        return driveList;
    }

    public static VirtualPath getPath(String path) {
        return new VirtualPath(Paths.get(path));
    }
}
