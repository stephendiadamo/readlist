package com.s_diadamo.readlist.general;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.BookFragment;
import com.s_diadamo.readlist.settings.SettingsFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CLEAN_DATE_FORMAT = "dd-MM-yyyy";
    public static final String USER_NAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public final static String REMEMBER_ME = "REMEMBER_ME";
    public final static String LOGGED_IN = "LOGGED_IN";
    public final static String EMAIL_ADDRESS = "EMAIL_ADDRESS";
    public final static String STORAGE_FILE_START = "/storage";
    public static final String CHECK_INTERNET_MESSAGE = "Please ensure internet connection is available";

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.CANADA);
        return simpleDateFormat.format(cal.getTime());
    }

    public static String getCurrentYear() {
        String stringDate = Utils.getCurrentDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        Date date;
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
        Date date;
        try {
            date = simpleDateFormat.parse(stringDate);
        } catch (ParseException e) {
            return "";
        }
        return (String) android.text.format.DateFormat.format("MM", date);
    }

    public static String parseDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.DATE_FORMAT, Locale.CANADA);
        return simpleDateFormat.format(date);
    }

    public static String cleanDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utils.CLEAN_DATE_FORMAT, Locale.CANADA);
        return simpleDateFormat.format(date);
    }

    public static Date getDateFromString(String date) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.CANADA);
            return df.parse(date);
        } catch (ParseException pe) {
            return null;
        }
    }

    public static void setDynamicHeight(ListView mListView) {
        ListAdapter mListAdapter = mListView.getAdapter();
        if (mListAdapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < mListAdapter.getCount(); i++) {
            View listItem = mListAdapter.getView(i, null, mListView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }

    public static void hideKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText() && activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void launchBookFragment(FragmentManager fragmentManager) {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        Fragment fragment = new BookFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public static void launchSettingsFragment(FragmentManager fragmentManager) {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        Fragment fragment = new SettingsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public static boolean checkUserIsLoggedIn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(LOGGED_IN, false);
    }

    public static boolean checkRememberMe(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(Utils.REMEMBER_ME, false);
    }

    public static void logout(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LOGGED_IN, false);
        editor.apply();
        ParseUser.logOutInBackground();
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String getUserName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString(Utils.USER_NAME, "");
        if (userName == null || userName.isEmpty()) {
            return "";
        }
        return userName;
    }

    public static void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show();
    }

    public static int calculateAverageWithQuery(Cursor cursor) {
        int updatesInWeek = 0;
        int numWeeks = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                updatesInWeek += cursor.getInt(1);
                numWeeks++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (numWeeks != 0) {
            return updatesInWeek / numWeeks;
        }
        return 0;
    }

    public static String formatTimeSpentReading(int seconds) {
        int minutes = seconds / 60;
        int hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        return "" + String.format("%02d", hours)
                + ":" + String.format("%02d", minutes)
                + ":" + String.format("%02d", seconds);
    }
}
