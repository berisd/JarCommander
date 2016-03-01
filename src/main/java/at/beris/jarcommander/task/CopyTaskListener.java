/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.task;

import at.beris.virtualfile.File;

public interface CopyTaskListener {
    void setCurrentProgressBar(int progress);

    void setAllProgressBar(int progress);

    void done();

    void startCopyFile(String fileName, long currentFileNumber, long totalCountFiles);

    int fileExists(File file);

    int showError(String message);
}
