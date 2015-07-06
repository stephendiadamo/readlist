package com.s_diadamo.readlist.book;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.updates.PageUpdate;
import com.s_diadamo.readlist.updates.PageUpdateOperations;

class BookUpdatePageDialog extends AlertDialog {
    public BookUpdatePageDialog(final Context context, final Book book, final BookAdapter bookAdapter, final BookOperations bookOperations) {
        super(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View content = layoutInflater.inflate(R.layout.dialog_set_book_current_page, null);

        setTitle("PageUpdate Page");

        final NumberPicker pagePicker = (NumberPicker) content.findViewById(R.id.set_page_picker);
        pagePicker.setMinValue(0);
        pagePicker.setMaxValue(book.getNumPages());
        pagePicker.setWrapSelectorWheel(false);
        pagePicker.setValue(book.getCurrentPage());

        Button addTwenty = (Button) content.findViewById(R.id.set_page_plus_twenty);
        Button addFifty = (Button) content.findViewById(R.id.set_page_plus_fifty);
        Button done = (Button) content.findViewById(R.id.set_page_done);

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

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                book.setCurrentPage(pagePicker.getValue());
                bookOperations.updateBook(book);
                bookAdapter.notifyDataSetChanged();

                PageUpdate pageUpdate = new PageUpdate(book.getId(), Utils.getCurrentDate(), pagePicker.getValue());
                new PageUpdateOperations(context).addPageUpdate(pageUpdate);

                dismiss();
            }
        });

        setView(content);
    }
}
