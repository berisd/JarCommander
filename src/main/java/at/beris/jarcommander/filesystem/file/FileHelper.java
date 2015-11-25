/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import org.apache.commons.io.FilenameUtils;

public class FileHelper {
    public static boolean isArchive(IFile file) {
        return FilenameUtils.getExtension(file.toString()).toUpperCase().equals("ZIP");
    }
}
