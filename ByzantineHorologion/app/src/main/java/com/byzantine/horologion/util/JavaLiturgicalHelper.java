package com.byzantine.horologion.util;

/**
 * Liturgical date helper written in Java (interoperating with the Kotlin engine).
 *
 * Computes the date of Orthodox Pascha (Easter) using Meeus's Julian algorithm,
 * then converts the resulting Julian-calendar date to the civil (Gregorian)
 * calendar via Julian Day Numbers, which is correct for any year.
 */
public final class JavaLiturgicalHelper {

    private JavaLiturgicalHelper() { }

    /** Julian Day Number for a date on the GREGORIAN (civil) calendar. */
    public static long gregorianToJdn(int y, int m, int d) {
        long a = (14 - m) / 12;
        long yy = y + 4800 - a;
        long mm = m + 12 * a - 3;
        return d + (153 * mm + 2) / 5 + 365 * yy + yy / 4 - yy / 100 + yy / 400 - 32045;
    }

    /** Julian Day Number for a date on the JULIAN (old-style) calendar. */
    public static long julianToJdn(int y, int m, int d) {
        long a = (14 - m) / 12;
        long yy = y + 4800 - a;
        long mm = m + 12 * a - 3;
        return d + (153 * mm + 2) / 5 + 365 * yy + yy / 4 - 32083;
    }

    /** Convert a JDN back to a Gregorian {year, month, day}. */
    public static int[] jdnToGregorian(long jdn) {
        long a = jdn + 32044;
        long b = (4 * a + 3) / 146097;
        long c = a - (146097 * b) / 4;
        long dd = (4 * c + 3) / 1461;
        long e = c - (1461 * dd) / 4;
        long mm = (5 * e + 2) / 153;
        int day = (int) (e - (153 * mm + 2) / 5 + 1);
        int month = (int) (mm + 3 - 12 * (mm / 10));
        int year = (int) (100 * b + dd - 4800 + mm / 10);
        return new int[] { year, month, day };
    }

    /**
     * Orthodox Pascha for the given civil year, returned as {year, month, day}
     * on the Gregorian (civil) calendar.
     */
    public static int[] orthodoxPaschaGregorian(int year) {
        int a = year % 4;
        int b = year % 7;
        int c = year % 19;
        int d = (19 * c + 15) % 30;
        int e = (2 * a + 4 * b - d + 34) % 7;
        int month = (d + e + 114) / 31;      // 3 = March, 4 = April (Julian)
        int day = ((d + e + 114) % 31) + 1;  // Julian-calendar date
        long jdn = julianToJdn(year, month, day);
        return jdnToGregorian(jdn);
    }

    /** Whole days between two Gregorian dates (b - a). */
    public static long daysBetween(int y1, int m1, int d1, int y2, int m2, int d2) {
        return gregorianToJdn(y2, m2, d2) - gregorianToJdn(y1, m1, d1);
    }
}
