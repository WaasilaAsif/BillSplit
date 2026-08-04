package com.example.billsplit.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public final class Money {

    private static final DecimalFormat FORMAT =
            new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));

    private Money() {
    }

    /** e.g. 1240.5 -> "Rs1,240.50" */
    public static String format(double amount) {
        return "Rs" + FORMAT.format(Math.abs(amount));
    }

    /** e.g. 1240.5 -> "Rs,240.50", -85 -> "-Rs85.00" (keeps the sign) */
    public static String formatSigned(double amount) {
        return (amount < 0 ? "-Rs" : "Rs") + FORMAT.format(Math.abs(amount));
    }
}
