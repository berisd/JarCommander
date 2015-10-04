/*
 * This file is part of JaxCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jaxcommander.helper;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class Localization {
    private static DateFormat dateFormat;
    private static NumberFormat numberFormat;

    public static DateFormat dateFormat() {
        if (dateFormat == null)
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
        return dateFormat;
    }

    public static NumberFormat numberFormat() {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getInstance(Locale.getDefault());
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        }
        return numberFormat;
    }
}
