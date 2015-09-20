/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Path;

public class SimpleFileStore {
    private FileStore fileStore;
    private Path path;

    public SimpleFileStore(FileStore fileStore) {
        this.fileStore = fileStore;
        String[] parts = fileStore.toString().split(" ");
        path = new File(parts[0]).toPath();
    }

    public Path getPath() {
        return path;
    }
}
