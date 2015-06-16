package com.s_diadamo.readlist.updates;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;

import java.util.ArrayList;


public class StatisticsFragment extends Fragment {
    View rootView;
    BookOperations bookOperations;
    UpdateOperations updateOperations;
    ArrayList<Update> updates;
    ArrayList<Book> books;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        setHasOptionsMenu(false);

        bookOperations = new BookOperations(container.getContext());
        updateOperations = new UpdateOperations(container.getContext());

        //TODO: Will this kill RAM?
        updates = updateOperations.getAllUpdates();

        populateData();

        return rootView;
    }

    private void populateData() {
//        setAverageWeeklyData();
//        setMontlyData();
//        setYearlyData();
        setAllTimeData();
    }

    private void setYearlyData() {
        TextView yearlyUpdates = (TextView) rootView.findViewById(R.id.updates_yearly_updates);
        TextView yearlyPages = (TextView) rootView.findViewById(R.id.updates_yearly_pages_read);
        TextView yearlyBooks = (TextView) rootView.findViewById(R.id.updates_yearly_books_read);
    }

    private void setMontlyData() {
        TextView monthlyUpdates = (TextView) rootView.findViewById(R.id.updates_monthly_updates);
        TextView monthlyPages = (TextView) rootView.findViewById(R.id.updates_monthly_pages_read);
        TextView monthlyBooks = (TextView) rootView.findViewById(R.id.updates_monthly_books_read);
    }

    private void setAverageWeeklyData() {
        TextView averageWeeklyUpdates = (TextView) rootView.findViewById(R.id.updates_weekly_updates);
        TextView averageWeeklyPages = (TextView) rootView.findViewById(R.id.updates_weekly_pages_read);
    }

    private void setAllTimeData() {
        TextView allTimeUpdates = (TextView) rootView.findViewById(R.id.updates_all_time_updates);
        TextView allTimePages = (TextView) rootView.findViewById(R.id.updates_all_time_pages_read);
        TextView allTimeBooks = (TextView) rootView.findViewById(R.id.updates_all_time_books);
        TextView allTimeBooksRead = (TextView) rootView.findViewById(R.id.updates_all_time_books_read);

        allTimeUpdates.setText(String.valueOf(updates.size()));
        allTimePages.setText(String.valueOf(updateOperations.getAllTimePagesRead()));
        allTimeBooks.setText(String.valueOf(bookOperations.getBooksCount()));
        allTimeBooksRead.setText(String.valueOf(bookOperations.getNumberOfBooksRead()));
    }

}
