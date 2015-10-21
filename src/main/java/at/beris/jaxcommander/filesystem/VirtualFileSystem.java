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

import java.io.IOException;
import java.util.List;

public class VirtualFileSystem {

    private FileSystemProvider provider;

    public VirtualFileSystem(FileSystemProvider provider) {
        this.provider = provider;
    }

    public List<VirtualDrive> getDriveList() {
        return provider.getDriveList();
    }

    public void delete(VirtualPath virtualPath) throws IOException {
        provider.delete(virtualPath);
    }
}
