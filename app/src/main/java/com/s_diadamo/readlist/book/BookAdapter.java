package com.s_diadamo.readlist.book;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.s_diadamo.readlist.comment.Comment;
import com.s_diadamo.readlist.comment.CommentFragment;
import com.s_diadamo.readlist.general.Analytics;
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.lent.LentBook;
import com.s_diadamo.readlist.lent.LentBookOperations;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.updates.BookUpdate;
import com.s_diadamo.readlist.updates.BookUpdateOperations;
import com.s_diadamo.readlist.updates.PageUpdate;
import com.s_diadamo.readlist.updates.PageUpdateOperations;

import java.util.ArrayList;

class BookAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Book> books;
    private boolean hideComplete;
    private boolean hideShelved;
    private BookOperations bookOperations;
    private FragmentManager fragmentManager;

    private static final String BOOK_ID = "BOOK_ID";
    private static final String EDIT_BOOK = "EDIT_BOOK";
    private static final String COMMENT_BOOK = "COMMENT_BOOK";


    public BookAdapter(Context context, ArrayList<Book> books, boolean hideComplete, boolean hideShelved, FragmentManager fragmentManager) {
        this.context = context;
        this.books = books;
        this.hideComplete = hideComplete;
        this.hideShelved = hideShelved;
        this.bookOperations = new BookOperations(context);
        this.fragmentManager = fragmentManager;
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
                if (books.size() > 0 && books.get(i).getShelfId() != Shelf.DEFAULT_SHELF_ID) {
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
    public View getView(int position, View convertView, final ViewGroup parent) {
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
            bookHolder.rating = (RatingBar) row.findViewById(R.id.book_rating);

            bookHolder.deleteBook = (ImageButton) row.findViewById(R.id.book_delete_book);
            bookHolder.editBook = (ImageButton) row.findViewById(R.id.book_edit_book);
            bookHolder.lendBook = (ImageButton) row.findViewById(R.id.book_lend_book);
            bookHolder.completeBook = (ImageButton) row.findViewById(R.id.book_mark_complete);
            bookHolder.updateBook = (ImageButton) row.findViewById(R.id.book_update);
            bookHolder.moreOptions = (ImageButton) row.findViewById(R.id.book_more_options);

            row.setTag(bookHolder);
        } else {
            bookHolder = (BookHolder) row.getTag();
        }

        final Book book = books.get(position);
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

            bookHolder.completeBook.setImageResource(R.drawable.ic_reread);
            bookHolder.completeBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseAnalytics.trackEventInBackground(Analytics.REREAD_BOOK);
                    rereadBook(book);
                }
            });
        } else {
            bookHolder.pageInfoContainer.setVisibility(View.VISIBLE);
            bookHolder.completeInfoContainer.setVisibility(View.GONE);
            bookHolder.currentPage.setText(String.valueOf(book.getCurrentPage()));
            bookHolder.pages.setText(String.valueOf(book.getNumPages()));

            bookHolder.completeBook.setImageResource(R.drawable.ic_done_black_128dp_2x);
            bookHolder.completeBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseAnalytics.trackEventInBackground(Analytics.COMPLETED_BOOK);
                    addRemainingPagesAndCompleteBook(book);
                }
            });
        }

        if (book.isLent(row.getContext())) {
            bookHolder.bookLentIcon.setVisibility(View.VISIBLE);
            bookHolder.lendBook.setImageResource(R.drawable.ic_unlend);
            bookHolder.lendBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseAnalytics.trackEventInBackground(Analytics.UNLENT_BOOK);
                    unLendBook(book);
                }
            });
        } else {
            bookHolder.bookLentIcon.setVisibility(View.INVISIBLE);
            bookHolder.lendBook.setImageResource(R.drawable.ic_lent_book);
            bookHolder.lendBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseAnalytics.trackEventInBackground(Analytics.UNLENT_BOOK);
                    lendBook(book);
                }
            });
        }

        bookHolder.deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseAnalytics.trackEventInBackground(Analytics.DELETED_BOOK);
                deleteBook(book);
            }
        });

        bookHolder.editBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseAnalytics.trackEventInBackground(Analytics.EDITED_BOOK);
                launchEditBookFragment(book);
            }
        });


        bookHolder.updateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseAnalytics.trackEventInBackground(Analytics.UPDATED_PAGE);
                updateBook(book);
            }
        });

        bookHolder.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_book_more_options, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        long id = item.getItemId();
                        if (id == R.id.comment) {
                            launchCommentFragment(book);
                        } else if (id == R.id.rate) {
                            showRatingDialog(book);
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        if (book.getRating() == -1) {
            bookHolder.rating.setVisibility(View.GONE);
        } else {
            bookHolder.rating.setVisibility(View.VISIBLE);
            bookHolder.rating.setRating((float) book.getRating());
        }

        bookHolder.infoContainer.setBackground(book.getColorAsDrawable());

        return row;
    }

    private void launchCommentFragment(Book book) {
        Bundle bundle = new Bundle();
        bundle.putInt(Comment.COMMENT_BOOK_ID, book.getId());

        Fragment fragment = new CommentFragment();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .addToBackStack(COMMENT_BOOK)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void deleteBook(final Book book) {
        new AlertDialog.Builder(context)
                .setMessage("Delete \"" + book.getTitle() + "\"?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utils.checkUserIsLoggedIn(context)) {
                            new SyncData(context).delete(book);
                            if (book.isLent(context)) {
                                new SyncData(context).delete(book.getLentBook(context));
                            }
                            bookOperations.deleteBook(book);
                        } else {
                            book.delete();
                            book.deleteLentBook(context);
                            bookOperations.updateBook(book);
                        }
                        books.remove(book);
                        notifyDataSetChanged();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void launchEditBookFragment(Book book) {
        Bundle bundle = new Bundle();
        //TODO: WTF is this? Use putInt...
        String bookId = String.valueOf(book.getId());
        bundle.putString(BOOK_ID, bookId);

        Fragment fragment = new BookEditFragment();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .addToBackStack(EDIT_BOOK)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void addRemainingPagesAndCompleteBook(Book book) {
        int remainingPages = book.getNumPages() - book.getCurrentPage();
        PageUpdate pageUpdate = new PageUpdate(book.getId(), remainingPages);
        new PageUpdateOperations(context).
                addPageUpdate(pageUpdate);
        if (Utils.checkUserIsLoggedIn(context)) {
            new SyncData(context).add(pageUpdate);
        }

        book.markComplete();
        book.setCurrentPage(book.getNumPages());

        notifyDataSetChanged();
        bookOperations.updateBook(book);

        BookUpdate bookUpdate = new BookUpdate(book.getId());
        new BookUpdateOperations(context).addBookUpdate(bookUpdate);
        if (Utils.checkUserIsLoggedIn(context)) {
            SyncData syncData = new SyncData(context);
            syncData.add(bookUpdate);
            syncData.update(book);
        }
    }

    private void unLendBook(Book book) {
        ParseAnalytics.trackEventInBackground(Analytics.UNLENT_BOOK);
        final LentBook lentBook = book.getLentBook(context);
        new AlertDialog.Builder(context)
                .setTitle("Unlend Confirmation")
                .setMessage("Retrieve book from " + lentBook.getLentTo() + "?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.checkUserIsLoggedIn(context)) {
                            new SyncData(context).delete(lentBook);
                            new LentBookOperations(context).deleteLentBook(lentBook);
                        } else {
                            lentBook.delete();
                            new LentBookOperations(context).updateLentBook(lentBook);
                        }
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void lendBook(Book book) {
        ParseAnalytics.trackEventInBackground(Analytics.LENT_BOOK);
        new BookMenuActions(context, this).lendBook(book);
    }

    private void rereadBook(Book book) {
        book.reread();
        notifyDataSetChanged();
        bookOperations.updateBook(book);
    }

    private void updateBook(Book book) {
        new BookMenuActions(context, bookOperations, this).setCurrentPage(book);
    }


    private void showRatingDialog(final Book book) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View rateBookDialog = inflater.inflate(R.layout.dialog_rate_book, null);
        final RatingBar ratingBar = (RatingBar) rateBookDialog.findViewById(R.id.book_rating_bar);

        ratingBar.setRating((float) book.getRating());
        alertDialog.setView(rateBookDialog);
        alertDialog.setTitle(R.string.add_rating);
        alertDialog
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        book.setRating(ratingBar.getRating());
                        bookOperations.updateBook(book);
                        if (Utils.checkUserIsLoggedIn(context)) {
                            new SyncData(context).update(book);
                        }
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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
        RatingBar rating;

        ImageButton deleteBook;
        ImageButton editBook;
        ImageButton lendBook;
        ImageButton completeBook;
        ImageButton updateBook;
        ImageButton moreOptions;
    }
}
