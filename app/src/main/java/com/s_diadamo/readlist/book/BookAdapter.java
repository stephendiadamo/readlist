package com.s_diadamo.readlist.book;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.shelf.Shelf;

import java.util.ArrayList;

class BookAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Book> books;
    private boolean hideComplete;
    private boolean hideShelved;


    public BookAdapter(Context context, ArrayList<Book> books, boolean hideComplete, boolean hideShelved) {
        this.context = context;
        this.books = books;
        this.hideComplete = hideComplete;
        this.hideShelved = hideShelved;
    }

    public void toggleHideComplete() {
        hideComplete = !hideComplete;
    }

    public void toggleHideShelved() {
        hideShelved = !hideShelved;
    }

    public void updateVisibleBooks() {
        for (int i = 0; i < books.size(); i++) {
            if (hideComplete) {
                if (books.get(i).isComplete()) {
                    books.remove(i);
                    i--;
                }
            }

            if (hideShelved) {
                if (books.get(i).getShelfId() != Shelf.DEFAULT_SHELF_ID) {
                    books.remove(i);
                    i--;
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return books.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BookHolder bookHolder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.row_book_element_swipe, parent, false);

            bookHolder = new BookHolder();
            bookHolder.bookCover = (ImageView) row.findViewById(R.id.book_cover);
            bookHolder.bookTitle = (TextView) row.findViewById(R.id.book_title);
            bookHolder.bookLentIcon = (ImageView) row.findViewById(R.id.book_lent_icon);
            bookHolder.bookAuthor = (TextView) row.findViewById(R.id.book_author);
            bookHolder.currentPage = (TextView) row.findViewById(R.id.book_current_page);
            bookHolder.pages = (TextView) row.findViewById(R.id.book_pages);
            bookHolder.percentageComplete = (TextView) row.findViewById(R.id.book_percentage_complete);
            bookHolder.dateAdded = (TextView) row.findViewById(R.id.book_date_added);
            bookHolder.infoContainer = (LinearLayout) row.findViewById(R.id.book_info_container);
            bookHolder.pageInfoContainer = (LinearLayout) row.findViewById(R.id.book_page_detail_container);
            bookHolder.completeInfoContainer = (LinearLayout) row.findViewById(R.id.book_complete_container);

            bookHolder.deleteBook = (ImageView) row.findViewById(R.id.book_delete_book);
            bookHolder.editBook = (ImageView) row.findViewById(R.id.book_edit_book);
            bookHolder.lendBook = (ImageView) row.findViewById(R.id.book_lend_book);

            row.setTag(bookHolder);
        } else {
            bookHolder = (BookHolder) row.getTag();
        }
        Book book = books.get(position);
        if (!book.getCoverPictureUrl().isEmpty()) {
            String bookCoverUri = book.getCoverPictureUrl();
            if (bookCoverUri.startsWith(Utils.STORAGE_FILE_START)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                final Bitmap bitmap = BitmapFactory.decodeFile(bookCoverUri, options);
                bookHolder.bookCover.setImageBitmap(bitmap);
            } else {
                MainActivity.imageLoader.DisplayImage(book.getCoverPictureUrl(), bookHolder.bookCover);
            }
        } else {
            bookHolder.bookCover.setImageResource(R.drawable.sample_cover);
        }

        bookHolder.bookTitle.setText(book.getTitle());
        bookHolder.bookAuthor.setText(book.getAuthor());
        bookHolder.dateAdded.setText(book.getCleanDateAdded());

        if (book.getNumPages() != 0 && book.getCurrentPage() > 0) {
            int complete = (100 * book.getCurrentPage() / book.getNumPages());
            bookHolder.percentageComplete.setText(String.valueOf(complete) + "%");
            bookHolder.percentageComplete.setVisibility(View.VISIBLE);
            row.findViewById(R.id.book_percentage_background).setVisibility(View.VISIBLE);
        } else {
            bookHolder.percentageComplete.setVisibility(View.INVISIBLE);
            row.findViewById(R.id.book_percentage_background).setVisibility(View.INVISIBLE);
        }

        if (book.isComplete()) {
            bookHolder.pageInfoContainer.setVisibility(View.GONE);
            bookHolder.completeInfoContainer.setVisibility(View.VISIBLE);
            ((TextView) row.findViewById(R.id.book_complete_date)).setText(book.getCleanCompletionDate());
        } else {
            bookHolder.pageInfoContainer.setVisibility(View.VISIBLE);
            bookHolder.completeInfoContainer.setVisibility(View.GONE);
            bookHolder.currentPage.setText(String.valueOf(book.getCurrentPage()));
            bookHolder.pages.setText(String.valueOf(book.getNumPages()));
        }

        if (book.isLent(row.getContext())) {
            bookHolder.bookLentIcon.setVisibility(View.VISIBLE);
        } else {
            bookHolder.bookLentIcon.setVisibility(View.INVISIBLE);
        }

        bookHolder.infoContainer.setBackground(book.getColorAsDrawable());
        return row;
    }

    static class BookHolder {
        ImageView bookCover;
        ImageView bookLentIcon;
        TextView bookTitle;
        TextView bookAuthor;
        TextView currentPage;
        TextView pages;
        TextView percentageComplete;
        TextView dateAdded;
        LinearLayout infoContainer;
        LinearLayout pageInfoContainer;
        LinearLayout completeInfoContainer;

        ImageView deleteBook;
        ImageView editBook;
        ImageView lendBook;
    }
}
