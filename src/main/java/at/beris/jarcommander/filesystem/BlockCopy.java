/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem;

import at.beris.jarcommander.filesystem.file.JFile;
import at.beris.jarcommander.filesystem.file.LocalFile;
import org.apache.commons.lang3.NotImplementedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BlockCopy {
    public final static int COPY_BUFFER_SIZE = 1024 * 16;

    private FileChannel bis;
    private FileChannel bos;

    private ByteBuffer buffer;

    private long size;
    private long bytesWrittenTotal;
    private long bytesWritten;

    public BlockCopy() {
        buffer = ByteBuffer.allocate(COPY_BUFFER_SIZE);
    }

    public void init(JFile sourceFile, JFile targetFile) throws IOException {
        if (!(sourceFile instanceof LocalFile) || !(targetFile instanceof LocalFile)) {
            throw new NotImplementedException("Not implemented for this JFile Type");
        }

        bis = new FileInputStream((File) sourceFile.getBaseObject()).getChannel();
        bos = new FileOutputStream((File) targetFile.getBaseObject()).getChannel();
        size = bis.size();
        bytesWrittenTotal = 0;
        bytesWritten = 0;
        buffer.clear();
    }

    public void close() throws IOException {
        bis.close();
        bos.close();
    }

    public void copy() throws IOException {
        buffer.flip();
        bytesWritten = bos.write(buffer);
        buffer.compact();
        bytesWrittenTotal += bytesWritten;
    }

    public int read() throws IOException {
        return bis.read(buffer);
    }

    public int positionBuffer() {
        return buffer.position();
    }

    public long size() {
        return size;
    }

    public long bytesWrittenTotal() {
        return bytesWrittenTotal;
    }

    public long bytesWritten() {
        return bytesWritten;
    }
}
