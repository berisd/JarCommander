/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander;

import org.apache.log4j.Logger;

import javax.swing.SwingUtilities;

public class Application {
    private final static Logger LOGGER = Logger.getLogger(Application.class);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
                                       @Override
                                       public void run() {
                                           new ApplicationContext().getApplicationFrame().setVisible(true);
                                       }
                                   }
        );
    }

    public static void logException(Throwable throwable) {
        StringBuilder sb = new StringBuilder(throwable.getClass().getName());
        sb.append(": " + throwable.getMessage());

        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(System.lineSeparator());
            sb.append(element.toString());
        }

        LOGGER.warn(sb.toString());
    }
}
