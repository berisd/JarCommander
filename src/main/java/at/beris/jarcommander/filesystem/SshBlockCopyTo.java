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
import at.beris.jarcommander.filesystem.file.SshFile;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;

import java.io.*;

public class SshBlockCopyTo implements IBlockCopy {
    private final static Logger LOGGER = Logger.getLogger(SshBlockCopyTo.class);

    private IFile sourceFile;

    private byte[] buf;

    private long fileSize;
    private long bytesWritten;

    private int bytesReadTotal;
    private int bytesRead;

    private OutputStream out;
    private InputStream in;
    private Channel channel;
    private FileInputStream fis;

    public SshBlockCopyTo() {
        buf = new byte[COPY_BUFFER_SIZE];
    }

    @Override
    public void init(IFile sourceFile, IFile targetFile) {
        if ((sourceFile instanceof SshFile && targetFile instanceof SshFile) ||
                (!(sourceFile instanceof SshFile) && !(targetFile instanceof SshFile))) {
            throw new NotImplementedException("Not implemented for this IFile Type");
        }

        bytesRead = 0;
        bytesReadTotal = 0;
        bytesWritten = 0;
        fileSize = 0;

        try {
            this.sourceFile = sourceFile;
            SshFile sshFile = (SshFile) targetFile;

            SshContext sshContext = sshFile.getContext();
            Session session = sshContext.getSession();

            String command = "scp -t " + sshFile.getAbsolutePath();
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            out = channel.getOutputStream();
            in = channel.getInputStream();

            channel.connect();

            if (checkAck(in) != 0) {
                System.exit(0);
            }


            fileSize = sourceFile.getSize();
            // send "C0644 filesize filename", where filename should not include '/'
            command = "C0644 " + fileSize + " ";
            command += sourceFile.getName();
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                throw new RuntimeException("Error with file transfer.");
            }

            fis = new FileInputStream(sourceFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (fis != null) {
                fis.close();
                fis = null;
            }

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            channel.disconnect();
        } catch (IOException e) {
            Application.logException(e);
        }
    }

    @Override
    public int write() {
        try {
            out.write(buf, 0, bytesRead);
            bytesWritten += bytesRead;
            return bytesRead;
        } catch (IOException e) {
            Application.logException(e);
        }
        return 0;
    }

    @Override
    public int read() {
        try {
            if (bytesReadTotal >= fileSize) {
                if (bytesReadTotal > fileSize)
                    LOGGER.warn("File sizes are not equal.");
                return -1;
            }

            long bytesToRead = 0;

            if (bytesReadTotal + buf.length < fileSize)
                bytesToRead = buf.length;
            else
                bytesToRead = fileSize - bytesReadTotal;

            if (bytesToRead == 0)
                return 0;

            bytesRead = fis.read(buf, 0, (int) bytesToRead);
            bytesReadTotal += bytesRead;
            if (bytesToRead < 0) {
                LOGGER.error("bytes read < 0 ");
                return 0;
            }

            return bytesRead;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int positionBuffer() {
        return 0;
    }

    @Override
    public long size() {
        return fileSize;
    }

    @Override
    public long bytesWritten() {
        return bytesWritten;
    }

    private static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');

            throw new RuntimeException(sb.toString());
        }
        return b;
    }
}
