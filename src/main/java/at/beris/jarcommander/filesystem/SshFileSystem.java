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
import at.beris.jarcommander.filesystem.drive.SshDrive;
import at.beris.jarcommander.filesystem.path.JPath;
import at.beris.jarcommander.filesystem.path.SshPath;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SshFileSystem implements JFileSystem {
    private SshContext context;

    public SshFileSystem(SshContext context) {
        this.context = context;
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
    public List<JDrive> getDriveList() {
        ArrayList<JDrive> driveList = new ArrayList<>();
        SshDrive drive = new SshDrive();
        drive.setPath(new SshPath(context, "/"));
        driveList.add(drive);
        return driveList;
    }

    @Override
    public void delete(JPath jPath) throws IOException {
        throw new NotImplementedException("");
    }
}
