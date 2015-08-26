package com.s_diadamo.readlist.readingSession;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.book.BookUpdatePageDialog;
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.general.Utils;


public class ReadingSessionActivity extends AppCompatActivity {

    public static String SESSION_BOOK_ID = "session_book_id";
    private static String NOTIFICATION_ID_TAG = "notification_id";
    private static String DO_NOT_ASK_TO_UPDATE_PAGE = "do_not_ask_to_update_page";
    private static String START_TIME = "start_time";
    private static int NOTIFICATION_ID = 0;

    boolean timerStarted = false;

    private Handler handler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private TextView timerTextView;
    private TextView bookTitle;
    private ImageButton startStopButton;
    private static Book book;
    private Context context;

    private Runnable timerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int seconds = (int) (updatedTime / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            timerTextView.setText(formatTime(hours, minutes, seconds));
            handler.postDelayed(this, 0);
        }
    };

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reading_session);
        context = this;

        bookTitle = (TextView) findViewById(R.id.record_reading_activity_book_title);
        startStopButton = (ImageButton) findViewById(R.id.record_reading_activity_start_button);
        timerTextView = (TextView) findViewById(R.id.record_reading_activity_timer);
        final Button saveSession = (Button) findViewById(R.id.record_reading_activity_save_session);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!timerStarted) {
                    timerStarted = true;
                    startStopButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    saveSession.setEnabled(false);
                    startTimer();
                } else {
                    timerStarted = false;
                    startStopButton.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    saveSession.setEnabled(true);
                    pauseTimer();
                }
            }
        });

        saveSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadingSession readingSession = new ReadingSession(book.getId(), (int) (timeInMilliseconds / 1000));
                new ReadingSessionOperations(context).addReadingSession(readingSession);
                Utils.showToast(v.getContext(), getString(R.string.saved_successfully));

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (!prefs.getBoolean(DO_NOT_ASK_TO_UPDATE_PAGE, false)) {
                    showPageUpdateDialog();
                } else {
                    goBackToMainActivity();
                }
            }
        });

        init();
    }

    private void goBackToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private void init() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            book = new BookOperations(this).getBook(extras.getInt(SESSION_BOOK_ID));
            bookTitle.setText(book.getTitle());
            if (intent.hasExtra(START_TIME)) {
                startTime = extras.getLong(START_TIME);
                handler.postDelayed(timerThread, 0);
                timerStarted = true;
                startStopButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            }
        }
    }

    private void showPageUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //TODO: put this in utils and replace all the null ViewGroups
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_show_update_progress, viewGroup, false);
        final CheckBox doNotAskAgain = (CheckBox) view.findViewById(R.id.show_update_dont_ask_me_again);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BookUpdatePageDialog updatePageDialog = new BookUpdatePageDialog(context, book);
                dialog.dismiss();
                updatePageDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        goBackToMainActivity();
                    }
                });
                if (doNotAskAgain.isChecked()) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(DO_NOT_ASK_TO_UPDATE_PAGE, true);
                    editor.apply();
                }
                updatePageDialog.show();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (doNotAskAgain.isChecked()) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(DO_NOT_ASK_TO_UPDATE_PAGE, true);
                    editor.apply();
                }
                dialog.dismiss();
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                goBackToMainActivity();
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(timerThread, 0);
        showNotificationTimer();
    }

    private void pauseTimer() {
        timeSwapBuff += timeInMilliseconds;
        handler.removeCallbacks(timerThread);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private String formatTime(int hours, int minutes, int seconds) {
        return "" + String.format("%02d", hours)
                + ":" + String.format("%02d", minutes)
                + ":" + String.format("%02d", seconds);
    }

    private void showNotificationTimer() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, ReadingSessionActivity.class);
        intent.putExtra(SESSION_BOOK_ID, book.getId());
        intent.putExtra(START_TIME, startTime);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(this);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Readlist")
                        .setContentText("You have a reading session active")
                        .setSmallIcon(R.drawable.notification_icon)
                        .setAutoCancel(false)
                        .setContentIntent(pendingIntent)
                        .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
        goBackToMainActivity();
    }
}
