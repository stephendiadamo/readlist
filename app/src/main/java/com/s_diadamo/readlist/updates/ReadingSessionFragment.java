package com.s_diadamo.readlist.updates;

import android.os.Bundle;
import android.os.SystemClock;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.s_diadamo.readlist.R;

public class ReadingSessionFragment extends Fragment {

    public static String SESSION_BOOK_ID = "session_book_id";
    boolean timerStarted = false;

    private Handler handler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private TextView timerTextView;

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

        return rootView;
    }

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(timerThread, 0);
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
}
