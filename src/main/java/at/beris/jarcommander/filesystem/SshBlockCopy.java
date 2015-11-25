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
import at.beris.jarcommander.exception.ApplicationException;
import at.beris.jarcommander.filesystem.file.IFile;
import at.beris.jarcommander.filesystem.file.LocalFile;
import at.beris.jarcommander.filesystem.file.SshFile;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;

import java.io.*;

public class SshBlockCopy implements IBlockCopy {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(SshBlockCopy.class);

    private IFile targetFile;

    private byte[] buf;

    private long fileSize;
    private long bytesWrittenTotal;
    private long bytesWritten;

    private int bytesReadTotal;
    private int bytesRead;

    private OutputStream out;
    private InputStream in;
    private Channel channel;
    private FileOutputStream fos;

    public SshBlockCopy() {
        buf = new byte[COPY_BUFFER_SIZE];
    }

    @Override
    public void init(IFile sourceFile, IFile targetFile) {
        if (!(sourceFile instanceof SshFile) || !(targetFile instanceof LocalFile)) {
            throw new NotImplementedException("Not implemented for this IFile Type");
        }

        bytesRead = 0;
        bytesReadTotal = 0;
        bytesWrittenTotal = 0;
        bytesWritten = 0;
        fileSize = 0;

        try {
            this.targetFile = targetFile;
            SshFile sshFile = (SshFile) sourceFile;

            SshContext sshContext = sshFile.getContext();
            Session session = sshContext.getSession();

            String command = "scp -f " + sshFile.getAbsolutePath();
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            out = channel.getOutputStream();
            in = channel.getInputStream();

            channel.connect();

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            int c = checkAck(in);
            if (c != 'C') {
                throw new ApplicationException(new RuntimeException("Ssh checkAck!=C"));
            }

            // read '0644 '
            in.read(buf, 0, 5);

            fileSize = 0L;
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    throw new ApplicationException(new RuntimeException("Ssh error while reading fileSize"));
                }
                if (buf[0] == ' ') break;
                fileSize = fileSize * 10L + (long) (buf[0] - '0');
            }

            String file = null;
            for (int i = 0; ; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }

            LOGGER.info("fileSize=" + fileSize + ", file=" + file);

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            // read a content of lfile
            fos = new FileOutputStream(targetFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            throw new ApplicationException(e);
        } catch (IOException e) {
            throw new ApplicationException(e);
        } catch (JSchException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public void close() {
        try {
            fos.close();
            fos = null;

//            if (checkAck(in) != 0) {
//                System.exit(0);
//            }

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
    public void copy() {
        try {
            fos.write(buf, 0, bytesRead);
            bytesWritten += bytesRead;
            bytesWrittenTotal += bytesWritten;

        } catch (IOException e) {
            Application.logException(e);
        }
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

//            if (fileSize < buf.length)
//                bytesToRead = fileSize;

            if (bytesToRead == 0)
                return 0;

            bytesRead = in.read(buf, 0, (int) bytesToRead);
            bytesReadTotal += bytesRead;
            if (bytesToRead < 0) {
                LOGGER.error("bytes read < 0 ");
                return 0;
            }

            return bytesRead;
        } catch (IOException e) {
            throw new ApplicationException(e);
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
    public long bytesWrittenTotal() {
        return bytesWrittenTotal;
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
            if (b == 1) { // error
                LOGGER.error(sb.toString());
            }
            if (b == 2) { // fatal error
                LOGGER.error(sb.toString());
            }
        }
        return b;
    }
}
