package com.s_diadamo.readlist.book;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.Utils;
import com.s_diadamo.readlist.shelf.Shelf;

class BookManuallyAddBookDialog extends AlertDialog {
    public BookManuallyAddBookDialog(Context context, final BookAdapter bookAdapter, final BookOperations bookOperations, final Shelf shelf) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View content = inflater.inflate(R.layout.dialog_manually_add_book, null, false);

        setTitle("Add New Book");

        final EditText bookTitleEditText = (EditText) content.findViewById(R.id.manual_add_title);
        final EditText bookAuthorEditText = (EditText) content.findViewById(R.id.manual_add_author);
        final EditText bookPagesEditText = (EditText) content.findViewById(R.id.manual_add_pages);
        ImageView bookImage = (ImageView) content.findViewById(R.id.manual_add_cover_photo);
        Button addButton = (Button) content.findViewById(R.id.manual_add_add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookTitle = bookTitleEditText.getText().toString();
                String bookAuthor = bookAuthorEditText.getText().toString();
                String pages = bookPagesEditText.getText().toString();

                if (bookTitle.isEmpty()) {
                    Toast.makeText(content.getContext(), "Please add a title", Toast.LENGTH_LONG).show();
                } else {
                    pages = pages.isEmpty() ? "0" : pages;
                    bookAuthor = bookAuthor.isEmpty() ? "" : bookAuthor;
                    Book book = new Book(bookTitle, bookAuthor, shelf.getId(), Utils.getCurrentDate(), Integer.parseInt(pages), 0, shelf.getColour(), 0, "", "");
                    bookAdapter.add(book);
                    bookAdapter.notifyDataSetChanged();
                    bookOperations.addBook(book);
                    dismiss();
                }
            }
        });

        bookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Launch the camera
                Toast.makeText(content.getContext(), "Launch the camera", Toast.LENGTH_SHORT).show();
            }
        });

        setView(content);
    }
}
