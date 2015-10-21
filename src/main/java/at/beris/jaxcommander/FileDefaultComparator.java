/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander;

import at.beris.jaxcommander.filesystem.file.VirtualFile;

import java.util.Comparator;

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
        int cmp = compareTo(file1.getParentFile(), file2.getParentFile());
        if (cmp == 0) {
            if (file1.isDirectory() != file2.isDirectory()) {
                return file1.isDirectory() ? -1 : 1;
            }
            cmp = file1.getName().toUpperCase().compareTo(file2.getName().toUpperCase());
        }
        return cmp;
    }
}
