/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.filesystem.drive.Drive;
import at.beris.jarcommander.filesystem.drive.RemoteDrive;
import at.beris.jarcommander.model.SiteModel;
import at.beris.virtualfile.util.UrlUtils;

import java.util.ArrayList;
import java.util.List;

public class RemoteFileSystem implements FileSystem {
    private SiteModel siteModel;

    public RemoteFileSystem(SiteModel siteModel) {
        this.siteModel = siteModel;
    }

    @Override
    public List<Drive> getDriveList() {
        ArrayList<Drive> driveList = new ArrayList<>();
        RemoteDrive drive = new RemoteDrive();
        String urlString = siteModel.getProtocol().toLowerCase() + "://" + siteModel.getUsername() + ":" + String.valueOf(siteModel.getPassword()) +
                "@" + siteModel.getHostname() + ":" + String.valueOf(siteModel.getPortNumber()) + "/";
        drive.setFile(Application.getContext().getFileManager().resolveFile(UrlUtils.newUrl(urlString)));
        driveList.add(drive);
        return driveList;
    }
}
