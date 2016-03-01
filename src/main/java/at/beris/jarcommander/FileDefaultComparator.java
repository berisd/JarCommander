/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander;

import at.beris.virtualfile.File;

import java.util.Comparator;

public class FileDefaultComparator implements Comparator<File> {
    @Override
    public int compare(File o1, File o2) {
        return compareTo(o1, o2);
    }

    public int compareTo(File file1, File file2) {
        if (file1 == null && file2 == null) {
            return 0;
        } else if (file1 == null || file2 == null) {
            return file1 == null ? -1 : 1;
        }
        int cmp = compareTo(file1.getParent(), file2.getParent());
        if (cmp == 0) {
            if (file1.isDirectory() != file2.isDirectory()) {
                return file1.isDirectory() ? -1 : 1;
            }
            cmp = file1.getName().toUpperCase().compareTo(file2.getName().toUpperCase());
        }
        return cmp;
    }
}
