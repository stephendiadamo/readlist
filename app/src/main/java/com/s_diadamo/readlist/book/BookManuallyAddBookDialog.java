package com.s_diadamo.readlist.book;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;

import java.util.Calendar;

/**
 * Created by s-diadamo on 15-06-04.
 */
public class BookManuallyAddBookDialog extends AlertDialog {
    public BookManuallyAddBookDialog(Context context, final BookAdapter bookAdapter, final BookOperations bookOperations) {
        super(context);

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View content = layoutInflater.inflate(R.layout.dialog_manually_add_book, null);

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

                if (bookTitle.isEmpty() || bookAuthor.isEmpty() || pages.isEmpty()) {
                    Toast.makeText(content.getContext(), "Please fill all information", Toast.LENGTH_LONG).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    Book book = new Book(bookTitle, bookAuthor, 0, calendar.getTime().toString(), Integer.parseInt(pages), 0, "", 0, "", "");
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
