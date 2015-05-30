package com.s_diadamo.readlist.book;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
}
