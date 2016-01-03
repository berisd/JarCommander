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
import at.beris.jarcommander.filesystem.file.FileManager;
import at.beris.jarcommander.model.SiteModel;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class SshFileSystem implements IFileSystem {
    private SiteModel siteModel;

    public SshFileSystem(SiteModel siteModel) {
        this.siteModel = siteModel;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {
        throw new NotImplementedException("");
    }

    @Override
    public List<IDrive> getDriveList() {
        ArrayList<IDrive> driveList = new ArrayList<>();
        SshDrive drive = new SshDrive();
        String urlString = siteModel.getProtocol().toLowerCase() + "://" + siteModel.getUsername() + ":" + String.valueOf(siteModel.getPassword()) +
                "@" + siteModel.getHostname() + ":" + String.valueOf(siteModel.getPortNumber()) + "/";
        drive.setFile(FileManager.newFile(FileUtils.newUrl(urlString)));
        driveList.add(drive);
        return driveList;
    }
}
