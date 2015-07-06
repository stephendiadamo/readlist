package com.s_diadamo.readlist.updates;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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


public class StatisticsFragment extends Fragment {
    private View rootView;
    private BookOperations bookOperations;
    private BookUpdateOperations bookUpdateOperations;
    private PageUpdateOperations pageUpdateOperations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        setHasOptionsMenu(true);

        bookOperations = new BookOperations(container.getContext());
        bookUpdateOperations = new BookUpdateOperations(container.getContext());
        pageUpdateOperations = new PageUpdateOperations(container.getContext());

        ProgressDialog progressDialog = new ProgressDialog(rootView.getContext());
        progressDialog.setMessage("Crunching the numbers.");
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
                    pageUpdateOperations.resetStatistics();
                    bookUpdateOperations.resetStatistics();
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

    private void setYearlyData() {
        TextView yearlyUpdates = (TextView) rootView.findViewById(R.id.updates_yearly_updates);
        TextView yearlyPages = (TextView) rootView.findViewById(R.id.updates_yearly_pages_read);
        TextView yearlyBooks = (TextView) rootView.findViewById(R.id.updates_yearly_books_read);

        yearlyUpdates.setText(String.valueOf(pageUpdateOperations.getNumberOfPageUpdatesThisYear()));
        yearlyPages.setText(String.valueOf(pageUpdateOperations.getNumberOfPagesThisYear()));
        yearlyBooks.setText(String.valueOf(bookUpdateOperations.getNumberOfBooksReadThisYear()));
    }

    private void setMonthlyData() {
        TextView monthlyUpdates = (TextView) rootView.findViewById(R.id.updates_monthly_updates);
        TextView monthlyPages = (TextView) rootView.findViewById(R.id.updates_monthly_pages_read);
        TextView monthlyBooks = (TextView) rootView.findViewById(R.id.updates_monthly_books_read);

        monthlyUpdates.setText(String.valueOf(pageUpdateOperations.getNumberOfPageUpdatesThisMonth()));
        monthlyPages.setText(String.valueOf(pageUpdateOperations.getNumberOfPagesReadThisMonth()));
        monthlyBooks.setText(String.valueOf(bookUpdateOperations.getNumberOfBooksReadThisMonth()));

    }

    private void setAverageWeeklyData() {
        TextView averageWeeklyUpdates = (TextView) rootView.findViewById(R.id.updates_weekly_updates);
        TextView averageWeeklyPages = (TextView) rootView.findViewById(R.id.updates_weekly_pages_read);

        averageWeeklyUpdates.setText(String.valueOf(pageUpdateOperations.getAverageWeeklyPageUpdates()));
        averageWeeklyPages.setText(String.valueOf(pageUpdateOperations.getAverageWeeklyPages()));
    }

    private void setAllTimeData() {
        TextView allTimeUpdates = (TextView) rootView.findViewById(R.id.updates_all_time_updates);
        TextView allTimePages = (TextView) rootView.findViewById(R.id.updates_all_time_pages_read);
        TextView allTimeBooks = (TextView) rootView.findViewById(R.id.updates_all_time_books);
        TextView allTimeBooksRead = (TextView) rootView.findViewById(R.id.updates_all_time_books_read);

        allTimeUpdates.setText(String.valueOf(pageUpdateOperations.getNumberOfPageUpdates()));
        allTimePages.setText(String.valueOf(pageUpdateOperations.getAllTimePagesRead()));
        allTimeBooks.setText(String.valueOf(bookOperations.getBooksCount()));
        allTimeBooksRead.setText(String.valueOf(bookUpdateOperations.getNumberOfBooksRead()));
    }

}
