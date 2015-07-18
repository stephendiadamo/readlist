package com.s_diadamo.readlist.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.s_diadamo.readlist.general.MainActivity;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.sync.SyncData;

public class SettingsFragment extends Fragment {
    private static final String LOGIN = "LOGIN";
    private boolean userLoggedIn;

    private Context context;
    private TextView login;
    private TextView loggedInAsLabel;
    private TextView loggedInAs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        context = rootView.getContext();
        setHasOptionsMenu(false);

        login = (TextView) rootView.findViewById(R.id.settings_login);
        loggedInAsLabel = (TextView) rootView.findViewById(R.id.settings_logged_in_user_label);
        loggedInAs = (TextView) rootView.findViewById(R.id.settings_logged_in_user);
        TextView syncData = (TextView) rootView.findViewById(R.id.settings_sync);
        TextView emailUs = (TextView) rootView.findViewById(R.id.settings_email_us);
        TextView readList = (TextView) rootView.findViewById(R.id.settings_readlist);
        TextView readListVersion = (TextView) rootView.findViewById(R.id.settings_readlist_version);

        setLoginLabels();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userLoggedIn) {
                    Utils.logout(context);
                    setLoginLabels();
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
                launchAccountFragment();
            }
        });

        readListVersion.setText(MainActivity.PACKAGE_NAME);

        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + "stephen.diadamo@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Readlist");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Utils.showToast(context, "Please install an email client");
                }
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

    private void launchAccountFragment(){
        Fragment fragment = new AccountFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(LOGIN)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void setLoginLabels() {
        userLoggedIn = Utils.checkUserIsLoggedIn(getActivity());
        if (userLoggedIn) {
            login.setText(R.string.logout);
            loggedInAsLabel.setVisibility(View.VISIBLE);
            loggedInAs.setVisibility(View.VISIBLE);
            loggedInAs.setText(Utils.getUserName(context));
        } else {
            login.setText(R.string.login);
            loggedInAsLabel.setVisibility(View.GONE);
            loggedInAs.setVisibility(View.GONE);
        }
    }

}
