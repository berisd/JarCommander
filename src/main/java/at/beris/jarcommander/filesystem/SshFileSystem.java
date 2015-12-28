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
import at.beris.jarcommander.filesystem.drive.SshDrive;
import at.beris.jarcommander.filesystem.file.FileFactory;
import at.beris.jarcommander.filesystem.path.SshPath;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class SshFileSystem implements IFileSystem {
    private SshContext context;
    private FileFactory fileFactory;

    public SshFileSystem(SshContext context, FileFactory fileFactory) {
        this.context = context;
        this.fileFactory = fileFactory;
        context.init();
    }

    @Override
    public void open() {
        context.connect();
    }

    @Override
    public void close() {
        throw new NotImplementedException("");
    }

    @Override
    public List<IDrive> getDriveList() {
        ArrayList<IDrive> driveList = new ArrayList<>();
        SshDrive drive = new SshDrive();
        drive.setPath(new SshPath(context, "/", fileFactory));
        driveList.add(drive);
        return driveList;
    }
}
