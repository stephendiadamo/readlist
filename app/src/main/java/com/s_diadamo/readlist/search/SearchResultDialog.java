package com.s_diadamo.readlist.search;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.general.Utils;

import java.util.ArrayList;

class SearchResultDialog extends AlertDialog.Builder {
    public SearchResultDialog(final Context context, final ArrayList<Book> books, final FragmentManager manager) {
        super(context);

        if (books.isEmpty()) {
            setTitle(R.string.no_results_found);
            setMessage(R.string.please_try_again_different_parameters);

            setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Utils.launchBookFragment(manager);
                }
            });
        } else {
            setTitle(R.string.search_results);
            final ListView bookListView = new ListView(context);
            bookListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            final SearchAdapter searchAdapter = new SearchAdapter(context, books);

            bookListView.setAdapter(searchAdapter);
            bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (searchAdapter.selectedBooks.contains(position)) {
                        searchAdapter.selectedBooks.remove(position);
                        view.setBackground(new ColorDrawable(Color.TRANSPARENT));
                    } else {
                        searchAdapter.selectedBooks.add(position);
                        view.setBackground(new ColorDrawable(Color.LTGRAY));
                    }
                }
            });

            setPositiveButton(R.string.add_selected, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BookOperations bookOperations = new BookOperations(context);
                    boolean loggedIn = Utils.checkUserIsLoggedIn(context);

                    SyncData syncData = new SyncData(context);
                    SparseBooleanArray booleanArray = bookListView.getCheckedItemPositions();

                    for (int i = 0; i < books.size(); i++) {
                        if (booleanArray.get(i)) {
                            bookOperations.addBook(books.get(i));
                            if (loggedIn) {
                                syncData.add(books.get(i));
                            }
                        }
                    }

                    Utils.launchBookFragment(manager);
                }
            });

            setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            setView(bookListView);
        }
    }
}
