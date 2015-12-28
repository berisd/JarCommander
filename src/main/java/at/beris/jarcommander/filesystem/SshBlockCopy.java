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
import org.apache.log4j.Logger;

import java.io.*;

public class SshBlockCopy implements IBlockCopy {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(SshBlockCopy.class);

    private static int SUCCESS = 0;
    private static int ERROR = 1;
    private static int FATAL_ERROR = 2;
    private static int UNKNOWN_ERROR = -1;

    private enum TransferMode {FROM_REMOTE, TO_REMOTE}

    private IFile sourceFile;
    private IFile targetFile;

    private byte[] buf;

    private long fileSize;
    private long bytesWritten;

    private int bytesReadTotal;
    private int bytesRead;

    private OutputStream out;
    private InputStream in;
    private Channel channel;
    private FileOutputStream fos;
    private FileInputStream fis;

    private TransferMode transferMode;


    public SshBlockCopy() {
        buf = new byte[COPY_BUFFER_SIZE];
    }

    @Override
    public void init(IFile sourceFile, IFile targetFile) {
        if ((sourceFile instanceof SshFile && targetFile instanceof SshFile) ||
                (!(sourceFile instanceof SshFile) && !(targetFile instanceof SshFile))) {
            throw new IllegalArgumentException("BlockCopy from " + sourceFile.getClass() + " to " + targetFile.getClass() + " not possible.");
        }

        this.sourceFile = sourceFile;
        this.targetFile = targetFile;

        bytesRead = 0;
        bytesReadTotal = 0;
        bytesWritten = 0;
        fileSize = 0;
        Session session;
        String command;
        try {
            if (sourceFile instanceof SshFile) {
                transferMode = TransferMode.FROM_REMOTE;
                session = ((SshFile) sourceFile).getContext().getSession();
                command = "scp -f " + sourceFile.getAbsolutePath();
            } else {
                transferMode = TransferMode.TO_REMOTE;
                session = ((SshFile) targetFile).getContext().getSession();
                command = "scp -t " + targetFile.getAbsolutePath();
            }

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            out = channel.getOutputStream();
            in = channel.getInputStream();

            channel.connect();

            if (transferMode == TransferMode.FROM_REMOTE)
                initFromRemote();
            else
                initToRemote();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    private void initToRemote() throws IOException {
        if (checkAck(in) != SUCCESS) {
            throw new RuntimeException("Error with file transfer.");
        }

        fileSize = sourceFile.getSize();
        // send "C0644 filesize filename", where filename should not include '/'
        String command = "C0644 " + fileSize + " ";
        command += sourceFile.getName();
        command += "\n";
        out.write(command.getBytes());
        out.flush();
        if (checkAck(in) != SUCCESS) {
            throw new RuntimeException("Error with file transfer.");
        }

        fis = new FileInputStream(sourceFile.getAbsolutePath());
    }

    private void initFromRemote() throws IOException {
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        int c = checkAck(in);
        if (c != 'C') {
            throw new RuntimeException("Ssh checkAck!=C");
        }

        // read '0644 '
        in.read(buf, 0, 5);

        fileSize = 0L;
        while (true) {
            if (in.read(buf, 0, 1) < 0) {
                throw new RuntimeException("Ssh error while reading fileSize");
            }
            if (buf[0] == ' ') break;
            fileSize = fileSize * 10L + (long) (buf[0] - '0');
        }

        String file;
        for (int i = 0; ; i++) {
            in.read(buf, i, 1);
            if (buf[i] == (byte) 0x0a) {
                file = new String(buf, 0, i);
                break;
            }
        }

        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        fos = new FileOutputStream(targetFile.getAbsolutePath());
    }

    @Override
    public void close() {
        try {
            if (fos != null) {
                fos.close();
                fos = null;
            }

            if (fis != null) {
                fis.close();
                fis = null;
            }

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
            if (transferMode == TransferMode.FROM_REMOTE)
                fos.write(buf, 0, bytesRead);
            else
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

            long bytesToRead;

            if (bytesReadTotal + buf.length < fileSize)
                bytesToRead = buf.length;
            else
                bytesToRead = fileSize - bytesReadTotal;

            if (bytesToRead == 0)
                return 0;

            if (transferMode == TransferMode.FROM_REMOTE)
                bytesRead = in.read(buf, 0, (int) bytesToRead);
            else
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

        if (b == SUCCESS || b == UNKNOWN_ERROR)
            return b;

        if (b == ERROR || b == FATAL_ERROR) {
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
