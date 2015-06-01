package com.s_diadamo.readlist.book;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.s_diadamo.readlist.R;

import java.util.Calendar;

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

    public static void manuallyAddBook(View view, final BookOperations bookOperations, final BookAdapter bookAdapter) {
        final Dialog manuallyAddBookDialog = new Dialog(view.getContext());
        manuallyAddBookDialog.setContentView(R.layout.dialog_manually_add_book);
        manuallyAddBookDialog.setTitle("Add New Book");

        final EditText bookTitleEditText = (EditText) manuallyAddBookDialog.findViewById(R.id.manual_add_title);
        final EditText bookAuthorEditText = (EditText) manuallyAddBookDialog.findViewById(R.id.manual_add_author);
        final EditText bookPagesEditText = (EditText) manuallyAddBookDialog.findViewById(R.id.manual_add_pages);
        Button addButton = (Button) manuallyAddBookDialog.findViewById(R.id.manual_add_add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookTitle = bookTitleEditText.getText().toString();
                String bookAuthor = bookAuthorEditText.getText().toString();
                String pages = bookPagesEditText.getText().toString();

                if (bookTitle.isEmpty() || bookAuthor.isEmpty() || pages.isEmpty()) {
                    Toast.makeText(manuallyAddBookDialog.getContext(), "Please fill all information", Toast.LENGTH_LONG).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    Book book = new Book(bookTitle, bookAuthor, 0, calendar.getTime().toString(), Integer.parseInt(pages), 0, "", 0, "");
                    bookAdapter.add(book);
                    bookAdapter.notifyDataSetChanged();
                    bookOperations.addBook(book);
                    manuallyAddBookDialog.dismiss();
                }
            }
        });

        manuallyAddBookDialog.show();
    }
}
