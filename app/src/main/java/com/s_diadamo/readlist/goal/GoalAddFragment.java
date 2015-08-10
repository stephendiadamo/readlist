package com.s_diadamo.readlist.goal;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.sync.SyncData;
import com.s_diadamo.readlist.general.Utils;

import java.util.Calendar;
import java.util.Date;


public class GoalAddFragment extends Fragment {

    private View rootView;
    private String startDate = "";
    private String endDate = "";
    private static final int START_DATE = 0;
    private static final int END_DATE = 1;
    private final Calendar calendar = Calendar.getInstance();
    private Date startDateLimit;
    private RadioGroup goalTypes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_goal, container, false);

        setHasOptionsMenu(true);

        Button startDateButton = (Button) rootView.findViewById(R.id.add_goal_start_date_button);
        Button endDateButton = (Button) rootView.findViewById(R.id.add_goal_end_date_button);

        TextView startDateTextView = (TextView) rootView.findViewById(R.id.add_goal_start_date);
        TextView endDateTextView = (TextView) rootView.findViewById(R.id.add_goal_end_date);

        startDateButton.setOnClickListener(setDateCalendarOnClick(startDateTextView, START_DATE));
        endDateButton.setOnClickListener(setDateCalendarOnClick(endDateTextView, END_DATE));

        final TextView goalAmountLabel = (TextView) rootView.findViewById(R.id.add_goal_amount_label);

        goalTypes = (RadioGroup) rootView.findViewById(R.id.add_goal_type);
        goalTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.add_goal_radio_type_book:
                        goalAmountLabel.setText(R.string.number_of_books);
                        break;
                    case R.id.add_goal_radio_type_page:
                        goalAmountLabel.setText(R.string.number_of_pages);
                        break;
                    default:
                        break;
                }
            }
        });

        return rootView;
    }

    private View.OnClickListener setDateCalendarOnClick(final TextView dateView, final int whichDate) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        fixDate(whichDate);
                        setDateString(Utils.parseDate(calendar.getTime()), whichDate);
                        dateView.setText(Utils.cleanDate(calendar.getTime()));
                    }
                };

                DatePickerDialog datePickerDialog = new DatePickerDialog(rootView.getContext(), dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                if (whichDate == END_DATE && startDateLimit != null) {
                    datePickerDialog.getDatePicker().setMinDate(startDateLimit.getTime());
                }

                datePickerDialog.show();
            }
        };
    }

    private void setDateString(String date, int whichDate) {
        switch (whichDate) {
            case START_DATE:
                startDate = date;
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
                startDateLimit = calendar.getTime();
                break;
            case END_DATE:
                endDate = date;
                break;
        }
    }

    private void fixDate(int which) {
        switch (which) {
            case 0:
                calendar.clear(Calendar.HOUR_OF_DAY);
                calendar.clear(Calendar.MINUTE);
                calendar.clear(Calendar.SECOND);
                calendar.clear(Calendar.MILLISECOND);
                break;
            case 1:
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_general_add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();

        if (id == R.id.add) {
            addGoal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addGoal() {
        EditText goalAmountEditText = ((EditText) rootView.findViewById(R.id.add_goal_amount));
        String goalAmount = goalAmountEditText.getText().toString();

        if (!startDate.isEmpty() && !endDate.isEmpty() && !goalAmount.isEmpty()) {
            int amount = Integer.parseInt(goalAmount);
            int goalTypeIndex = goalTypes.indexOfChild(rootView.findViewById(goalTypes.getCheckedRadioButtonId()));
            Goal goal = new Goal(goalTypeIndex, amount, startDate, endDate);
            new GoalOperations(rootView.getContext()).addGoal(goal);
            if (Utils.checkUserIsLoggedIn(rootView.getContext())) {
                new SyncData(rootView.getContext()).add(goal);
            }
            backToGoals();
        } else {
            Toast.makeText(rootView.getContext(), "Please fill out all fields", Toast.LENGTH_LONG).show();
        }
    }

    private void backToGoals() {
        Utils.hideKeyBoard(getActivity());
        Fragment fragment = new GoalFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
