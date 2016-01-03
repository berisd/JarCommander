/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file.provider;

import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.model.FileModel;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class LocalArchiveOperationProviderTest {
    @Test
    public void testList() throws Exception {
        File file = new File("src/test/resources/testarchive.zip");

        FileModel fileModel = new FileModel();
        fileModel.setUrl(file.toURI().toURL());
        LocalArchiveOperationProvider provider = new LocalArchiveOperationProvider();
        List<IFile> fileList = provider.list(null, fileModel);
        Assert.assertTrue(fileList.size()>0);
    }
}