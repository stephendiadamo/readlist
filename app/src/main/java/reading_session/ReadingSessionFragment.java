package reading_session;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.book.Book;
import com.s_diadamo.readlist.general.Utils;


public class ReadingSessionFragment extends Fragment {

    public static String SESSION_BOOK_ID = "session_book_id";
    boolean timerStarted = false;

    private Handler handler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private TextView timerTextView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reading_session, container, false);
        context = rootView.getContext();


        TextView bookTitle = (TextView) rootView.findViewById(R.id.record_reading_activity_book_title);
        final ImageButton startStopButton = (ImageButton) rootView.findViewById(R.id.record_reading_activity_start_button);
        timerTextView = (TextView) rootView.findViewById(R.id.record_reading_activity_timer);
        Button settings = (Button) rootView.findViewById(R.id.record_reading_activity_settings);
        final Button saveSession = (Button) rootView.findViewById(R.id.record_reading_activity_save_session);

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
                // TODO: Save reading session
                Utils.showToast(context, getString(R.string.saved_successfully));
            }
        });

        return rootView;
    }

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(timerThread, 0);

        showNotificationTimer();
    }

    private void pauseTimer() {
        timeSwapBuff += timeInMilliseconds;
        handler.removeCallbacks(timerThread);
    }

    private String formatTime(int hours, int minutes, int seconds) {
        return "" + String.format("%02d", hours)
                + ":" + String.format("%02d", minutes)
                + ":" + String.format("%02d", seconds);
    }

    private void showNotificationTimer() {
        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(), intent, 0);

        Notification notification =
                new NotificationCompat.Builder(context)
                        .setContentTitle("Readlist")
                        .setContentText("You have a reading session active")
                        .setSmallIcon(R.drawable.notification_icon)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.ic_pause_circle_outline_black_48dp, "Pause", pendingIntent)
                        .build();

        notificationManager.notify(0, notification);

    }
}
