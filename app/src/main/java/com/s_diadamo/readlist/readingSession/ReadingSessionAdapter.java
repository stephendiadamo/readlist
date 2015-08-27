package com.s_diadamo.readlist.readingSession;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;

import java.util.ArrayList;

public class ReadingSessionAdapter extends ArrayAdapter<ReadingSession> {

    private final Context context;
    private final ArrayList<ReadingSession> readingSessions;

    public ReadingSessionAdapter(Context context, ArrayList<ReadingSession> readingSessions) {
        super(context, R.layout.row_reading_session, readingSessions);
        this.context = context;
        this.readingSessions = readingSessions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SessionHolder sessionHolder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.row_reading_session, parent, false);

            sessionHolder = new SessionHolder();
            sessionHolder.dateAdded = (TextView) row.findViewById(R.id.reading_session_row_date);
            sessionHolder.length = (TextView) row.findViewById(R.id.reading_session_length);

            row.setTag(sessionHolder);
        } else {
            sessionHolder = (SessionHolder) row.getTag();
        }

        ReadingSession readingSession = readingSessions.get(position);
        sessionHolder.dateAdded.setText(readingSession.getCleanDateAdded());
        sessionHolder.length.setText(Utils.formatTimeSpentReading(readingSession.getLengthOfTime()));

        return row;
    }

    static class SessionHolder {
        TextView dateAdded;
        TextView length;
    }
}
