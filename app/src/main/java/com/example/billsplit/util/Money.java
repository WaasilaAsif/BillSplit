package com.example.billsplit.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Single place for "$1,240.00"-style formatting so every screen renders
 * amounts identically instead of scattering ad hoc String.format calls.
 */
public final class Money {

    private static final DecimalFormat FORMAT =
            new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));

    private Money() {
    }

    /** e.g. 1240.5 -> "$1,240.50" */
    public static String format(double amount) {
        return "$" + FORMAT.format(Math.abs(amount));
    }

    /** e.g. 1240.5 -> "$1,240.50", -85 -> "-$85.00" (keeps the sign) */
    public static String formatSigned(double amount) {
        return (amount < 0 ? "-$" : "$") + FORMAT.format(Math.abs(amount));
    }
}
