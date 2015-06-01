package com.s_diadamo.readlist.book;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.s_diadamo.readlist.R;

public class BookMenuActions {

    public static void editNumberOfPages(final Book book, View view, final BookOperations bookOperations, final BookAdapter bookAdapter) {
        final Dialog editNumberOfPagesDialog = new Dialog(view.getContext());
        editNumberOfPagesDialog.setContentView(R.layout.dialog_edit_book_pages);
        editNumberOfPagesDialog.setTitle("Edit Book Pages");

        final Button updatePagesButton = (Button) editNumberOfPagesDialog.findViewById(R.id.update_page_button);
        updatePagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNumberOfPages = ((EditText) editNumberOfPagesDialog.findViewById(R.id.update_page_num_value)).getText().toString();
                if (!newNumberOfPages.isEmpty()) {
                    int pages = Integer.parseInt(newNumberOfPages);
                    book.setNumPages(pages);
                    bookOperations.updateBook(book);
                    bookAdapter.notifyDataSetChanged();
                }
                editNumberOfPagesDialog.dismiss();
            }
        });
        editNumberOfPagesDialog.show();
    }

    public static void setCurrentPage(final Book book, View view, final BookOperations bookOperations, final BookAdapter bookAdapter) {
        final Dialog setCurrentPageDialog = new Dialog(view.getContext());
        setCurrentPageDialog.setContentView(R.layout.dialog_set_book_current_page);
        setCurrentPageDialog.setTitle("Update Page");

        final NumberPicker pagePicker = (NumberPicker) setCurrentPageDialog.findViewById(R.id.set_page_picker);
        pagePicker.setMinValue(0);
        pagePicker.setMaxValue(book.getNumPages());
        pagePicker.setValue(book.getCurrentPage());

        Button addTwenty = (Button) setCurrentPageDialog.findViewById(R.id.set_page_plus_twenty);
        Button addFifty = (Button) setCurrentPageDialog.findViewById(R.id.set_page_plus_fifty);
        Button done = (Button) setCurrentPageDialog.findViewById(R.id.set_page_done);

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
                setCurrentPageDialog.dismiss();
            }
        });
        setCurrentPageDialog.show();
    }
}
