package com.s_diadamo.readlist.goal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;

import java.util.ArrayList;

public class GoalFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Goal>> {
    private View rootView;
    private GoalOperations goalOperations;
    private GoalAdapter selectedListViewAdapter;
    private MenuItem hideCompletedGoals;
    private SharedPreferences prefs;
    private GoalAdapter bookGoalAdapter;
    private GoalAdapter pageGoalAdapter;
    private ListView bookListView;
    private ListView pageListView;
    private Context context;
    private boolean doneLoading = false;

    private static final String HIDE_COMPLETED_GOALS = "HIDE_COMPLETED_GOALS";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_goals, container, false);
        context = rootView.getContext();

        getLoaderManager().initLoader(GoalLoader.ID, null, this);
        setHasOptionsMenu(true);
        goalOperations = new GoalOperations(context);

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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        hideCompletedGoals = menu.findItem(R.id.hide_completed_goals);

        // Not sure why this would ever be null...
        if (hideCompletedGoals != null) {
            hideCompletedGoals.setChecked(prefs.getBoolean(HIDE_COMPLETED_GOALS, false));
        }
        if (doneLoading) {
            updateVisibleGoals();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();
        if (id == R.id.add_goal) {
            launchAddGoalFragment();
            return true;
        } else if (id == R.id.hide_completed_goals) {
            toggleHideCompletedGoals();
            updateVisibleGoals();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_general_delete, menu);
        try {
            ListView selectedListView = (ListView) v;
            selectedListViewAdapter = (GoalAdapter) selectedListView.getAdapter();
        } catch (ClassCastException e) {
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                if (selectedListViewAdapter != null) {
                    Goal goal = selectedListViewAdapter.getItem(info.position);
                    if (Utils.checkUserIsLoggedIn(context)) {
                        new SyncData(context).delete(goal);
                        goalOperations.deleteGoal(goal);
                    } else {
                        goal.delete();
                        goalOperations.updateGoal(goal);
                    }

                    selectedListViewAdapter.remove(goal);
                }
                return true;
        }

        return super.onContextItemSelected(item);
    }


    private void launchAddGoalFragment() {
        Fragment fragment = new GoalAddFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack("GOALS")
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public Loader<ArrayList<Goal>> onCreateLoader(int id, Bundle args) {
        return new GoalLoader(context);
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

        pageGoalAdapter = new GoalAdapter(context, pageGoals);
        bookGoalAdapter = new GoalAdapter(context, bookGoals);

        if (pageGoals.isEmpty()) {
            rootView.findViewById(R.id.page_goals_header).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.page_goals_header).setVisibility(View.VISIBLE);
            pageListView = (ListView) rootView.findViewById(R.id.goals_page_goals);
            pageListView.setAdapter(pageGoalAdapter);
            registerForContextMenu(pageListView);
            pageListView.setLongClickable(false);

            pageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getActivity().openContextMenu(view);
                }
            });

            Utils.setDynamicHeight(pageListView);
        }

        if (bookGoals.isEmpty()) {
            rootView.findViewById(R.id.book_goals_header).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.book_goals_header).setVisibility(View.VISIBLE);
            bookListView = (ListView) rootView.findViewById(R.id.goals_book_goals);
            bookListView.setAdapter(bookGoalAdapter);
            registerForContextMenu(bookListView);
            bookListView.setLongClickable(false);

            bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getActivity().openContextMenu(view);
                }
            });

            Utils.setDynamicHeight(bookListView);
        }
        if (hideCompletedGoals != null && hideCompletedGoals.isChecked()) {
            updateVisibleGoals();
        }
        doneLoading = true;
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Goal>> loader) {
    }

    private void toggleHideCompletedGoals() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HIDE_COMPLETED_GOALS, !hideCompletedGoals.isChecked());
        editor.apply();
        hideCompletedGoals.setChecked(!hideCompletedGoals.isChecked());
    }

    private void updateVisibleGoals() {
        if (hideCompletedGoals.isChecked()) {
            pageGoalAdapter.hideCompletedGoals();
            bookGoalAdapter.hideCompletedGoals();

            if (pageGoalAdapter.isEmpty()) {
                rootView.findViewById(R.id.page_goals_header).setVisibility(View.GONE);
            } else {
                rootView.findViewById(R.id.page_goals_header).setVisibility(View.VISIBLE);
            }
            if (bookGoalAdapter.isEmpty()) {
                rootView.findViewById(R.id.book_goals_header).setVisibility(View.GONE);
            } else {
                rootView.findViewById(R.id.book_goals_header).setVisibility(View.VISIBLE);
            }

            if (pageListView != null)
                Utils.setDynamicHeight(pageListView);
            if (bookListView != null)
                Utils.setDynamicHeight(bookListView);

        } else {
            getLoaderManager().initLoader(GoalLoader.ID, null, this);
        }
    }
}

