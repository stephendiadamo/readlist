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
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.book.BookUpdatePageDialog;
import com.s_diadamo.readlist.general.Analytics;
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;


public class ReadingSessionActivity extends AppCompatActivity {

    public static String SESSION_BOOK_ID = "session_book_id";
    private static String DO_NOT_ASK_TO_UPDATE_PAGE = "do_not_ask_to_update_page";
    public static String START_TIME = "start_time";
    private static int NOTIFICATION_ID = 0;

    boolean timerStarted = false;

    private Handler handler;
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private TextView timerTextView;
    private TextView bookTitle;
    private ImageButton startStopButton;
    private static Book book;
    private Context context;
    private boolean clickedNo = false;

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

        handler = new Handler();

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
                if (Utils.checkUserIsLoggedIn(context)) {
                    new SyncData(context).add(readingSession);
                }

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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        finish();
    }

    private void init() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int bookId = extras.getInt(SESSION_BOOK_ID);
            if (bookId == -1) {
                goBackToMainActivity();
            } else {
                book = new BookOperations(this).getBook(bookId);
                bookTitle.setText(book.getTitle());
                if (intent.hasExtra(START_TIME)) {
                    startTime = extras.getLong(START_TIME);
                    handler.postDelayed(timerThread, 0);
                    timerStarted = true;
                    startStopButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                }
            }
        }
    }

    private void init(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int bookId = extras.getInt(SESSION_BOOK_ID);
            if (bookId == -1) {
                goBackToMainActivity();
            } else {
                book = new BookOperations(this).getBook(bookId);
                bookTitle.setText(book.getTitle());
                if (intent.hasExtra(START_TIME)) {
                    startTime = extras.getLong(START_TIME);
                    handler.postDelayed(timerThread, 0);
                    timerStarted = true;
                    startStopButton.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                }
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
                clickedNo = true;
                dialog.dismiss();
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (clickedNo) {
                    goBackToMainActivity();
                }
            }
        });

        builder.setView(view);
        builder.show();
    }

    private void startTimer() {
        ParseAnalytics.trackEventInBackground(Analytics.STARTED_READING_SESSION);
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
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.you_have_an_active_reading_session))
                        .setSmallIcon(R.drawable.notification_icon)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setContentIntent(pendingIntent)
                        .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void saveReadingSessionData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SESSION_BOOK_ID, book.getId());
        editor.putLong(START_TIME, startTime);
        editor.apply();
    }

    public void clearReadingSessionData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(SESSION_BOOK_ID);
        editor.remove(START_TIME);
        editor.apply();
    }

    public void loadReadingSessionData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.contains(SESSION_BOOK_ID) && prefs.contains(START_TIME)) {
            Intent intent = new Intent(this, ReadingSessionActivity.class);
            intent.putExtra(SESSION_BOOK_ID, prefs.getInt(SESSION_BOOK_ID, -1));
            intent.putExtra(START_TIME, prefs.getLong(START_TIME, 0));
            init(intent);
        }
    }

    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
        if (timerStarted) {
            saveReadingSessionData();
        } else {
            clearReadingSessionData();
        }
        handler.removeCallbacks(timerThread);
        goBackToMainActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerStarted) {
            saveReadingSessionData();
        } else {
            clearReadingSessionData();
        }
        handler.removeCallbacks(timerThread);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReadingSessionData();
    }
}
