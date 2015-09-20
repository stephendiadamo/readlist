package com.s_diadamo.readlist.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.LoaderIDs;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.readingSession.ReadingSession;
import com.s_diadamo.readlist.readingSession.ReadingSessionAdapter;
import com.s_diadamo.readlist.readingSession.ReadingSessionLoader;
import com.s_diadamo.readlist.readingSession.ReadingSessionOperations;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.updates.PageUpdate;
import com.s_diadamo.readlist.updates.PageUpdateAdapter;
import com.s_diadamo.readlist.updates.PageUpdateLoader;
import com.s_diadamo.readlist.updates.PageUpdateOperations;

import java.util.ArrayList;

public class BookStatsFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private View rootView;
    private Context context;
    private int bookId;
    private ListView readingSessionsListView;
    private ReadingSessionAdapter readingSessionAdapter;
    private ReadingSessionOperations readingSessionOperations;
    private ListView pageUpdateListView;
    private PageUpdateAdapter pageUpdateAdapter;
    private PageUpdateOperations pageUpdateOperations;
    private TextView timeSpentReading;

    private int listId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        rootView = inflater.inflate(R.layout.fragment_view_book_statistics, container, false);
        context = rootView.getContext();
        bookId = getArguments().getInt(BookAdapter.BOOK_ID);
        Book book = new BookOperations(context).getBook(bookId);

        readingSessionOperations = new ReadingSessionOperations(context);
        pageUpdateOperations = new PageUpdateOperations(context);

        TextView title = (TextView) rootView.findViewById(R.id.view_book_stats_title);
        TextView author = (TextView) rootView.findViewById(R.id.view_book_stats_author);
        TextView dateAdded = (TextView) rootView.findViewById(R.id.view_book_stats_date_added);
        TextView percentComplete = (TextView) rootView.findViewById(R.id.view_book_stats_percent_completed);
        TextView rating = (TextView) rootView.findViewById(R.id.view_book_stats_rating);
        timeSpentReading = (TextView) rootView.findViewById(R.id.view_book_time_spent_reading);
        readingSessionsListView = (ListView) rootView.findViewById(R.id.view_book_stats_reading_sessions_list);
        pageUpdateListView = (ListView) rootView.findViewById(R.id.view_book_stats_page_update_list);

        getLoaderManager().initLoader(LoaderIDs.READING_SESSION_LOADER_ID, null, this);
        getLoaderManager().initLoader(LoaderIDs.PAGE_UPDATE_LOADER_ID, null, this);

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        dateAdded.setText(book.getCleanDateAdded());

        int percent = book.getNumPages() != 0 ?
                100 * book.getCurrentPage() / book.getNumPages() : 0;
        percentComplete.setText("" + percent + "%");

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
        listId = v.getId();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                switch (listId) {
                    case R.id.view_book_stats_reading_sessions_list:
                        final ReadingSession readingSession = readingSessionAdapter.getItem(info.position);
                        deleteReadingSession(readingSession);
                        break;
                    case R.id.view_book_stats_page_update_list:
                        final PageUpdate pageUpdate = pageUpdateAdapter.getItem(info.position);
                        deletePageUpdate(pageUpdate);
                        break;
                }
                return true;
        }

        return super.onContextItemSelected(item);
    }

    private void deletePageUpdate(final PageUpdate pageUpdate) {
        new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.delete_this_reading_session))
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utils.checkUserIsLoggedIn(context)) {
                            new SyncData(context).delete(pageUpdate);
                            pageUpdateOperations.deletePageUpdate(pageUpdate);
                        } else {
                            pageUpdate.delete();
                            pageUpdateOperations.updatePageUpdate(pageUpdate);
                        }
                        pageUpdateAdapter.remove(pageUpdate);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void deleteReadingSession(final ReadingSession readingSession) {
        new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.delete_this_reading_session))
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utils.checkUserIsLoggedIn(context)) {
                            new SyncData(context).delete(readingSession);
                            readingSessionOperations.deleteSession(readingSession);
                        } else {
                            readingSession.delete();
                            readingSessionOperations.update(readingSession);
                        }

                        int timeSpentReadingBook = readingSessionOperations.getTimeSpentReadingForBook(bookId);
                        timeSpentReading.setText(Utils.formatTimeSpentReading(timeSpentReadingBook));
                        readingSessionAdapter.remove(readingSession);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LoaderIDs.READING_SESSION_LOADER_ID:
                return new ReadingSessionLoader(context, bookId);
            case LoaderIDs.PAGE_UPDATE_LOADER_ID:
                return new PageUpdateLoader(context, bookId);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        switch (id) {
            case LoaderIDs.READING_SESSION_LOADER_ID:
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
                readingSessionsListView.setEmptyView(rootView.findViewById(R.id.view_book_stats_empty_reading_sessions));
                break;
            case LoaderIDs.PAGE_UPDATE_LOADER_ID:
                ArrayList<PageUpdate> pageUpdates = (ArrayList<PageUpdate>) data;
                pageUpdateAdapter = new PageUpdateAdapter(context, pageUpdates);
                pageUpdateListView.setAdapter(pageUpdateAdapter);

                registerForContextMenu(pageUpdateListView);
                pageUpdateListView.setLongClickable(false);
                pageUpdateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getActivity().openContextMenu(view);
                    }
                });
                pageUpdateListView.setEmptyView(rootView.findViewById(R.id.view_book_stats_empty_page_update));
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }
}
