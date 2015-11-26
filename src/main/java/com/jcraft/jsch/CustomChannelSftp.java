/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package com.jcraft.jsch;

public class CustomChannelSftp extends ChannelSftp {
    public SftpATTRS getATTR(Buffer buf) {
        return SftpATTRS.getATTR(buf);
    }

    public class CustomLsEntry extends ChannelSftp.LsEntry {
        private String filename;
        private String longname;
        private SftpATTRS attrs;

        public CustomLsEntry(String filename, String longname, SftpATTRS attrs) {
            super(filename, longname, attrs);

            this.filename = filename;
            this.longname = longname;
            this.attrs = attrs;
        }

        public String getFilename() {
            return this.filename;
        }

        void setFilename(String filename) {
            this.filename = filename;
        }

        public String getLongname() {
            return this.longname;
        }

        void setLongname(String longname) {
            this.longname = longname;
        }

        public SftpATTRS getAttrs() {
            return this.attrs;
        }

        void setAttrs(SftpATTRS attrs) {
            this.attrs = attrs;
        }

        public String toString() {
            return this.longname;
        }

        public int compareTo(Object o) throws ClassCastException {
            if (o instanceof ChannelSftp.LsEntry) {
                return this.filename.compareTo(((ChannelSftp.LsEntry) o).getFilename());
            } else {
                throw new ClassCastException("a decendent of LsEntry must be given.");
            }
        }

    }
}
