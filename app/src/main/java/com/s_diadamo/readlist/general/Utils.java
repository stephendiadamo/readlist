package com.s_diadamo.readlist.general;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.parse.ParseUser;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.BookFragment;

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

    public static boolean checkUserIsLoggedIn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = prefs.getString(Utils.USER_NAME, "");
        String password = prefs.getString(Utils.PASSWORD, "");
        return (userName != null && !userName.isEmpty() && password != null && !password.isEmpty());
    }

    public static boolean checkRememberMe(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String rememberMe = prefs.getString(Utils.REMEMBER_ME, "");
        return (rememberMe != null && !rememberMe.isEmpty() &&rememberMe.equals("yes"));
    }

    public static void logout(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Utils.USER_NAME);
        editor.remove(Utils.PASSWORD);
        editor.apply();
        ParseUser.logOutInBackground();
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
