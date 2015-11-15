/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.task;

import at.beris.jarcommander.filesystem.file.JFile;

public interface CopyTaskListener {
    void setCurrentProgressBar(int progress);

    void setAllProgressBar(int progress);

    void done();

    void startCopyFile(JFile sourceFile, long currentFileNumber, long totalCountFiles);

    void fileExists(JFile file);
}
