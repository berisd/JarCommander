/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.Application;
import at.beris.jarcommander.filesystem.SshBlockCopy;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static at.beris.jarcommander.filesystem.IBlockCopy.COPY_BUFFER_SIZE;

public class LocalFileOperationProvider implements IFileOperationProvider<File> {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(LocalFileOperationProvider.class);

    private SshBlockCopy sshBlockCopy;
    private FileFactory fileFactory;

    private long filesProcessed;

    public LocalFileOperationProvider(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
        sshBlockCopy = new SshBlockCopy();
    }

    @Override
    public boolean exists(File file) {
        return file.exists();
    }

    @Override
    public boolean mkdirs(File file) {
        return file.mkdirs();
    }

    @Override
    public void delete(File file) {
        try {
            if (file.exists()) {
                Files.walkFileTree(file.toPath(), new LocalFileDeletingVisitor());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void copy(File sourceFile, IFile targetFile, CopyListener listener) throws IOException {
        LOGGER.debug("copy " + sourceFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
        filesProcessed = 0L;
        copyRecursive(sourceFile, targetFile, listener);
    }

    private void copyRecursive(File sourceFile, IFile targetFile, CopyListener listener) throws IOException {
        if (sourceFile.getName().equals(".."))
            return;

        if (!(targetFile instanceof LocalFile) && !(targetFile instanceof SshFile)) {
            throw new NotImplementedException("File operation not implemented for this target filetype.");
        }

        if (targetFile.exists()) {
            listener.fileExists(targetFile);
        }

        if (sourceFile.isDirectory()) {
            if (!targetFile.exists())
                targetFile.mkdirs();

            IFile targetParentFile = targetFile.getParentFile();
            if (targetParentFile != null) {
                targetParentFile.mkdirs();
            }

            for (File sourceChildFile : sourceFile.listFiles()) {
                if (sourceChildFile.toString().equals(".."))
                    continue;
                IFile destFile = fileFactory.newInstance(new File((File) targetFile.getBaseObject(), sourceChildFile.getName()));
                copyRecursive(sourceChildFile, destFile, listener);
            }
        } else {

            listener.startCopyFile(sourceFile.getAbsolutePath(), filesProcessed + 1);
            if (targetFile instanceof LocalFile) {
                copyLocal(sourceFile, (LocalFile) targetFile, listener);
            } else if (targetFile instanceof SshFile) {
                copyToRemoteSiteWithSftp(fileFactory.newInstance(sourceFile), targetFile, listener);
            }
            filesProcessed++;
        }
        targetFile.refresh();
    }

    public void copyLocal(File sourceFile, LocalFile targetFile, CopyListener listener) throws IOException {
        FileChannel bis = null;
        FileChannel bos = null;
        ByteBuffer buffer = ByteBuffer.allocate(COPY_BUFFER_SIZE);

        long bytesWrittenTotal;
        long bytesWrittenBlock;

        try {
            bis = new FileInputStream(sourceFile).getChannel();
            bos = new FileOutputStream(targetFile.getBaseObject()).getChannel();
            bytesWrittenTotal = 0;
            buffer.clear();

            while (bis.read(buffer) >= 0 || buffer.position() != 0) {
                buffer.flip();
                bytesWrittenBlock = bos.write(buffer);
                bytesWrittenTotal += bytesWrittenBlock;
                buffer.compact();
                listener.afterBlockCopied(sourceFile.length(), bytesWrittenBlock, bytesWrittenTotal);
                if (listener.interrupt())
                    break;
            }
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
    }

    public void copyToRemoteSiteWithSftp(IFile sourceFile, IFile targetFile, CopyListener listener) throws IOException {
        long bytesWrittenTotal = 0;
        long bytesWrittenBlock = 0;

        sshBlockCopy.init(sourceFile, targetFile);

        while (sshBlockCopy.read() >= 0 || sshBlockCopy.positionBuffer() != 0) {
            bytesWrittenBlock = sshBlockCopy.write();
            bytesWrittenTotal += bytesWrittenBlock;
            listener.afterBlockCopied(sourceFile.getSize(), bytesWrittenBlock, bytesWrittenTotal);
            if (listener.interrupt())
                break;
        }
        sshBlockCopy.close();
    }

    private class LocalFileDeletingVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                throws IOException {
            if (attributes.isRegularFile()) {
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path directory, IOException ioe)
                throws IOException {
            Files.delete(directory);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException ioe)
                throws IOException {
            Application.logException(ioe);
            return FileVisitResult.CONTINUE;
        }
    }
}
