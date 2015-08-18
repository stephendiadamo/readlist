package com.s_diadamo.readlist.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.sync.SyncBookData;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.updates.PageUpdate;
import com.s_diadamo.readlist.updates.PageUpdateOperations;

class BookUpdatePageDialog extends AlertDialog.Builder {
    public BookUpdatePageDialog(final Context context, final Book book, final BookAdapter bookAdapter, final BookOperations bookOperations) {
        super(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View content = layoutInflater.inflate(R.layout.dialog_set_book_current_page, null);

        setTitle(R.string.update_page);

        final NumberPicker pagePicker = (NumberPicker) content.findViewById(R.id.set_page_picker);
        pagePicker.setMinValue(0);
        pagePicker.setMaxValue(book.getNumPages());
        pagePicker.setWrapSelectorWheel(false);
        pagePicker.setValue(book.getCurrentPage());

        Button addTwenty = (Button) content.findViewById(R.id.set_page_plus_twenty);
        Button addFifty = (Button) content.findViewById(R.id.set_page_plus_fifty);

        addTwenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagePicker.setValue(pagePicker.getValue() + 20);
            }
        });

        addFifty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagePicker.setValue(pagePicker.getValue() + 50);
            }
        });
        setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int pagesRead = pagePicker.getValue() - book.getCurrentPage();

                book.setCurrentPage(pagePicker.getValue());
                bookOperations.updateBook(book);
                bookAdapter.notifyDataSetChanged();

                PageUpdate pageUpdate = new PageUpdate(book.getId(), Utils.getCurrentDate(), pagesRead);
                new PageUpdateOperations(context).addPageUpdate(pageUpdate);
                if (Utils.checkUserIsLoggedIn(context)) {
                    new SyncData(context).add(pageUpdate);
                    new SyncData(context).update(book);
                }
            }
        });
        setNegativeButton(R.string.cancel, null);
        setView(content);
    }
}
