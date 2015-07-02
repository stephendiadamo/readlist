package com.s_diadamo.readlist.goal;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.s_diadamo.readlist.Utils;

import java.util.ArrayList;

public class GoalFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Goal>> {
    private View rootView;
    private GoalOperations goalOperations;
    private GoalAdapter selectedListViewAdapter;

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
            launchAddGoalFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_goal_actions, menu);
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
            case R.id.delete_goal:
                if (selectedListViewAdapter != null) {
                    Goal g = selectedListViewAdapter.getItem(info.position);
                    goalOperations.deleteGoal(g);
                    selectedListViewAdapter.remove(g);
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

        GoalAdapter pageGoalAdapter = new GoalAdapter(rootView.getContext(), R.layout.row_goal_element, pageGoals);
        GoalAdapter bookGoalAdapter = new GoalAdapter(rootView.getContext(), R.layout.row_goal_element, bookGoals);

        if (pageGoals.isEmpty()) {
            rootView.findViewById(R.id.page_goals_header).setVisibility(View.GONE);
        } else {
            ListView pageListView = (ListView) rootView.findViewById(R.id.goals_page_goals);
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
            ListView bookListView = (ListView) rootView.findViewById(R.id.goals_book_goals);
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
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Goal>> loader) {
    }
}

