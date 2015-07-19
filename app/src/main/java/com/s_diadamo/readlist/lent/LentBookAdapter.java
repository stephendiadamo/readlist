package com.s_diadamo.readlist.lent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.general.Utils;

import java.util.ArrayList;

public class LentBookAdapter extends ArrayAdapter<LentBook> {

    private final Context context;
    private final int layoutResourceID;
    private final ArrayList<LentBook> lentBooks;

    public LentBookAdapter(Context context, int resource, ArrayList<LentBook> lentBooks) {
        super(context, resource, lentBooks);
        this.context = context;
        this.layoutResourceID = resource;
        this.lentBooks = lentBooks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LentBookHolder lentBookHolder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceID, parent, false);

            lentBookHolder = new LentBookHolder();
            lentBookHolder.bookCover = (ImageView) row.findViewById(R.id.lent_book_cover);
            lentBookHolder.bookTitle = (TextView) row.findViewById(R.id.lent_book_title);
            lentBookHolder.lentTo = (TextView) row.findViewById(R.id.lent_book_to);
            lentBookHolder.lentOn = (TextView) row.findViewById(R.id.lent_book_date);

            row.setTag(lentBookHolder);
        } else {
            lentBookHolder = (LentBookHolder) row.getTag();
        }

        LentBook lentBook = lentBooks.get(position);
        if (!lentBook.getCoverPictureUrl().isEmpty()) {
            String bookCoverUri = lentBook.getCoverPictureUrl();
            if (bookCoverUri.startsWith(Utils.STORAGE_FILE_START)) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                final Bitmap bitmap = BitmapFactory.decodeFile(bookCoverUri, options);
                lentBookHolder.bookCover.setImageBitmap(bitmap);
            } else {
                MainActivity.imageLoader.DisplayImage(lentBook.getCoverPictureUrl(), lentBookHolder.bookCover);
            }
        }

        lentBookHolder.bookTitle.setText(lentBook.getTitle());
        lentBookHolder.lentOn.setText(lentBook.getCleanDateLent());
        lentBookHolder.lentTo.setText(lentBook.getLentTo());

        return row;
    }

    static class LentBookHolder {
        ImageView bookCover;
        TextView bookTitle;
        TextView lentTo;
        TextView lentOn;
    }
}
