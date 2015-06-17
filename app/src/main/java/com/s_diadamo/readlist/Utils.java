package com.s_diadamo.readlist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.CANADA);
        return simpleDateFormat.format(cal.getTime());
    }
}
