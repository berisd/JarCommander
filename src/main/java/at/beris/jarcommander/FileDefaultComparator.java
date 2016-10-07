/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander;

import at.beris.virtualfile.VirtualFile;

import java.io.IOException;
import java.util.Comparator;

import static at.beris.jarcommander.Application.logException;

public class FileDefaultComparator implements Comparator<VirtualFile> {
    @Override
    public int compare(VirtualFile o1, VirtualFile o2) {
        return compareTo(o1, o2);
    }

    public int compareTo(VirtualFile file1, VirtualFile file2) {
        if (file1 == null && file2 == null) {
            return 0;
        } else if (file1 == null || file2 == null) {
            return file1 == null ? -1 : 1;
        }
        try {
            int cmp = compareTo(file1.getParent(), file2.getParent());
            if (cmp == 0) {
                if (file1.isDirectory() != file2.isDirectory()) {
                    return file1.isDirectory() ? -1 : 1;
                }
                cmp = file1.getName().toUpperCase().compareTo(file2.getName().toUpperCase());
            }
            return cmp;
        } catch (IOException e) {
            logException(e);
        }
        return -1;
    }
}
