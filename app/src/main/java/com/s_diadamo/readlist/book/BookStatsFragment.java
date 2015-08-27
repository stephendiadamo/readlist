package com.s_diadamo.readlist.book;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.readingSession.ReadingSession;
import com.s_diadamo.readlist.readingSession.ReadingSessionAdapter;
import com.s_diadamo.readlist.readingSession.ReadingSessionLoader;
import com.s_diadamo.readlist.readingSession.ReadingSessionOperations;
import com.s_diadamo.readlist.sync.SyncData;

import java.util.ArrayList;

public class BookStatsFragment extends Fragment implements LoaderManager.LoaderCallbacks {


    private Context context;
    private int bookId;
    private ListView readingSessionsListView;
    private ReadingSessionAdapter readingSessionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        View rootView = inflater.inflate(R.layout.fragment_view_book_statistics, container, false);
        context = rootView.getContext();
        bookId = getArguments().getInt(BookAdapter.BOOK_ID);
        Book book = new BookOperations(context).getBook(bookId);

        ReadingSessionOperations readingSessionOperations = new ReadingSessionOperations(context);

        TextView title = (TextView) rootView.findViewById(R.id.view_book_stats_title);
        TextView author = (TextView) rootView.findViewById(R.id.view_book_stats_author);
        TextView dateAdded = (TextView) rootView.findViewById(R.id.view_book_stats_date_added);
        TextView percentComplete = (TextView) rootView.findViewById(R.id.view_book_stats_percent_completed);
        TextView rating = (TextView) rootView.findViewById(R.id.view_book_stats_rating);
        TextView timeSpentReading = (TextView) rootView.findViewById(R.id.view_book_time_spent_reading);
        readingSessionsListView = (ListView) rootView.findViewById(R.id.view_book_stats_reading_sessions_list);

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        dateAdded.setText(book.getCleanDateAdded());

        int percent = book.getNumPages() != 0 ?
                100 * book.getCurrentPage() / book.getNumPages() : 0;
        percentComplete.setText("" + percent);

        double bookRating = book.getRating() != -1 ? book.getRating() : 0;
        rating.setText("" + bookRating);

        int timeSpentReadingBook = readingSessionOperations.getTimeSpentReadingForBook(bookId);
        timeSpentReading.setText(Utils.formatTimeSpentReading(timeSpentReadingBook));

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.clear();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_general_delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final ReadingSession readingSession = readingSessionAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.delete:
                if (Utils.checkUserIsLoggedIn(context)) {
                    //TODO: Parse update here
                    new ReadingSessionOperations(context).deleteSession(readingSession);
                } else {
                    readingSession.delete();
                    new ReadingSessionOperations(context).update(readingSession);
                }
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ReadingSessionLoader.ID:
                return new ReadingSessionLoader(context, bookId);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        switch (id) {
            case ReadingSessionLoader.ID:
                ArrayList<ReadingSession> readingSessions = (ArrayList<ReadingSession>) data;
                readingSessionAdapter = new ReadingSessionAdapter(context, readingSessions);
                readingSessionsListView.setAdapter(readingSessionAdapter);


                registerForContextMenu(readingSessionsListView);
                readingSessionsListView.setLongClickable(false);
                readingSessionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getActivity().openContextMenu(view);
                    }
                });
                break;
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
    }
}
