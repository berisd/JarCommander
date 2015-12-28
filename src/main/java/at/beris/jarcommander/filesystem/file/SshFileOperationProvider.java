/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import at.beris.jarcommander.filesystem.SshBlockCopy;
import at.beris.jarcommander.filesystem.SshContext;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;

public class SshFileOperationProvider implements IFileOperationProvider<SshFile> {
    private SshBlockCopy sshBlockCopy;

    public SshFileOperationProvider() {
        sshBlockCopy = new SshBlockCopy();
    }

    @Override
    public boolean exists(SshFile sshFile) {
        return false;
    }

    @Override
    public boolean mkdirs(SshFile sshFile) {
        return false;
    }

    @Override
    public void delete(SshFile sshFile) {
        try {
            SshContext sshContext = sshFile.getContext();
            ChannelSftp channelSftp = (ChannelSftp) sshContext.getChannel();
            channelSftp.rm(sshFile.getAbsolutePath());
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void copy(SshFile sourceFile, IFile targetFile, CopyListener listener) throws IOException {
        if (targetFile instanceof LocalFile) {
            copyToLocalHost(sourceFile, targetFile, listener);
        } else {
            throw new NotImplementedException("File operation not implemented for this target filetype.");
        }
    }

    public void copyToLocalHost(IFile sourceFile, IFile targetFile, CopyListener listener) throws IOException {
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
}
