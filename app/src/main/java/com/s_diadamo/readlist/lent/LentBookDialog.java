package com.s_diadamo.readlist.lent;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;

import java.util.Calendar;

public class LentBookDialog extends AlertDialog.Builder {
    private final Calendar calendar = Calendar.getInstance();
    private final Context context;
    private LentBookAdapter lentBookAdapter;

    public LentBookDialog(Context context, final LentBook lentBook) {
        super(context);
        this.context = context;
        generateEditLentBookDialog(lentBook);
    }

    public LentBookDialog(Context context, final Book book) {
        super(context);
        this.context = context;
        generateNewLentBookDialog(book);
    }

    public void setLentBookAdapter(LentBookAdapter lentBookAdapter) {
        this.lentBookAdapter = lentBookAdapter;
    }

    private void generateNewLentBookDialog(final Book book) {
        LayoutInflater inflater = LayoutInflater.from(context);

        final View lentBookDialog = inflater.inflate(R.layout.dialog_lend_book, null);

        final Button datePickerButton = (Button) lentBookDialog.findViewById(R.id.lent_book_date_button);
        final TextView dateView = (TextView) lentBookDialog.findViewById(R.id.lent_book_date);
        final EditText nameInput = (EditText) lentBookDialog.findViewById(R.id.lent_book_name);

        dateView.setText(Utils.cleanDate(calendar.getTime()));

        datePickerButton.setOnClickListener(getCalendarOnClick(dateView));

        setView(lentBookDialog);
        setTitle(R.string.lend_book);
        setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameInput.getText().toString();
                        if (name.isEmpty()) {
                            Utils.showToast(context, "Please fill in a name");
                            return;
                        }
                        LentBook lentBook = new LentBook(book.getId(), name, Utils.parseDate(calendar.getTime()));
                        new LentBookOperations(context).addLentBook(lentBook);
                        new SyncData(context).add(lentBook);
                    }
                }
        );

        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
    }

    private void generateEditLentBookDialog(final LentBook lentBook) {
        LayoutInflater inflater = LayoutInflater.from(context);

        final View lentBookDialog = inflater.inflate(R.layout.dialog_lend_book, null);

        final Button datePickerButton = (Button) lentBookDialog.findViewById(R.id.lent_book_date_button);
        final TextView dateView = (TextView) lentBookDialog.findViewById(R.id.lent_book_date);
        final EditText nameInput = (EditText) lentBookDialog.findViewById(R.id.lent_book_name);

        nameInput.setText(lentBook.getLentTo());

        dateView.setText(lentBook.getCleanDateLent());
        calendar.setTime(Utils.getDateFromString(lentBook.getDateLent()));

        datePickerButton.setOnClickListener(getCalendarOnClick(dateView));

        setView(lentBookDialog);
        setTitle(R.string.lend_book);
        setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameInput.getText().toString();
                        if (name.isEmpty()) {
                            Utils.showToast(context, "Please fill in a name");
                            return;
                        }

                        lentBook.setLentTo(name);
                        lentBook.setDateLent(Utils.parseDate(calendar.getTime()));
                        new LentBookOperations(context).updateLentBook(lentBook);
                        lentBookAdapter.notifyDataSetChanged();
                        new SyncData(context).update(lentBook);
                    }
                }
        );

        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
    }

    private View.OnClickListener getCalendarOnClick(final TextView dateView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateView.setText(Utils.cleanDate(calendar.getTime()));
                    }
                };

                new DatePickerDialog(context, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };
    }
}
