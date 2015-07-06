package com.s_diadamo.readlist;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.s_diadamo.readlist.book.BookFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CLEAN_DATE_FORMAT = "dd-MM-yyyy";

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

    public static void launchBookFragment(FragmentManager fragmentManager){
        Fragment fragment = new BookFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
