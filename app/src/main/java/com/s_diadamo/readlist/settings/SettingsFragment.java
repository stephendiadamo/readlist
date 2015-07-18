package com.s_diadamo.readlist.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.LoginFragment;
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;

public class SettingsFragment extends Fragment {
    private static final String LOGIN = "LOGIN";

    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        context = rootView.getContext();
        setHasOptionsMenu(false);

        TextView login = (TextView) rootView.findViewById(R.id.settings_login);
        TextView loggedInAsLabel = (TextView) rootView.findViewById(R.id.settings_logged_in_user_label);
        TextView loggedInAs = (TextView) rootView.findViewById(R.id.settings_logged_in_user);
        TextView syncData = (TextView) rootView.findViewById(R.id.settings_sync);
        TextView emailUs = (TextView) rootView.findViewById(R.id.settings_email_us);
        TextView readList = (TextView) rootView.findViewById(R.id.settings_readlist);

        final boolean userLoggedIn = Utils.checkUserIsLoggedIn(getActivity());
        if (userLoggedIn) {
            login.setText(R.string.logout);
            loggedInAsLabel.setVisibility(View.VISIBLE);
            loggedInAs.setVisibility(View.GONE);
            loggedInAs.setText(Utils.getUserName(context));
        } else {
            login.setText(R.string.login);
            loggedInAsLabel.setVisibility(View.GONE);
            loggedInAs.setVisibility(View.GONE);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLoggedIn) {
                    Utils.logout(context);
                } else {
                    launchLoginFragment();
                }
            }
        });

        syncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userLoggedIn) {
                    Utils.showToast(context, "You must be logged in to sync data");
                } else {
                    launchSyncData();
                }
            }
        });

        readList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateLog();
            }
        });

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.settings);
        }

        return rootView;
    }

    private void launchLoginFragment() {
        Fragment fragment = new LoginFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(LOGIN)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void launchSyncData() {
        if (Utils.isNetworkAvailable(getActivity())) {
            SyncData syncData = new SyncData(context);
            syncData.syncAllData((AppCompatActivity) getActivity());
        } else {
            Utils.showToast(context, Utils.CHECK_INTERNET_MESSAGE);
        }
    }

    private void showUpdateLog() {


    }
}
