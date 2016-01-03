/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.exception;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(Throwable cause) {
        super(cause);
    }
}
