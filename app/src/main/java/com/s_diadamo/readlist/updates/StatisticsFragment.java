package com.s_diadamo.readlist.updates;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.readingSession.ReadingSessionOperations;
import com.s_diadamo.readlist.sync.SyncBookUpdateData;
import com.s_diadamo.readlist.sync.SyncPageUpdateData;

import java.util.ArrayList;


public class StatisticsFragment extends Fragment {
    private View rootView;
    private BookOperations bookOperations;
    private BookUpdateOperations bookUpdateOperations;
    private PageUpdateOperations pageUpdateOperations;
    private ReadingSessionOperations readingSessionOperations;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        context = rootView.getContext();

        setHasOptionsMenu(true);

        bookOperations = new BookOperations(context);
        bookUpdateOperations = new BookUpdateOperations(context);
        pageUpdateOperations = new PageUpdateOperations(context);
        readingSessionOperations = new ReadingSessionOperations(context);

        ProgressDialog progressDialog = new ProgressDialog(rootView.getContext());
        progressDialog.setMessage("Crunching the numbers");
        populateData();
        progressDialog.dismiss();

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getResources().getString(R.string.statistics));
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_statistics, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();
        if (id == R.id.reset_stats) {
            AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
            builder.setMessage("Are you sure you want to reset your statistics? " +
                    "This cannot be undone.");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (Utils.checkUserIsLoggedIn(context)) {
                        pageUpdateOperations.resetStatistics();
                        bookUpdateOperations.resetStatistics();
                        new SyncBookUpdateData(context).deleteBookUpdates();
                        new SyncPageUpdateData(context).deletePageUpdates();
                    } else {
                        ArrayList<BookUpdate> bookUpdates = bookUpdateOperations.getAllValidBookUpdates();
                        for (BookUpdate bookUpdate : bookUpdates) {
                            bookUpdate.delete();
                            bookUpdateOperations.updateBookUpdate(bookUpdate);
                        }

                        ArrayList<PageUpdate> pageUpdates = pageUpdateOperations.getAllValidPageUpdates();
                        for (PageUpdate pageUpdate : pageUpdates) {
                            pageUpdate.delete();
                            pageUpdateOperations.updatePageUpdate(pageUpdate);
                        }
                    }

                    populateData();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateData() {
        setAverageWeeklyData();
        setMonthlyData();
        setYearlyData();
        setAllTimeData();
    }

    private void setAverageWeeklyData() {
        TextView averageWeeklyReadingSessions = (TextView) rootView.findViewById(R.id.updates_weekly_reading_sessions);
        TextView averageWeeklyPages = (TextView) rootView.findViewById(R.id.updates_weekly_pages_read);
        TextView averageWeeklyTimeSpentReading = (TextView) rootView.findViewById(R.id.updates_weekly_time_spent_reading);

        averageWeeklyReadingSessions.setText(String.valueOf(readingSessionOperations.getAverageWeeklyReadingSessions()));
        averageWeeklyPages.setText(String.valueOf(pageUpdateOperations.getAverageWeeklyPages()));
        averageWeeklyTimeSpentReading.setText(formatTimeSpentReading(readingSessionOperations.getAverageWeeklyTimeSpentReading()));
    }

    private void setMonthlyData() {
        TextView monthlyReadingSessions = (TextView) rootView.findViewById(R.id.updates_monthly_reading_sessions);
        TextView monthlyPages = (TextView) rootView.findViewById(R.id.updates_monthly_pages_read);
        TextView monthlyBooks = (TextView) rootView.findViewById(R.id.updates_monthly_books_read);
        TextView monthlyTimeSpentReading = (TextView) rootView.findViewById(R.id.updates_monthly_time_spent_reading);

        monthlyReadingSessions.setText(String.valueOf(readingSessionOperations.getNumberOfReadingSessionsThisMonth()));
        monthlyPages.setText(String.valueOf(pageUpdateOperations.getNumberOfPagesReadThisMonth()));
        monthlyBooks.setText(String.valueOf(bookUpdateOperations.getNumberOfBooksReadThisMonth()));
        monthlyTimeSpentReading.setText(formatTimeSpentReading(readingSessionOperations.getTimeSpentReadingThisMonth()));
    }

    private void setYearlyData() {
        TextView yearlyReadingSessions = (TextView) rootView.findViewById(R.id.updates_yearly_reading_sessions);
        TextView yearlyPages = (TextView) rootView.findViewById(R.id.updates_yearly_pages_read);
        TextView yearlyBooks = (TextView) rootView.findViewById(R.id.updates_yearly_books_read);
        TextView yearlyTimeSpentReading = (TextView) rootView.findViewById(R.id.updates_yearly_time_spent_reading);

        yearlyReadingSessions.setText(String.valueOf(readingSessionOperations.getNumberOfReadingSessionsThisYear()));
        yearlyPages.setText(String.valueOf(pageUpdateOperations.getNumberOfPagesThisYear()));
        yearlyBooks.setText(String.valueOf(bookUpdateOperations.getNumberOfBooksReadThisYear()));
        yearlyTimeSpentReading.setText(formatTimeSpentReading(readingSessionOperations.getTimeSpentReadingThisYear()));
    }

    private void setAllTimeData() {
        TextView allTimeReadingSessions = (TextView) rootView.findViewById(R.id.updates_all_time_reading_sessions);
        TextView allTimePages = (TextView) rootView.findViewById(R.id.updates_all_time_pages_read);
        TextView allTimeBooks = (TextView) rootView.findViewById(R.id.updates_all_time_books);
        TextView allTimeBooksRead = (TextView) rootView.findViewById(R.id.updates_all_time_books_read);
        TextView allTimeTimeSpentReading = (TextView) rootView.findViewById(R.id.updates_all_time_time_spent_reading);

        allTimeReadingSessions.setText(String.valueOf(readingSessionOperations.getNumberOfReadingSessions()));
        allTimePages.setText(String.valueOf(pageUpdateOperations.getAllTimePagesRead()));
        allTimeBooks.setText(String.valueOf(bookOperations.getBooksCount()));
        allTimeBooksRead.setText(String.valueOf(bookUpdateOperations.getNumberOfBooksRead()));
        allTimeTimeSpentReading.setText(formatTimeSpentReading(readingSessionOperations.getTimeSpentReading()));
    }

    private String formatTimeSpentReading(int seconds) {
        int minutes = seconds / 60;
        int hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        return "" + String.format("%02d", hours)
                + ":" + String.format("%02d", minutes)
                + ":" + String.format("%02d", seconds);
    }
}
