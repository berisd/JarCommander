/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.file.LocalFile;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;

public class LocalBlockCopy implements IBlockCopy {

    private FileChannel bis;
    private FileChannel bos;

    private ByteBuffer buffer;

    private long size;
    private long bytesWrittenTotal;
    private long bytesWritten;

    public LocalBlockCopy() {
        buffer = ByteBuffer.allocate(COPY_BUFFER_SIZE);
    }

    @Override
    public void init(IFile sourceFile, IFile targetFile) {
        if (!(sourceFile instanceof LocalFile) || !(targetFile instanceof LocalFile)) {
            throw new NotImplementedException("Not implemented for this IFile Type");
        }

        try {
            bis = new FileInputStream((File) sourceFile.getBaseObject()).getChannel();
            bos = new FileOutputStream((File) targetFile.getBaseObject()).getChannel();
            size = bis.size();
            bytesWrittenTotal = 0;
            bytesWritten = 0;
            buffer.clear();
        } catch (FileNotFoundException e) {
            Application.logException(e);
        } catch (ClosedByInterruptException e) {
            targetFile.delete();
        } catch (IOException e) {
            Application.logException(e);
        }
    }

    @Override
    public void close() {
        try {
            bis.close();
            bos.close();
        } catch (IOException e) {
            Application.logException(e);
        }
    }

    @Override
    public void copy() {
        try {
            buffer.flip();
            bytesWritten = bos.write(buffer);
            buffer.compact();
            bytesWrittenTotal += bytesWritten;
        } catch (IOException e) {
            Application.logException(e);
        }
    }

    @Override
    public int read() {
        try {
            return bis.read(buffer);
        } catch (ClosedChannelException e) {
            return 0;
        } catch (IOException e) {
            Application.logException(e);
        }
        return 0;
    }

    @Override
    public int positionBuffer() {
        return buffer.position();
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public long bytesWrittenTotal() {
        return bytesWrittenTotal;
    }

    @Override
    public long bytesWritten() {
        return bytesWritten;
    }
}
