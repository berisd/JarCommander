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
import at.beris.jarcommander.filesystem.drive.RemoteDrive;
import at.beris.virtualfile.FileManager;
import at.beris.jarcommander.model.SiteModel;
import at.beris.virtualfile.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class RemoteFileSystem implements IFileSystem {
    private SiteModel siteModel;

    public RemoteFileSystem(SiteModel siteModel) {
        this.siteModel = siteModel;
    }

    @Override
    public List<IDrive> getDriveList() {
        ArrayList<IDrive> driveList = new ArrayList<>();
        RemoteDrive drive = new RemoteDrive();
        String urlString = siteModel.getProtocol().toLowerCase() + "://" + siteModel.getUsername() + ":" + String.valueOf(siteModel.getPassword()) +
                "@" + siteModel.getHostname() + ":" + String.valueOf(siteModel.getPortNumber()) + "/";
        drive.setFile(FileManager.newFile(FileUtils.newUrl(urlString)));
        driveList.add(drive);
        return driveList;
    }
}
