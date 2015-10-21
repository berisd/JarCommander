/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.filesystem.file;

import at.beris.jaxcommander.Application;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VirtualFile<T> {

    private Provider<T> provider;
    private VirtualFile<T> parentFile;

    public VirtualFile(Provider<T> provider) {
        this.provider = provider;
    }

    public T getBaseObject() {
        return provider.getBaseObject();
    }

    public String getName() {
        return provider.getName();
    }

    public Date getLastModified() {
        return provider.getLastModified();
    }

    public long getSize() {
        return provider.getSize();
    }

    public boolean isDirectory() {
        return provider.isDirectory();
    }

    public void setParentFile(VirtualFile parentFile) {
        this.parentFile = parentFile;
    }

    public VirtualFile getParentFile() {
        return parentFile;
    }

    public Path toPath() {
        return provider.toPath();
    }

    public VirtualFile[] listFiles() {
        List<VirtualFile> fileList = new ArrayList();

        for (T file : provider.listFiles()) {
            try {
                Constructor<? extends Provider> constructor = provider.getClass().getConstructor(provider.getBaseObject().getClass());
                fileList.add(new VirtualFile(constructor.newInstance(file)));
            } catch (NoSuchMethodException e) {
                Application.logException(e);
            } catch (InvocationTargetException e) {
                Application.logException(e);
            } catch (InstantiationException e) {
                Application.logException(e);
            } catch (IllegalAccessException e) {
                Application.logException(e);
            }

        }
        return (VirtualFile[]) fileList.toArray();
    }
}
