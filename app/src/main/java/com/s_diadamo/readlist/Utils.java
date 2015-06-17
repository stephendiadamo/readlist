package com.s_diadamo.readlist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.CANADA);
        return simpleDateFormat.format(cal.getTime());
    }

    public static String getCurrentYear() {
        String stringDate = Utils.getCurrentDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        Date date = null;
        try {
            date = simpleDateFormat.parse(stringDate);
        } catch (ParseException e) {
            return "";
        }
        return (String) android.text.format.DateFormat.format("yyyy", date);
    }

    public static String getCurrentMonth() {
        String stringDate = Utils.getCurrentDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        Date date = null;
        try {
            date = simpleDateFormat.parse(stringDate);
        } catch (ParseException e) {
            return "";
        }
        return (String) android.text.format.DateFormat.format("MM", date);
    }
}
