package com.s_diadamo.readlist.goal;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.Utils;
import com.s_diadamo.readlist.book.BookLoader;

import java.util.ArrayList;

public class GoalsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Goal>> {
    private View rootView;
    private GoalOperations goalOperations;
    private GoalAdapter pageGoalAdapter;
    private GoalAdapter bookGoalAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_goals, container, false);

        getLoaderManager().initLoader(GoalLoader.ID, null, this);
        setHasOptionsMenu(true);
        goalOperations = new GoalOperations(rootView.getContext());

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getResources().getString(R.string.goals));
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_goals, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();
        if (id == R.id.add_goal) {
            Toast.makeText(rootView.getContext(), "Add Goal", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<Goal>> onCreateLoader(int id, Bundle args) {
        return new GoalLoader(rootView.getContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Goal>> loader, ArrayList<Goal> data) {

        ArrayList<Goal> bookGoals = new ArrayList<>();
        ArrayList<Goal> pageGoals = new ArrayList<>();

        for (Goal goal : data) {
            if (goal.getType() == Goal.BOOK_GOAL) {
                bookGoals.add(goal);
            } else {
                pageGoals.add(goal);
            }
        }

        pageGoalAdapter = new GoalAdapter(rootView.getContext(), R.layout.row_goal_element, pageGoals);
        bookGoalAdapter = new GoalAdapter(rootView.getContext(), R.layout.row_goal_element, bookGoals);

        if (pageGoals.isEmpty()) {
            rootView.findViewById(R.id.page_goals_header).setVisibility(View.GONE);
        } else {
            ListView pageListView = (ListView) rootView.findViewById(R.id.goals_page_goals);
            pageListView.setClickable(false);
            pageListView.setAdapter(pageGoalAdapter);
            Utils.setDynamicHeight(pageListView);
        }

        if (bookGoals.isEmpty()) {
            rootView.findViewById(R.id.book_goals_header).setVisibility(View.GONE);
        } else {
            ListView bookListView = (ListView) rootView.findViewById(R.id.goals_book_goals);
            bookListView.setClickable(false);
            bookListView.setAdapter(bookGoalAdapter);
            Utils.setDynamicHeight(bookListView);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Goal>> loader) {
    }
}

